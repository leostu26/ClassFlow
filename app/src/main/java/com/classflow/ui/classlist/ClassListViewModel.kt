package com.classflow.ui.classlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.classflow.data.ClassFlowDatabase
import com.classflow.data.model.Course
import com.classflow.data.repository.CourseRepository
import kotlinx.coroutines.launch

class ClassListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CourseRepository
    val allCourses: LiveData<List<Course>>

    init {
        val db = ClassFlowDatabase.getDatabase(application)
        repository = CourseRepository(db.courseDao())
        allCourses = repository.allCourses
    }

    fun deleteCourse(course: Course) = viewModelScope.launch {
        repository.delete(course)
    }
}
