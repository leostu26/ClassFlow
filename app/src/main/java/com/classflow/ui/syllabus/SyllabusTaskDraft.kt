package com.classflow.ui.syllabus

import com.classflow.data.model.Priority
import com.classflow.data.model.TaskType
import java.util.UUID

data class SyllabusTaskDraft(
    val localId: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val dueDate: Long? = null,
    val type: TaskType = TaskType.ASSIGNMENT,
    val priority: Priority = Priority.MEDIUM,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
)
