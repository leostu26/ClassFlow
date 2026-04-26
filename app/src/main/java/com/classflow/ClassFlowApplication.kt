package com.classflow

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.classflow.notification.NotificationHelper
import com.classflow.notification.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClassFlowApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val prefs = getSharedPreferences("classflow_settings", MODE_PRIVATE)
        val themeMode = prefs.getString("themeMode", "System") ?: "System"
        AppCompatDelegate.setDefaultNightMode(
            when (themeMode) {
                "Light" -> AppCompatDelegate.MODE_NIGHT_NO
                "Dark" -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )

        NotificationHelper.createNotificationChannel(this)

        CoroutineScope(Dispatchers.IO).launch {
            ReminderScheduler.scheduleAllEligibleReminders(this@ClassFlowApplication)
        }
    }
}
