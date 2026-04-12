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

    // Due today: today 00:00 → today 23:59
    val tasksDueToday: LiveData<List<TaskWithCourseName>>

    // Due this week: tomorrow 00:00 → 7 days from now
    val tasksDueThisWeek: LiveData<List<TaskWithCourseName>>

    // Future: beyond 7 days from now
    val tasksFuture: LiveData<List<TaskWithCourseName>>

    init {
        val db = ClassFlowDatabase.getDatabase(application)
        courseRepository = CourseRepository(db.courseDao())
        taskRepository = TaskRepository(db.taskDao())

        courseCount = courseRepository.courseCount
        pendingTaskCount = taskRepository.totalPendingCount

        tasksDueToday = taskRepository.getTasksWithCourseNameDueSoon(
            DateUtils.todayStart(),
            DateUtils.todayEnd()
        )
        tasksDueThisWeek = taskRepository.getTasksWithCourseNameDueSoon(
            DateUtils.tomorrowStart(),
            DateUtils.daysFromNow(7)
        )
        tasksFuture = taskRepository.getTasksWithCourseNameFuture(
            DateUtils.daysFromNow(7)
        )
    }
}
