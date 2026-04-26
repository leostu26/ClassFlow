package com.classflow.ui.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.classflow.data.ClassFlowDatabase
import com.classflow.data.model.TaskWithCourseInfo
import com.classflow.data.model.Priority
import com.classflow.data.model.TaskType
import com.classflow.data.repository.CourseRepository
import com.classflow.data.repository.TaskRepository
import com.classflow.ui.tasks.CompletionFilter
import com.classflow.ui.tasks.DueFilter
import com.classflow.util.DateUtils
import kotlinx.coroutines.launch

class SearchTasksViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ClassFlowDatabase.getDatabase(application)
    private val taskRepo = TaskRepository(db.taskDao())
    private val courseRepo = CourseRepository(db.courseDao())

    private var allTasks: List<TaskWithCourseInfo> = emptyList()

    private val _results = MutableLiveData<List<TaskWithCourseInfo>>(emptyList())
    val results: LiveData<List<TaskWithCourseInfo>> = _results

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isFiltering = MutableLiveData(false)
    val isFiltering: LiveData<Boolean> = _isFiltering

    private val _searchQuery = MutableLiveData("")

    private val _priorityFilter = MutableLiveData<Priority?>(null)
    private val _typeFilter = MutableLiveData<TaskType?>(null)
    private val _completionFilter = MutableLiveData(CompletionFilter.ALL)
    private val _dueFilter = MutableLiveData(DueFilter.ALL)

    val priorityFilter: LiveData<Priority?> = _priorityFilter
    val typeFilter: LiveData<TaskType?> = _typeFilter
    val completionFilter: LiveData<CompletionFilter> = _completionFilter
    val dueFilter: LiveData<DueFilter> = _dueFilter

    init {
        Log.d("SearchTasks", "ViewModel created")
        viewModelScope.launch { loadAllTasks() }
    }

    private suspend fun loadAllTasks() {
        _isLoading.value = true
        val tasks = taskRepo.getAllTasksOnce()
        val courses = courseRepo.getAllCoursesOnce()
        val courseMap = courses.associateBy { it.id }

        allTasks = tasks.map { task ->
            val course = courseMap[task.courseId]
            TaskWithCourseInfo(
                taskId = task.id,
                courseId = task.courseId,
                title = task.title,
                description = task.description,
                dueDate = task.dueDate,
                isCompleted = task.isCompleted,
                priority = task.priority,
                type = task.type,
                courseName = course?.name ?: "",
                courseColor = course?.color ?: "#4A90D9"
            )
        }

        Log.d("SearchTasks", "Loaded tasks count = ${allTasks.size}")
        _isLoading.value = false
        filterTasks()
    }

    private fun filterTasks() {
        val q = (_searchQuery.value ?: "").trim()
        val priority = _priorityFilter.value
        val type = _typeFilter.value
        val completion = _completionFilter.value ?: CompletionFilter.ALL
        val due = _dueFilter.value ?: DueFilter.ALL

        val now = System.currentTimeMillis()
        val todayStart = DateUtils.todayStart()
        val todayEnd = DateUtils.todayEnd()
        val weekEnd = DateUtils.daysFromNow(7)

        val filtered = allTasks.filter { task ->
            (q.isBlank() || task.title.contains(q, ignoreCase = true) ||
                task.description.contains(q, ignoreCase = true) ||
                task.courseName.contains(q, ignoreCase = true)) &&
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

        Log.d("SearchTasks", "query='$q' priority=$priority type=$type completion=$completion due=$due → ${filtered.size} results")

        _isFiltering.value = q.isNotBlank() || priority != null || type != null ||
            completion != CompletionFilter.ALL || due != DueFilter.ALL
        _results.value = filtered
    }

    fun setSearchQuery(query: String) {
        if (_searchQuery.value != query) {
            _searchQuery.value = query
            filterTasks()
        }
    }

    fun setPriorityFilter(priority: Priority?) {
        _priorityFilter.value = priority
        filterTasks()
    }

    fun setTypeFilter(type: TaskType?) {
        _typeFilter.value = type
        filterTasks()
    }

    fun setCompletionFilter(filter: CompletionFilter) {
        _completionFilter.value = filter
        filterTasks()
    }

    fun setDueFilter(filter: DueFilter) {
        _dueFilter.value = filter
        filterTasks()
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _priorityFilter.value = null
        _typeFilter.value = null
        _completionFilter.value = CompletionFilter.ALL
        _dueFilter.value = DueFilter.ALL
        filterTasks()
    }
}
