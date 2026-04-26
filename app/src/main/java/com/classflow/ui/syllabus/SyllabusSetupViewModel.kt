package com.classflow.ui.syllabus

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.classflow.data.ClassFlowDatabase
import com.classflow.data.model.Course
import com.classflow.data.model.Priority
import com.classflow.data.model.Task
import com.classflow.data.model.TaskType
import com.classflow.data.repository.CourseRepository
import com.classflow.data.repository.TaskRepository
import com.classflow.notification.ReminderScheduler
import com.classflow.ui.settings.SettingsRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

class SyllabusSetupViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository: TaskRepository
    private val courseRepository: CourseRepository

    val courses: LiveData<List<Course>>

    private val _drafts = MutableLiveData<List<SyllabusTaskDraft>>(emptyList())
    val drafts: LiveData<List<SyllabusTaskDraft>> get() = _drafts

    var selectedCourseId: Long = -1L
    var selectedCourseName: String = ""

    private val _creationSuccess = MutableLiveData<Int>()
    val creationSuccess: LiveData<Int> get() = _creationSuccess

    private val _submitError = MutableLiveData<String>()
    val submitError: LiveData<String> get() = _submitError

    private val _dateHint = MutableLiveData<String>()
    val dateHint: LiveData<String> get() = _dateHint

    private val settingsRepository = SettingsRepository(application)

    private fun defaultType(): TaskType {
        val name = settingsRepository.getUserSettings().defaultTaskType
        return TaskType.values().firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: TaskType.ASSIGNMENT
    }

    private fun defaultPriority(): Priority {
        val name = settingsRepository.getUserSettings().defaultPriority
        return Priority.values().firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: Priority.MEDIUM
    }

    private fun newDraft() = SyllabusTaskDraft(type = defaultType(), priority = defaultPriority())

    init {
        val db = ClassFlowDatabase.getDatabase(application)
        taskRepository = TaskRepository(db.taskDao())
        courseRepository = CourseRepository(db.courseDao())
        courses = courseRepository.allCourses
    }

    fun resetForCourse(courseId: Long, courseName: String) {
        selectedCourseId = courseId
        selectedCourseName = courseName
        _drafts.value = listOf(newDraft())
    }

    fun setDrafts(drafts: List<SyllabusTaskDraft>) {
        _drafts.value = drafts.toMutableList()
    }

    /** Appends generated drafts. If existing rows are all blank/empty, replaces them. */
    fun appendDrafts(drafts: List<SyllabusTaskDraft>) {
        val current = _drafts.value.orEmpty()
        val allEmpty = current.all { it.title.isBlank() && it.dueDate == null && it.description.isBlank() }
        _drafts.value = if (allEmpty) drafts.toMutableList()
                        else (current + drafts).toMutableList()
    }

    fun addEmptyRow() {
        val current = _drafts.value.orEmpty().toMutableList()
        current.add(newDraft())
        _drafts.value = current
    }

    fun removeRow(localId: String) {
        val current = _drafts.value.orEmpty().toMutableList()
        current.removeIf { it.localId == localId }
        if (current.isEmpty()) current.add(newDraft())
        _drafts.value = current
    }

    fun duplicateRow(localId: String) {
        val current = _drafts.value.orEmpty().toMutableList()
        val idx = current.indexOfFirst { it.localId == localId }
        if (idx >= 0) {
            val copy = current[idx].copy(
                localId = UUID.randomUUID().toString(),
                errors = emptyList(),
                warnings = emptyList()
            )
            current.add(idx + 1, copy)
            _drafts.value = current
        }
    }

    fun clearRow(localId: String) {
        updateDraft(localId) { newDraft().copy(localId = it.localId) }
    }

    fun updateTitle(localId: String, title: String) {
        updateDraft(localId) { it.copy(title = title) }
    }

    fun updateDescription(localId: String, description: String) {
        updateDraft(localId) { it.copy(description = description) }
    }

    fun updateDueDate(localId: String, dueDate: Long?) {
        updateDraft(localId) { it.copy(dueDate = dueDate) }
    }

    fun updateType(localId: String, type: TaskType) {
        updateDraft(localId) { it.copy(type = type) }
    }

    fun updatePriority(localId: String, priority: Priority) {
        updateDraft(localId) { it.copy(priority = priority) }
    }

    fun copyPreviousDueDate(localId: String) {
        val list = _drafts.value.orEmpty()
        val idx = list.indexOfFirst { it.localId == localId }
        if (idx > 0) {
            val prevDate = list[idx - 1].dueDate ?: return
            updateDueDate(localId, prevDate)
        }
    }

    fun addDaysToCurrentRow(localId: String, days: Int) {
        val list = _drafts.value.orEmpty()
        val draft = list.firstOrNull { it.localId == localId } ?: return
        val currentDate = draft.dueDate
        if (currentDate == null) {
            _dateHint.value = "Pick a due date first"
            return
        }
        val cal = Calendar.getInstance()
        cal.timeInMillis = currentDate
        cal.add(Calendar.DAY_OF_YEAR, days)
        updateDueDate(localId, cal.timeInMillis)
    }

    fun consumeDateHint() { _dateHint.value = "" }

    fun validateAndPrepareDrafts(): Boolean {
        val nowMs = System.currentTimeMillis()
        val seen = mutableSetOf<String>()

        val validated = _drafts.value.orEmpty().map { draft ->
            val errors = mutableListOf<String>()
            val warnings = mutableListOf<String>()

            if (draft.title.isBlank()) errors.add("Title is required")
            if (draft.dueDate == null) errors.add("Due date is required")
            else if (draft.dueDate < nowMs) warnings.add("Due date is in the past")

            val key = "${draft.title.trim().lowercase()}_${draft.dueDate}"
            if (draft.title.isNotBlank() && draft.dueDate != null && !seen.add(key)) {
                warnings.add("Duplicate of another row in this list")
            }

            draft.copy(errors = errors, warnings = warnings)
        }

        _drafts.value = validated
        return validated.none { it.errors.isNotEmpty() }
    }

    suspend fun checkExistingDuplicates() {
        if (selectedCourseId < 0) return
        val existing = taskRepository.getTasksForCourseOnce(selectedCourseId)
        val current = _drafts.value.orEmpty()
        val updated = current.map { draft ->
            val warnings = draft.warnings.toMutableList()
            if (draft.title.isNotBlank() && draft.dueDate != null) {
                val isDuplicate = existing.any { t ->
                    t.title.equals(draft.title.trim(), ignoreCase = true) &&
                        t.dueDate == draft.dueDate
                }
                if (isDuplicate && "Possible duplicate of existing task" !in warnings) {
                    warnings.add("Possible duplicate of existing task")
                }
            }
            draft.copy(warnings = warnings)
        }
        _drafts.value = updated
    }

    fun hasBlockingErrors(): Boolean = _drafts.value.orEmpty().any { it.errors.isNotEmpty() }

    fun getTotalWarnings(): Int = _drafts.value.orEmpty().sumOf { it.warnings.size }

    fun createTasks() = viewModelScope.launch {
        val courseId = selectedCourseId
        if (courseId < 0) return@launch
        val list = _drafts.value.orEmpty()
        if (list.any { it.errors.isNotEmpty() }) return@launch

        val tasks = list.map { draft ->
            Task(
                courseId = courseId,
                title = draft.title.trim(),
                description = draft.description.trim(),
                dueDate = draft.dueDate ?: 0L,
                isCompleted = false,
                priority = draft.priority,
                type = draft.type
            )
        }
        taskRepository.insertAll(tasks)
        ReminderScheduler.scheduleAllEligibleReminders(getApplication())
        _drafts.value = emptyList()
        _creationSuccess.value = tasks.size
    }

    fun consumeCreationSuccess() { _creationSuccess.value = 0 }
    fun consumeSubmitError() { _submitError.value = "" }

    fun submitValidRows() = viewModelScope.launch {
        val courseId = selectedCourseId
        if (courseId < 0) return@launch

        val validDrafts = _drafts.value.orEmpty().filter { it.title.isNotBlank() }
        if (validDrafts.isEmpty()) {
            _submitError.value = "Add at least one task before submitting."
            return@launch
        }

        val tasks = validDrafts.map { draft ->
            Task(
                courseId = courseId,
                title = draft.title.trim(),
                description = draft.description.trim(),
                dueDate = draft.dueDate ?: 0L,
                isCompleted = false,
                priority = draft.priority,
                type = draft.type
            )
        }
        taskRepository.insertAll(tasks)
        ReminderScheduler.scheduleAllEligibleReminders(getApplication())
        _drafts.value = emptyList()
        _creationSuccess.value = tasks.size
    }

    private fun updateDraft(localId: String, transform: (SyllabusTaskDraft) -> SyllabusTaskDraft) {
        val current = _drafts.value.orEmpty().toMutableList()
        val idx = current.indexOfFirst { it.localId == localId }
        if (idx >= 0) {
            current[idx] = transform(current[idx])
            _drafts.value = current
        }
    }
}
