package com.classflow.ui.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.classflow.data.ClassFlowDatabase
import com.classflow.data.model.Priority
import com.classflow.data.model.Task
import com.classflow.data.model.TaskType
import com.classflow.data.repository.TaskRepository
import com.classflow.notification.ReminderScheduler
import com.classflow.util.DateUtils
import kotlinx.coroutines.launch

enum class CompletionFilter { ALL, PENDING, COMPLETED }
enum class DueFilter { ALL, OVERDUE, DUE_TODAY, DUE_THIS_WEEK, NO_DATE }

class TasksViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    private val _courseId = MutableLiveData<Long>()
    private val _rawTasks: LiveData<List<Task>>
    val pendingCount: LiveData<Int>
    val totalCount: LiveData<Int>

    private val _searchQuery = MutableLiveData("")
    private val _priorityFilter = MutableLiveData<Priority?>(null)
    private val _typeFilter = MutableLiveData<TaskType?>(null)
    private val _completionFilter = MutableLiveData(CompletionFilter.ALL)
    private val _dueFilter = MutableLiveData(DueFilter.ALL)

    val priorityFilter: LiveData<Priority?> = _priorityFilter
    val typeFilter: LiveData<TaskType?> = _typeFilter
    val completionFilter: LiveData<CompletionFilter> = _completionFilter
    val dueFilter: LiveData<DueFilter> = _dueFilter

    val filteredTasks = MediatorLiveData<List<Task>>()
    private val _isFiltering = MutableLiveData(false)
    val isFiltering: LiveData<Boolean> = _isFiltering

    init {
        val db = ClassFlowDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao())

        _rawTasks = _courseId.switchMap { id -> repository.getTasksForCourse(id) }
        pendingCount = _courseId.switchMap { id -> repository.getPendingTaskCount(id) }
        totalCount = _courseId.switchMap { id -> repository.getTotalTaskCount(id) }

        fun rebuild() {
            val all = _rawTasks.value ?: emptyList()
            val query = _searchQuery.value ?: ""
            val priority = _priorityFilter.value
            val type = _typeFilter.value
            val completion = _completionFilter.value ?: CompletionFilter.ALL
            val due = _dueFilter.value ?: DueFilter.ALL
            val isFilter = query.isNotEmpty() || priority != null || type != null ||
                completion != CompletionFilter.ALL || due != DueFilter.ALL
            _isFiltering.value = isFilter
            filteredTasks.value = applyFilters(all, query, priority, type, completion, due)
        }

        filteredTasks.addSource(_rawTasks) { rebuild() }
        filteredTasks.addSource(_searchQuery) { rebuild() }
        filteredTasks.addSource(_priorityFilter) { rebuild() }
        filteredTasks.addSource(_typeFilter) { rebuild() }
        filteredTasks.addSource(_completionFilter) { rebuild() }
        filteredTasks.addSource(_dueFilter) { rebuild() }
    }

    fun setCourseId(courseId: Long) {
        _courseId.value = courseId
    }

    fun setSearchQuery(query: String) {
        if (_searchQuery.value != query) _searchQuery.value = query
    }

    fun setPriorityFilter(priority: Priority?) {
        if (_priorityFilter.value != priority) _priorityFilter.value = priority
    }

    fun setTypeFilter(type: TaskType?) {
        if (_typeFilter.value != type) _typeFilter.value = type
    }

    fun setCompletionFilter(filter: CompletionFilter) {
        if (_completionFilter.value != filter) _completionFilter.value = filter
    }

    fun setDueFilter(filter: DueFilter) {
        if (_dueFilter.value != filter) _dueFilter.value = filter
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _priorityFilter.value = null
        _typeFilter.value = null
        _completionFilter.value = CompletionFilter.ALL
        _dueFilter.value = DueFilter.ALL
    }

    fun toggleComplete(task: Task) = viewModelScope.launch {
        val newCompleted = !task.isCompleted
        repository.setCompleted(task.id, newCompleted)
        if (newCompleted) {
            ReminderScheduler.cancelTaskReminder(getApplication(), task.id)
        } else {
            ReminderScheduler.scheduleTaskReminderById(getApplication(), task.id)
        }
    }

    fun setTaskCompleted(taskId: Long, completed: Boolean) = viewModelScope.launch {
        repository.setCompleted(taskId, completed)
        if (completed) {
            ReminderScheduler.cancelTaskReminder(getApplication(), taskId)
        } else {
            ReminderScheduler.scheduleTaskReminderById(getApplication(), taskId)
        }
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.delete(task)
        ReminderScheduler.cancelTaskReminder(getApplication(), task.id)
    }

    private fun applyFilters(
        tasks: List<Task>,
        query: String,
        priority: Priority?,
        type: TaskType?,
        completion: CompletionFilter,
        due: DueFilter
    ): List<Task> {
        val now = System.currentTimeMillis()
        val todayStart = DateUtils.todayStart()
        val todayEnd = DateUtils.todayEnd()
        val weekEnd = DateUtils.daysFromNow(7)

        return tasks.filter { task ->
            (query.isEmpty() || task.title.contains(query, ignoreCase = true) ||
                task.description.contains(query, ignoreCase = true)) &&
            (priority == null || task.priority == priority) &&
            (type == null || task.type == type) &&
            when (completion) {
                CompletionFilter.ALL -> true
                CompletionFilter.PENDING -> !task.isCompleted
                CompletionFilter.COMPLETED -> task.isCompleted
            } &&
            when (due) {
                DueFilter.ALL -> true
                DueFilter.OVERDUE -> task.dueDate > 0L && task.dueDate < now && !task.isCompleted
                DueFilter.DUE_TODAY -> task.dueDate in todayStart..todayEnd
                DueFilter.DUE_THIS_WEEK -> task.dueDate in todayStart..weekEnd
                DueFilter.NO_DATE -> task.dueDate == 0L
            }
        }
    }
}
