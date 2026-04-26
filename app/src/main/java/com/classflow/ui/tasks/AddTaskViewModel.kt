package com.classflow.ui.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.classflow.data.ClassFlowDatabase
import com.classflow.data.model.Priority
import com.classflow.data.model.Task
import com.classflow.data.model.TaskType
import com.classflow.data.repository.TaskRepository
import com.classflow.notification.ReminderScheduler
import kotlinx.coroutines.launch

class AddTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    init {
        val db = ClassFlowDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao())
    }

    fun saveTask(
        courseId: Long,
        courseName: String,
        title: String,
        description: String,
        dueDate: Long,
        priority: Priority,
        type: TaskType
    ) = viewModelScope.launch {
        val task = Task(
            courseId = courseId,
            title = title,
            description = description,
            dueDate = dueDate,
            priority = priority,
            type = type
        )
        val id = repository.insert(task)
        ReminderScheduler.scheduleTaskReminder(getApplication(), task.copy(id = id), courseName)
    }
}
