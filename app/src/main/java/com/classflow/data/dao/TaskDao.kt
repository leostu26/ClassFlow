package com.classflow.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.classflow.data.model.Task
import com.classflow.data.model.TaskWithCourseName
import com.classflow.data.model.TaskWithCourseInfo

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE courseId = :courseId ORDER BY dueDate ASC")
    fun getTasksForCourse(courseId: Long): LiveData<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC LIMIT 5")
    fun getUpcomingTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :start AND :end AND isCompleted = 0 ORDER BY dueDate ASC")
    fun getTasksDueSoon(start: Long, end: Long): LiveData<List<Task>>

    @Query("""
        SELECT t.id as taskId, t.courseId, t.title, t.description, t.dueDate,
               t.isCompleted, t.priority, t.type, c.name as courseName, c.color as courseColor
        FROM tasks t
        INNER JOIN courses c ON t.courseId = c.id
        WHERE t.dueDate BETWEEN :start AND :end AND t.isCompleted = 0
        ORDER BY t.dueDate ASC
    """)
    fun getTasksWithCourseNameDueSoon(start: Long, end: Long): LiveData<List<TaskWithCourseName>>

    @Query("""
        SELECT t.id as taskId, t.courseId, t.title, t.description, t.dueDate,
               t.isCompleted, t.priority, t.type, c.name as courseName, c.color as courseColor
        FROM tasks t
        INNER JOIN courses c ON t.courseId = c.id
        WHERE t.dueDate > :afterDate AND t.isCompleted = 0
        ORDER BY t.dueDate ASC
    """)
    fun getTasksWithCourseNameFuture(afterDate: Long): LiveData<List<TaskWithCourseName>>

    @Query("""
        SELECT t.id as taskId, t.courseId, t.title, t.description, t.dueDate,
               t.isCompleted, t.priority, t.type, c.name as courseName, c.color as courseColor
        FROM tasks t
        INNER JOIN courses c ON t.courseId = c.id
        WHERE t.dueDate > 0
        ORDER BY c.name ASC, t.dueDate ASC
    """)
    fun getAllTasksWithCourseInfo(): LiveData<List<TaskWithCourseInfo>>

    @Query("""
        SELECT t.id as taskId, t.courseId, t.title, t.description, t.dueDate,
               t.isCompleted, t.priority, t.type, c.name as courseName, c.color as courseColor
        FROM tasks t
        INNER JOIN courses c ON t.courseId = c.id
        ORDER BY c.name ASC, t.dueDate ASC
    """)
    fun getAllTasksWithCourseInfoForSearch(): LiveData<List<TaskWithCourseInfo>>

    @Query("SELECT COUNT(*) FROM tasks WHERE courseId = :courseId AND isCompleted = 0")
    fun getPendingTaskCount(courseId: Long): LiveData<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE courseId = :courseId")
    fun getTotalTaskCount(courseId: Long): LiveData<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getTotalPendingCount(): LiveData<Int>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    @Query("SELECT * FROM tasks WHERE courseId = :courseId")
    suspend fun getTasksForCourseOnce(courseId: Long): List<Task>

    @Query("SELECT * FROM tasks ORDER BY courseId ASC, dueDate ASC")
    suspend fun getAllTasksOnce(): List<Task>

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :taskId")
    suspend fun setTaskCompleted(taskId: Long, completed: Boolean)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
