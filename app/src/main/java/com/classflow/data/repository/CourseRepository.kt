package com.classflow.data.repository

import androidx.lifecycle.LiveData
import com.classflow.data.dao.CourseDao
import com.classflow.data.model.Course

class CourseRepository(private val courseDao: CourseDao) {

    val allCourses: LiveData<List<Course>> = courseDao.getAllCourses()
    val courseCount: LiveData<Int> = courseDao.getCourseCount()

    suspend fun insert(course: Course): Long = courseDao.insertCourse(course)

    suspend fun update(course: Course) = courseDao.updateCourse(course)

    suspend fun delete(course: Course) = courseDao.deleteCourse(course)

    suspend fun getCourseById(id: Long): Course? = courseDao.getCourseById(id)
}
