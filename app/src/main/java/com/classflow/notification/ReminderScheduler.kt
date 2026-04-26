package com.classflow.notification

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.classflow.data.ClassFlowDatabase
import com.classflow.data.model.Task
import com.classflow.ui.settings.SettingsRepository
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    private const val TAG = "task_reminder"

    fun scheduleTaskReminder(context: Context, task: Task, courseName: String) {
        val settings = SettingsRepository(context).getUserSettings()
        if (!settings.notificationsEnabled) return

        cancelTaskReminder(context, task.id)

        if (task.isCompleted || task.dueDate == 0L) return

        val reminderMs = calculateReminderTime(task.dueDate, settings.reminderTiming)
        val delayMs = reminderMs - System.currentTimeMillis()
        if (delayMs <= 0) return

        val data = workDataOf(
            TaskReminderWorker.KEY_TASK_ID to task.id,
            TaskReminderWorker.KEY_COURSE_NAME to courseName,
            TaskReminderWorker.KEY_REMINDER_TIMING to settings.reminderTiming
        )

        val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .addTag(TAG)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            workName(task.id),
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    suspend fun scheduleTaskReminderById(context: Context, taskId: Long) {
        val settings = SettingsRepository(context).getUserSettings()
        if (!settings.notificationsEnabled) return

        val db = ClassFlowDatabase.getDatabase(context)
        val task = db.taskDao().getTaskById(taskId) ?: return
        val course = db.courseDao().getCourseById(task.courseId)
        scheduleTaskReminder(context, task, course?.name ?: "")
    }

    fun cancelTaskReminder(context: Context, taskId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork(workName(taskId))
    }

    suspend fun scheduleAllEligibleReminders(context: Context) {
        val settings = SettingsRepository(context).getUserSettings()
        if (!settings.notificationsEnabled) return

        val db = ClassFlowDatabase.getDatabase(context)
        val tasks = db.taskDao().getAllTasksOnce()
        val courses = db.courseDao().getAllCoursesOnce()
        val courseMap = courses.associateBy({ it.id }, { it.name })

        tasks.filter { !it.isCompleted && it.dueDate > 0 }.forEach { task ->
            scheduleTaskReminder(context, task, courseMap[task.courseId] ?: "")
        }
    }

    fun cancelAllTaskReminders(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
    }

    fun calculateReminderTime(dueDate: Long, reminderTiming: String): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = dueDate
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        when (reminderTiming) {
            "3 days before" -> cal.add(Calendar.DAY_OF_YEAR, -3)
            "1 day before"  -> cal.add(Calendar.DAY_OF_YEAR, -1)
            // "Same day" fires at 8 AM on the due date — no offset needed
        }
        return cal.timeInMillis
    }

    private fun workName(taskId: Long) = "task_reminder_$taskId"
}
