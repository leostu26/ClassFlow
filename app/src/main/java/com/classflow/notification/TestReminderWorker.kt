package com.classflow.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class TestReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        NotificationHelper.showTestNotification(
            applicationContext,
            "This reminder was scheduled 10 seconds ago."
        )
        return Result.success()
    }
}
