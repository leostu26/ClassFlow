package com.classflow.ui.settings

import android.content.Context

class SettingsRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getUserSettings(): UserSettings = UserSettings(
        themeMode = prefs.getString(KEY_THEME_MODE, "System") ?: "System",
        weekStartDay = prefs.getString(KEY_WEEK_START_DAY, "Sunday") ?: "Sunday",
        showCompletedTasks = prefs.getBoolean(KEY_SHOW_COMPLETED, true),
        defaultTaskType = prefs.getString(KEY_DEFAULT_TASK_TYPE, "Assignment") ?: "Assignment",
        defaultPriority = prefs.getString(KEY_DEFAULT_PRIORITY, "Medium") ?: "Medium",
        notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, false),
        reminderTiming = prefs.getString(KEY_REMINDER_TIMING, "1 day before") ?: "1 day before"
    )

    fun updateThemeMode(mode: String) = prefs.edit().putString(KEY_THEME_MODE, mode).apply()

    fun updateWeekStartDay(day: String) = prefs.edit().putString(KEY_WEEK_START_DAY, day).apply()

    fun updateShowCompletedTasks(show: Boolean) = prefs.edit().putBoolean(KEY_SHOW_COMPLETED, show).apply()

    fun updateDefaultTaskType(taskType: String) = prefs.edit().putString(KEY_DEFAULT_TASK_TYPE, taskType).apply()

    fun updateDefaultPriority(priority: String) = prefs.edit().putString(KEY_DEFAULT_PRIORITY, priority).apply()

    fun updateNotificationsEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()

    fun updateReminderTiming(timing: String) = prefs.edit().putString(KEY_REMINDER_TIMING, timing).apply()

    fun restoreDefaultSettings() {
        prefs.edit()
            .putString(KEY_THEME_MODE, "System")
            .putString(KEY_WEEK_START_DAY, "Sunday")
            .putBoolean(KEY_SHOW_COMPLETED, true)
            .putString(KEY_DEFAULT_TASK_TYPE, "Assignment")
            .putString(KEY_DEFAULT_PRIORITY, "Medium")
            .putBoolean(KEY_NOTIFICATIONS_ENABLED, false)
            .putString(KEY_REMINDER_TIMING, "1 day before")
            .apply()
    }

    companion object {
        const val PREFS_NAME = "classflow_settings"
        const val KEY_THEME_MODE = "themeMode"
        const val KEY_WEEK_START_DAY = "weekStartDay"
        const val KEY_SHOW_COMPLETED = "showCompletedTasks"
        const val KEY_DEFAULT_TASK_TYPE = "defaultTaskType"
        const val KEY_DEFAULT_PRIORITY = "defaultPriority"
        const val KEY_NOTIFICATIONS_ENABLED = "notificationsEnabled"
        const val KEY_REMINDER_TIMING = "reminderTiming"
    }
}
