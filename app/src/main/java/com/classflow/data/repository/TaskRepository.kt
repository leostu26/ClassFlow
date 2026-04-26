package com.classflow.data.repository

import androidx.lifecycle.LiveData
import com.classflow.data.dao.TaskDao
import com.classflow.data.model.Task
import com.classflow.data.model.TaskWithCourseName
import com.classflow.data.model.TaskWithCourseInfo

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()
    val upcomingTasks: LiveData<List<Task>> = taskDao.getUpcomingTasks()
    val totalPendingCount: LiveData<Int> = taskDao.getTotalPendingCount()
    val allTasksWithCourseInfo: LiveData<List<TaskWithCourseInfo>> = taskDao.getAllTasksWithCourseInfo()
    val allTasksWithCourseInfoForSearch: LiveData<List<TaskWithCourseInfo>> = taskDao.getAllTasksWithCourseInfoForSearch()

    fun getTasksForCourse(courseId: Long): LiveData<List<Task>> =
        taskDao.getTasksForCourse(courseId)

    fun getPendingTaskCount(courseId: Long): LiveData<Int> =
        taskDao.getPendingTaskCount(courseId)

    fun getTotalTaskCount(courseId: Long): LiveData<Int> =
        taskDao.getTotalTaskCount(courseId)

    fun getTasksDueSoon(start: Long, end: Long): LiveData<List<Task>> =
        taskDao.getTasksDueSoon(start, end)

    fun getTasksWithCourseNameDueSoon(start: Long, end: Long): LiveData<List<TaskWithCourseName>> =
        taskDao.getTasksWithCourseNameDueSoon(start, end)

    fun getTasksWithCourseNameFuture(afterDate: Long): LiveData<List<TaskWithCourseName>> =
        taskDao.getTasksWithCourseNameFuture(afterDate)

    suspend fun getTaskById(taskId: Long): Task? = taskDao.getTaskById(taskId)

    suspend fun insert(task: Task): Long = taskDao.insertTask(task)

    suspend fun insertAll(tasks: List<Task>) = taskDao.insertTasks(tasks)

    suspend fun getTasksForCourseOnce(courseId: Long): List<Task> =
        taskDao.getTasksForCourseOnce(courseId)

    suspend fun getAllTasksOnce(): List<Task> = taskDao.getAllTasksOnce()

    suspend fun update(task: Task) = taskDao.updateTask(task)

    suspend fun delete(task: Task) = taskDao.deleteTask(task)

    suspend fun setCompleted(taskId: Long, completed: Boolean) =
        taskDao.setTaskCompleted(taskId, completed)

    suspend fun deleteAllTasks() = taskDao.deleteAllTasks()
}
