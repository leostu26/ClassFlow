package com.classflow.ui.gantt

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.classflow.data.ClassFlowDatabase
import com.classflow.data.model.Priority
import com.classflow.data.model.TaskType
import com.classflow.data.model.TaskWithCourseInfo
import com.classflow.data.repository.TaskRepository
import com.classflow.notification.ReminderScheduler
import java.util.Calendar
import kotlinx.coroutines.launch

enum class ViewMode { ALL_TASKS, BY_CLASS }

data class WindowSummary(val total: Int, val highPriority: Int, val dueToday: Int)

sealed class GanttListItem {
    data class Header(
        val courseName: String,
        val courseColor: String,
        val startRange: Long,
        val endRange: Long
    ) : GanttListItem()

    data class TaskRow(
        val task: TaskWithCourseInfo,
        val startDate: Long,
        val windowStart: Long,
        val windowEnd: Long,
        val daysLabel: String
    ) : GanttListItem()

    data class AllTaskRow(
        val task: TaskWithCourseInfo,
        val startDate: Long,
        val windowStart: Long,
        val windowEnd: Long,
        val daysLabel: String
    ) : GanttListItem()
}

class GanttChartViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val DAY_MS = 24 * 60 * 60 * 1000L
        const val WINDOW_DAYS = 14

        fun todayMidnight(): Long = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        fun normalizeToDayStart(ts: Long): Long = Calendar.getInstance().apply {
            timeInMillis = ts
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private data class VisibleTask(val task: TaskWithCourseInfo, val startDate: Long)

    private val repository = TaskRepository(ClassFlowDatabase.getDatabase(application).taskDao())

    private val _windowStart = MutableLiveData(todayMidnight())
    val windowStart: LiveData<Long> = _windowStart

    private val _viewMode = MutableLiveData(ViewMode.ALL_TASKS)
    val viewMode: LiveData<ViewMode> = _viewMode

    private val _tasks = repository.allTasksWithCourseInfo

    private val _ganttItems = MediatorLiveData<List<GanttListItem>>()
    val ganttItems: LiveData<List<GanttListItem>> = _ganttItems

    private val _windowSummary = MutableLiveData<WindowSummary>()
    val windowSummary: LiveData<WindowSummary> = _windowSummary

    private val _dueCounts = MutableLiveData<Map<Long, Int>>(emptyMap())
    val dueCounts: LiveData<Map<Long, Int>> = _dueCounts

    init {
        fun rebuild() {
            val winStart = _windowStart.value ?: return
            val mode = _viewMode.value ?: return
            val tasks = _tasks.value ?: return
            val winEnd = winStart + WINDOW_DAYS * DAY_MS
            val visible = computeVisible(tasks, winStart, winEnd)
            _ganttItems.value = buildGanttItems(visible, winStart, winEnd, mode)
            _windowSummary.value = computeSummary(visible, winStart)
            _dueCounts.value = computeDueCounts(visible, winStart)
        }
        _ganttItems.addSource(_windowStart) { rebuild() }
        _ganttItems.addSource(_viewMode) { rebuild() }
        _ganttItems.addSource(_tasks) { rebuild() }
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    fun previousWindow() {
        _windowStart.value = (_windowStart.value ?: todayMidnight()) - WINDOW_DAYS * DAY_MS
    }

    fun nextWindow() {
        _windowStart.value = (_windowStart.value ?: todayMidnight()) + WINDOW_DAYS * DAY_MS
    }

    fun goToToday() {
        _windowStart.value = todayMidnight()
    }

    // TODO: read weekStartDay from SettingsRepository to align the 14-day window header to Sunday or Monday

    fun setTaskCompleted(taskId: Long, completed: Boolean) = viewModelScope.launch {
        repository.setCompleted(taskId, completed)
        if (completed) {
            ReminderScheduler.cancelTaskReminder(getApplication(), taskId)
        } else {
            ReminderScheduler.scheduleTaskReminderById(getApplication(), taskId)
        }
    }

    fun setViewMode(mode: ViewMode) {
        if (_viewMode.value != mode) _viewMode.value = mode
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun estimatedStartDate(task: TaskWithCourseInfo): Long {
        val days: Long = when (task.type) {
            TaskType.PROJECT -> when (task.priority) {
                Priority.HIGH -> 10L; Priority.MEDIUM -> 7L; Priority.LOW -> 4L
            }
            TaskType.ASSIGNMENT -> when (task.priority) {
                Priority.HIGH -> 5L; Priority.MEDIUM -> 3L; Priority.LOW -> 2L
            }
            TaskType.QUIZ -> when (task.priority) {
                Priority.HIGH -> 3L; Priority.MEDIUM -> 2L; Priority.LOW -> 1L
            }
            TaskType.EXAM -> when (task.priority) {
                Priority.HIGH -> 7L; Priority.MEDIUM -> 5L; Priority.LOW -> 3L
            }
            TaskType.DISCUSSION -> when (task.priority) {
                Priority.HIGH -> 3L; Priority.MEDIUM -> 2L; Priority.LOW -> 1L
            }
            TaskType.RESPONSES -> when (task.priority) {
                Priority.HIGH -> 2L; Priority.MEDIUM -> 1L; Priority.LOW -> 1L
            }
            TaskType.OTHER -> when (task.priority) {
                Priority.HIGH -> 4L; Priority.MEDIUM -> 2L; Priority.LOW -> 1L
            }
        }
        return task.dueDate - days * DAY_MS
    }

    private fun overlapsWindow(startDate: Long, dueDate: Long, winStart: Long, winEnd: Long): Boolean =
        startDate < winEnd && dueDate + DAY_MS > winStart

    fun daysLabel(task: TaskWithCourseInfo): String {
        if (task.isCompleted) return "Completed"
        if (task.dueDate == 0L) return ""
        val diff = (task.dueDate - todayMidnight()) / DAY_MS
        return when {
            diff == 0L -> "Due today"
            diff > 0L -> "${diff}d left"
            diff == -1L -> "Overdue"
            else -> "${-diff}d late"
        }
    }

    private fun allTaskSortKey(row: VisibleTask): Int {
        val todayMs = todayMidnight()
        return when {
            row.task.dueDate in todayMs until todayMs + DAY_MS -> 0   // due today
            !row.task.isCompleted && row.task.dueDate < todayMs -> 1  // overdue incomplete
            else -> 2
        }
    }

    // ── Compute helpers ───────────────────────────────────────────────────────

    private fun computeVisible(
        tasks: List<TaskWithCourseInfo>, winStart: Long, winEnd: Long
    ): List<VisibleTask> = tasks
        .map { VisibleTask(it, estimatedStartDate(it)) }
        .filter { overlapsWindow(it.startDate, it.task.dueDate, winStart, winEnd) }

    private fun computeSummary(visible: List<VisibleTask>, winStart: Long): WindowSummary {
        val todayMs = todayMidnight()
        return WindowSummary(
            total = visible.size,
            highPriority = visible.count { it.task.priority == Priority.HIGH },
            dueToday = visible.count { it.task.dueDate in todayMs until todayMs + DAY_MS }
        )
    }

    private fun computeDueCounts(visible: List<VisibleTask>, winStart: Long): Map<Long, Int> {
        val counts = mutableMapOf<Long, Int>()
        visible.forEach { row ->
            val dueMidnight = normalizeToDayStart(row.task.dueDate)
            val slot = (dueMidnight - winStart) / DAY_MS
            if (slot in 0 until WINDOW_DAYS) {
                val key = winStart + slot * DAY_MS
                counts[key] = (counts[key] ?: 0) + 1
            }
        }
        return counts
    }

    // ── List builders ─────────────────────────────────────────────────────────

    private fun buildGanttItems(
        visible: List<VisibleTask>, winStart: Long, winEnd: Long, mode: ViewMode
    ): List<GanttListItem> {
        if (visible.isEmpty()) return emptyList()
        return if (mode == ViewMode.ALL_TASKS)
            buildAllTasksList(visible, winStart, winEnd)
        else
            buildByClassList(visible, winStart, winEnd)
    }

    private fun buildAllTasksList(
        rows: List<VisibleTask>, winStart: Long, winEnd: Long
    ): List<GanttListItem> = rows
        .sortedWith(compareBy(
            { allTaskSortKey(it) },
            { it.task.dueDate },
            { -it.task.priority.ordinal },
            { it.task.type.ordinal }
        ))
        .map { row ->
            GanttListItem.AllTaskRow(
                task = row.task,
                startDate = row.startDate,
                windowStart = winStart,
                windowEnd = winEnd,
                daysLabel = daysLabel(row.task)
            )
        }

    private fun buildByClassList(
        rows: List<VisibleTask>, winStart: Long, winEnd: Long
    ): List<GanttListItem> {
        val result = mutableListOf<GanttListItem>()
        rows.groupBy { it.task.courseId }.forEach { (_, courseRows) ->
            val first = courseRows.first().task
            result += GanttListItem.Header(
                courseName = first.courseName,
                courseColor = first.courseColor,
                startRange = maxOf(courseRows.minOf { it.startDate }, winStart),
                endRange = minOf(courseRows.maxOf { it.task.dueDate }, winEnd - DAY_MS)
            )
            courseRows.forEach { row ->
                result += GanttListItem.TaskRow(
                    task = row.task,
                    startDate = row.startDate,
                    windowStart = winStart,
                    windowEnd = winEnd,
                    daysLabel = daysLabel(row.task)
                )
            }
        }
        return result
    }
}
