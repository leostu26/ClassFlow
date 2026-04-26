package com.classflow.ui.workload

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.classflow.data.ClassFlowDatabase
import com.classflow.data.model.Priority
import com.classflow.data.model.TaskType
import com.classflow.data.model.TaskWithCourseInfo
import com.classflow.data.repository.TaskRepository
import java.util.Calendar

enum class WorkloadLevel(val label: String) {
    LIGHT("Light"),
    MODERATE("Moderate"),
    HEAVY("Heavy"),
    OVERLOADED("Overloaded")
}

data class DayWorkload(
    val dayMs: Long,
    val activeTasks: List<TaskWithCourseInfo>,
    val completedTasks: List<TaskWithCourseInfo>,
    val points: Int
)

data class WorkloadUiState(
    val weekStart: Long,
    val weekEnd: Long,
    val totalScore: Int,
    val workloadLevel: WorkloadLevel,
    val activeTasks: Int,
    val highPriorityCount: Int,
    val dueTodayCount: Int,
    val completedCount: Int,
    val overdueCount: Int,
    val mostLoadedDay: DayWorkload?,
    val typeBreakdown: Map<TaskType, Int>,
    val dailyBreakdown: List<DayWorkload>,
    val isEmpty: Boolean
)

class WorkloadViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val DAY_MS = 24 * 60 * 60 * 1000L

        fun todayMidnight(): Long = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        fun normalizeToDayStart(ts: Long): Long = Calendar.getInstance().apply {
            timeInMillis = ts
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // TODO: read weekStartDay from SettingsRepository and use Calendar.SUNDAY or Calendar.MONDAY as the anchor
        fun currentWeekStart(): Long = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            val offset = when (get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> 0
                Calendar.TUESDAY -> -1
                Calendar.WEDNESDAY -> -2
                Calendar.THURSDAY -> -3
                Calendar.FRIDAY -> -4
                Calendar.SATURDAY -> -5
                else -> -6 // SUNDAY
            }
            add(Calendar.DAY_OF_MONTH, offset)
        }.timeInMillis
    }

    private val repository = TaskRepository(ClassFlowDatabase.getDatabase(application).taskDao())
    private val _tasks = repository.allTasksWithCourseInfo

    private val _weekStart = MutableLiveData(currentWeekStart())
    val weekStart: LiveData<Long> = _weekStart

    private val _uiState = MediatorLiveData<WorkloadUiState>()
    val uiState: LiveData<WorkloadUiState> = _uiState

    init {
        fun rebuild() {
            val ws = _weekStart.value ?: return
            val tasks = _tasks.value ?: return
            _uiState.value = buildUiState(ws, tasks)
        }
        _uiState.addSource(_weekStart) { rebuild() }
        _uiState.addSource(_tasks) { rebuild() }
    }

    fun previousWeek() { _weekStart.value = (_weekStart.value ?: currentWeekStart()) - 7 * DAY_MS }
    fun nextWeek() { _weekStart.value = (_weekStart.value ?: currentWeekStart()) + 7 * DAY_MS }
    fun goToCurrentWeek() { _weekStart.value = currentWeekStart() }
    fun goToNextWeek() { _weekStart.value = currentWeekStart() + 7 * DAY_MS }

    private fun taskPoints(task: TaskWithCourseInfo): Int {
        val base = when (task.type) {
            TaskType.ASSIGNMENT -> 2
            TaskType.QUIZ -> 2
            TaskType.EXAM -> 5
            TaskType.PROJECT -> 5
            TaskType.DISCUSSION -> 2
            TaskType.RESPONSES -> 1
            TaskType.OTHER -> 1
        }
        val mult = when (task.priority) {
            Priority.LOW -> 1.0
            Priority.MEDIUM -> 1.5
            Priority.HIGH -> 2.0
        }
        return (base * mult).toInt()
    }

    private fun workloadLevel(score: Int): WorkloadLevel = when {
        score <= 5 -> WorkloadLevel.LIGHT
        score <= 14 -> WorkloadLevel.MODERATE
        score <= 24 -> WorkloadLevel.HEAVY
        else -> WorkloadLevel.OVERLOADED
    }

    private fun buildUiState(weekStart: Long, allTasks: List<TaskWithCourseInfo>): WorkloadUiState {
        val weekEndExclusive = weekStart + 7 * DAY_MS
        val todayMs = todayMidnight()

        val weekTasks = allTasks.filter { task ->
            task.dueDate > 0 && normalizeToDayStart(task.dueDate) in weekStart until weekEndExclusive
        }

        val activeTasks = weekTasks.filter { !it.isCompleted }
        val completedTasks = weekTasks.filter { it.isCompleted }

        val totalScore = activeTasks.sumOf { taskPoints(it) }
        val highPriorityCount = activeTasks.count { it.priority == Priority.HIGH }
        val dueTodayCount = activeTasks.count { normalizeToDayStart(it.dueDate) == todayMs }
        val overdueCount = activeTasks.count { normalizeToDayStart(it.dueDate) < todayMs }

        val typeBreakdown = TaskType.values().associateWith { type ->
            weekTasks.count { it.type == type }
        }

        val dailyBreakdown = (0..6).map { i ->
            val dayMs = weekStart + i * DAY_MS
            val dayActive = activeTasks.filter { normalizeToDayStart(it.dueDate) == dayMs }
            val dayCompleted = completedTasks.filter { normalizeToDayStart(it.dueDate) == dayMs }
            DayWorkload(dayMs, dayActive, dayCompleted, dayActive.sumOf { taskPoints(it) })
        }

        val mostLoadedDay = dailyBreakdown
            .filter { it.activeTasks.isNotEmpty() }
            .maxByOrNull { it.points }

        return WorkloadUiState(
            weekStart = weekStart,
            weekEnd = weekStart + 6 * DAY_MS,
            totalScore = totalScore,
            workloadLevel = workloadLevel(totalScore),
            activeTasks = activeTasks.size,
            highPriorityCount = highPriorityCount,
            dueTodayCount = dueTodayCount,
            completedCount = completedTasks.size,
            overdueCount = overdueCount,
            mostLoadedDay = mostLoadedDay,
            typeBreakdown = typeBreakdown,
            dailyBreakdown = dailyBreakdown,
            isEmpty = weekTasks.isEmpty()
        )
    }
}
