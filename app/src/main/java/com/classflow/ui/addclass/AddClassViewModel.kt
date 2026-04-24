package com.classflow.ui.addclass

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.classflow.data.ClassFlowDatabase
import com.classflow.data.model.Course
import com.classflow.data.repository.CourseRepository
import kotlinx.coroutines.launch

class AddClassViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CourseRepository

    init {
        val db = ClassFlowDatabase.getDatabase(application)
        repository = CourseRepository(db.courseDao())
    }

    fun saveCourse(
        name: String,
        code: String,
        instructor: String,
        schedule: String,
        room: String,
        color: String
    ) = viewModelScope.launch {
        val course = Course(
            name = name,
            code = code,
            instructor = instructor,
            schedule = schedule,
            room = room,
            color = color
        )
        repository.insert(course)
    }
}
