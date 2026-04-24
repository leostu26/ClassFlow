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
    val courseName: String
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
