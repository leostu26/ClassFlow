package com.classflow.ui.settings

data class UserSettings(
    val themeMode: String = "System",
    val weekStartDay: String = "Sunday",
    val showCompletedTasks: Boolean = true,
    val defaultTaskType: String = "Assignment",
    val defaultPriority: String = "Medium",
    val notificationsEnabled: Boolean = false,
    val reminderTiming: String = "1 day before"
)
