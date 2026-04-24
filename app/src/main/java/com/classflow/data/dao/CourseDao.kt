package com.classflow.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.classflow.data.model.Course

@Dao
interface CourseDao {

    @Query("SELECT * FROM courses ORDER BY name ASC")
    fun getAllCourses(): LiveData<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: Long): Course?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course): Long

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("SELECT COUNT(*) FROM courses")
    fun getCourseCount(): LiveData<Int>
}
