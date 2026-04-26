package com.classflow.ui.syllabus

import com.classflow.data.model.Priority
import com.classflow.data.model.TaskType
import java.util.Calendar

data class TaskConfig(
    var checked: Boolean = false,
    var titlePattern: String = "",
    var taskType: TaskType = TaskType.ASSIGNMENT,
    var priority: Priority = Priority.MEDIUM,
    var dueDow: Int = Calendar.SUNDAY,
    var moduleMode: ModuleMode = ModuleMode.EVERY,
    var moduleNumbers: String = ""
)
