package com.classflow.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.classflow.data.ClassFlowDatabase

class TaskReminderWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    companion object {
        const val KEY_TASK_ID = "taskId"
        const val KEY_COURSE_NAME = "courseName"
        const val KEY_REMINDER_TIMING = "reminderTiming"
    }

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(KEY_TASK_ID, -1L)
        if (taskId < 0) return Result.success()

        val task = ClassFlowDatabase.getDatabase(applicationContext)
            .taskDao()
            .getTaskById(taskId)

        if (task == null || task.isCompleted) return Result.success()

        val courseName = inputData.getString(KEY_COURSE_NAME) ?: ""
        val reminderTiming = inputData.getString(KEY_REMINDER_TIMING) ?: "1 day before"

        NotificationHelper.showTaskReminderNotification(
            applicationContext, taskId, task.title, courseName, reminderTiming
        )

        return Result.success()
    }
}
