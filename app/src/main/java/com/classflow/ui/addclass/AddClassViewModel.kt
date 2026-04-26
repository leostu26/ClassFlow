package com.classflow.ui.addclass

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.classflow.data.ClassFlowDatabase
import com.classflow.data.model.Course
import com.classflow.data.repository.CourseRepository
import kotlinx.coroutines.launch

class AddClassViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CourseRepository

    private val _existingColors = MutableLiveData<List<String>>(emptyList())
    val existingColors: LiveData<List<String>> = _existingColors

    init {
        val db = ClassFlowDatabase.getDatabase(application)
        repository = CourseRepository(db.courseDao())
    }

    fun loadExistingColors() = viewModelScope.launch {
        _existingColors.value = repository.getAllCoursesOnce().map { it.color }
    }

    fun saveCourse(
        name: String,
        code: String,
        instructor: String,
        schedule: String,
        room: String,
        color: String,
        classMode: String = "In Person",
        meetingLink: String = "",
        platform: String = ""
    ) = viewModelScope.launch {
        repository.insert(
            Course(
                name = name,
                code = code,
                instructor = instructor,
                schedule = schedule,
                room = room,
                color = color,
                classMode = classMode,
                meetingLink = meetingLink,
                platform = platform
            )
        )
    }
}
