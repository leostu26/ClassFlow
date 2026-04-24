package com.classflow.data.model

data class TaskWithCourseInfo(
    val taskId: Long,
    val courseId: Long,
    val title: String,
    val description: String,
    val dueDate: Long,
    val isCompleted: Boolean,
    val priority: Priority,
    val type: TaskType,
    val courseName: String,
    val courseColor: String
)
