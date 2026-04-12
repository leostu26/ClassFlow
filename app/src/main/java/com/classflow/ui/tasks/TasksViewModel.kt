package com.classflow.ui.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.classflow.data.ClassFlowDatabase
import com.classflow.data.model.Task
import com.classflow.data.repository.TaskRepository
import kotlinx.coroutines.launch

class TasksViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    private val _courseId = MutableLiveData<Long>()
    val tasks: LiveData<List<Task>>
    val pendingCount: LiveData<Int>
    val totalCount: LiveData<Int>

    init {
        val db = ClassFlowDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao())

        tasks = _courseId.switchMap { id -> repository.getTasksForCourse(id) }
        pendingCount = _courseId.switchMap { id -> repository.getPendingTaskCount(id) }
        totalCount = _courseId.switchMap { id -> repository.getTotalTaskCount(id) }
    }

    fun setCourseId(courseId: Long) {
        _courseId.value = courseId
    }

    fun toggleComplete(task: Task) = viewModelScope.launch {
        repository.setCompleted(task.id, !task.isCompleted)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }
}
