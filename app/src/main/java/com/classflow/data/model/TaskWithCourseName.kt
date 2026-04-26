package com.classflow.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithCourseName(
    val taskId: Long,
    val courseId: Long,
    val title: String,
    val description: String,
    val dueDate: Long,
    val isCompleted: Boolean,
    val priority: Priority,
    val type: TaskType,
    val courseName: String,
    val courseColor: String = "#4A90D9"
) {
    fun toTask() = Task(
        id = taskId,
        courseId = courseId,
        title = title,
        description = description,
        dueDate = dueDate,
        isCompleted = isCompleted,
        priority = priority,
        type = type
    )
}
