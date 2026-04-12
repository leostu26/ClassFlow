package com.classflow.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.classflow.data.ClassFlowDatabase
import com.classflow.data.model.TaskWithCourseName
import com.classflow.data.repository.CourseRepository
import com.classflow.data.repository.TaskRepository
import com.classflow.util.DateUtils

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val courseRepository: CourseRepository
    private val taskRepository: TaskRepository

    val courseCount: LiveData<Int>
    val pendingTaskCount: LiveData<Int>
    val tasksDueSoon: LiveData<List<TaskWithCourseName>>

    init {
        val db = ClassFlowDatabase.getDatabase(application)
        courseRepository = CourseRepository(db.courseDao())
        taskRepository = TaskRepository(db.taskDao())

        courseCount = courseRepository.courseCount
        pendingTaskCount = taskRepository.totalPendingCount
        tasksDueSoon = taskRepository.getTasksWithCourseNameDueSoon(
            DateUtils.todayStart(),
            DateUtils.daysFromNow(7)
        )
    }
}
