package com.classflow.ui.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.classflow.data.ClassFlowDatabase
import com.classflow.data.model.Priority
import com.classflow.data.model.Task
import com.classflow.data.model.TaskType
import com.classflow.data.repository.TaskRepository
import com.classflow.notification.ReminderScheduler
import kotlinx.coroutines.launch

class TaskDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    private val _task = MutableLiveData<Task?>()
    val task: LiveData<Task?> = _task

    init {
        val db = ClassFlowDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao())
    }

    fun loadTask(taskId: Long) = viewModelScope.launch {
        _task.value = repository.getTaskById(taskId)
    }

    fun saveTask(
        taskId: Long,
        courseId: Long,
        courseName: String,
        title: String,
        description: String,
        dueDate: Long,
        priority: Priority,
        type: TaskType,
        isCompleted: Boolean
    ) = viewModelScope.launch {
        val task = Task(
            id = taskId,
            courseId = courseId,
            title = title,
            description = description,
            dueDate = dueDate,
            priority = priority,
            type = type,
            isCompleted = isCompleted
        )
        repository.update(task)
        if (isCompleted || dueDate == 0L) {
            ReminderScheduler.cancelTaskReminder(getApplication(), taskId)
        } else {
            ReminderScheduler.scheduleTaskReminder(getApplication(), task, courseName)
        }
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.delete(task)
        ReminderScheduler.cancelTaskReminder(getApplication(), task.id)
    }
}
