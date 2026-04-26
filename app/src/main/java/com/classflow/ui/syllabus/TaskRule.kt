package com.classflow.ui.syllabus

import com.classflow.data.model.Priority
import com.classflow.data.model.TaskType
import java.util.Calendar

data class TaskRule(
    var enabled: Boolean = true,
    var titlePattern: String = "",
    var type: TaskType = TaskType.ASSIGNMENT,
    var priority: Priority = Priority.MEDIUM,
    var dueDow: Int = Calendar.SUNDAY,
    var moduleMode: ModuleMode = ModuleMode.EVERY,
    var specificModules: String = ""
)

enum class ModuleMode(val label: String) {
    EVERY("Every module"),
    LAST_ONLY("Last module only"),
    SPECIFIC("Specific modules"),
    SINGLE("Single module")
}
