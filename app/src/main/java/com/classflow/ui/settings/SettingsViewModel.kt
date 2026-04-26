package com.classflow.ui.settings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.classflow.BuildConfig
import com.classflow.data.ClassFlowDatabase
import com.classflow.data.model.Course
import com.classflow.data.model.Priority
import com.classflow.data.model.Task
import com.classflow.data.model.TaskType
import com.classflow.data.repository.CourseRepository
import com.classflow.data.repository.TaskRepository
import com.classflow.notification.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ImportPreview(
    val courseCount: Int,
    val taskCount: Int,
    val exportedAt: String
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val courseRepository: CourseRepository
    private val taskRepository: TaskRepository
    private val settingsRepository = SettingsRepository(application)

    // ── Preferences ───────────────────────────────────────────────────────────

    val settings = MutableLiveData(settingsRepository.getUserSettings())

    fun updateThemeMode(mode: String) {
        settingsRepository.updateThemeMode(mode)
        settings.value = settingsRepository.getUserSettings()
    }

    fun updateWeekStartDay(day: String) {
        settingsRepository.updateWeekStartDay(day)
        settings.value = settingsRepository.getUserSettings()
    }

    fun updateShowCompletedTasks(show: Boolean) {
        settingsRepository.updateShowCompletedTasks(show)
        settings.value = settingsRepository.getUserSettings()
    }

    fun updateDefaultTaskType(taskType: String) {
        settingsRepository.updateDefaultTaskType(taskType)
        settings.value = settingsRepository.getUserSettings()
    }

    fun updateDefaultPriority(priority: String) {
        settingsRepository.updateDefaultPriority(priority)
        settings.value = settingsRepository.getUserSettings()
    }

    // ── Restore / Delete ──────────────────────────────────────────────────────

    val restoreSuccess = MutableLiveData<Boolean>()
    val deleteAllSuccess = MutableLiveData<Boolean>()
    val deleteAllError = MutableLiveData<String?>()

    fun restoreDefaultSettings() {
        val wasEnabled = settingsRepository.getUserSettings().notificationsEnabled
        settingsRepository.restoreDefaultSettings()
        settings.value = settingsRepository.getUserSettings()
        restoreSuccess.value = true
        if (wasEnabled) ReminderScheduler.cancelAllTaskReminders(getApplication())
    }

    fun consumeRestoreSuccess() { restoreSuccess.value = false }

    fun deleteAllData() = viewModelScope.launch(Dispatchers.IO) {
        try {
            taskRepository.deleteAllTasks()
            courseRepository.deleteAllCourses()
            ReminderScheduler.cancelAllTaskReminders(getApplication())
            deleteAllSuccess.postValue(true)
        } catch (e: Exception) {
            deleteAllError.postValue("Delete failed: ${e.message}")
        }
    }

    fun consumeDeleteAllSuccess() { deleteAllSuccess.value = false }
    fun consumeDeleteAllError(): String? = deleteAllError.value.also { deleteAllError.value = null }

    // ── Reminders ─────────────────────────────────────────────────────────────

    fun enableNotifications() = viewModelScope.launch {
        settingsRepository.updateNotificationsEnabled(true)
        settings.postValue(settingsRepository.getUserSettings())
        ReminderScheduler.scheduleAllEligibleReminders(getApplication())
    }

    fun disableNotifications() {
        settingsRepository.updateNotificationsEnabled(false)
        settings.value = settingsRepository.getUserSettings()
        ReminderScheduler.cancelAllTaskReminders(getApplication())
    }

    fun updateReminderTiming(timing: String) = viewModelScope.launch {
        settingsRepository.updateReminderTiming(timing)
        settings.postValue(settingsRepository.getUserSettings())
        if (settingsRepository.getUserSettings().notificationsEnabled) {
            ReminderScheduler.cancelAllTaskReminders(getApplication())
            ReminderScheduler.scheduleAllEligibleReminders(getApplication())
        }
    }

    // ── Export ────────────────────────────────────────────────────────────────

    val exportFilename = MutableLiveData<String?>()
    val exportSuccess = MutableLiveData<Boolean>()
    val exportError = MutableLiveData<String?>()
    private var pendingExportJson: String? = null

    init {
        val db = ClassFlowDatabase.getDatabase(application)
        courseRepository = CourseRepository(db.courseDao())
        taskRepository = TaskRepository(db.taskDao())
    }

    fun exportBackup() = viewModelScope.launch {
        try {
            val courses = courseRepository.getAllCoursesOnce()
            val tasks = taskRepository.getAllTasksOnce()
            pendingExportJson = buildBackupJson(courses, tasks)
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            exportFilename.value = "classflow_backup_$timestamp.json"
        } catch (e: Exception) {
            exportError.value = "Could not create backup file."
        }
    }

    fun buildBackupJson(courses: List<Course>, tasks: List<Task>): String {
        val isoDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val isoDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

        val root = JSONObject().apply {
            put("appName", "ClassFlow")
            put("backupVersion", BACKUP_VERSION)
            put("exportedAt", isoDateTime.format(Date()))

            val coursesArr = JSONArray()
            courses.forEach { c ->
                coursesArr.put(JSONObject().apply {
                    put("id", c.id)
                    put("name", c.name)
                    put("courseCode", c.code)
                    put("professor", c.instructor)
                    put("schedule", c.schedule)
                    put("room", c.room)
                    put("color", c.color)
                    put("classMode", c.classMode)
                    put("meetingLink", c.meetingLink)
                    put("platform", c.platform)
                })
            }
            put("classes", coursesArr)

            val tasksArr = JSONArray()
            tasks.forEach { t ->
                tasksArr.put(JSONObject().apply {
                    put("id", t.id)
                    put("classId", t.courseId)
                    put("courseId", t.courseId)
                    put("title", t.title)
                    put("description", t.description)
                    put("dueDate", if (t.dueDate > 0) isoDate.format(Date(t.dueDate)) else "")
                    put("type", t.type.name.lowercase().replaceFirstChar { it.uppercase() })
                    put("priority", t.priority.name.lowercase().replaceFirstChar { it.uppercase() })
                    put("isCompleted", t.isCompleted)
                })
            }
            put("tasks", tasksArr)
        }
        return root.toString(2)
    }

    fun writeExportToUri(uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val json = pendingExportJson ?: return@launch
            pendingExportJson = null
            getApplication<Application>().contentResolver.openOutputStream(uri)?.use { out ->
                out.write(json.toByteArray())
            }
            exportSuccess.postValue(true)
        } catch (e: Exception) {
            exportError.postValue("Could not create backup file.")
        }
    }

    fun consumeExportFilename(): String? = exportFilename.value.also { exportFilename.value = null }
    fun consumeExportSuccess() { exportSuccess.value = false }
    fun consumeExportError(): String? = exportError.value.also { exportError.value = null }

    // ── Import ────────────────────────────────────────────────────────────────

    val importConfirmation = MutableLiveData<ImportPreview?>()
    val importSuccess = MutableLiveData<String?>()
    val importError = MutableLiveData<String?>()

    private data class PendingImport(
        val courses: List<JSONObject>,
        val tasks: List<JSONObject>
    )
    private var pendingImport: PendingImport? = null

    fun parseImportFile(uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val json = getApplication<Application>().contentResolver
                .openInputStream(uri)
                ?.bufferedReader()
                ?.use { it.readText() }
                ?: throw Exception("Could not open file")

            val root = JSONObject(json)

            if (root.optString("appName") != "ClassFlow") {
                importError.postValue("Invalid backup file.")
                return@launch
            }

            val backupVersion = root.optInt("backupVersion", 0)
            if (backupVersion > BACKUP_VERSION) {
                importError.postValue("Unsupported backup version.")
                return@launch
            }

            val coursesArr = root.optJSONArray("classes")
            if (coursesArr == null || coursesArr.length() == 0) {
                importError.postValue("No classes found in backup.")
                return@launch
            }

            val tasksArr = root.optJSONArray("tasks") ?: JSONArray()
            val courses = (0 until coursesArr.length()).map { coursesArr.getJSONObject(it) }
            val tasks = (0 until tasksArr.length()).map { tasksArr.getJSONObject(it) }

            pendingImport = PendingImport(courses, tasks)

            val exportedAtRaw = when {
                root.has("exportedAt") -> root.optString("exportedAt")
                root.has("exportDate") -> root.optString("exportDate")
                else -> ""
            }
            importConfirmation.postValue(ImportPreview(courses.size, tasks.size, formatExportDate(exportedAtRaw)))
        } catch (e: Exception) {
            importError.postValue("Invalid backup file.")
        }
    }

    fun confirmImport(replaceMode: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {
        val pending = pendingImport ?: return@launch
        pendingImport = null

        try {
            if (replaceMode) {
                taskRepository.deleteAllTasks()
                courseRepository.deleteAllCourses()
                ReminderScheduler.cancelAllTaskReminders(getApplication())
            }

            // ── Import courses ──────────────────────────────────────────────────
            val existingCourses = courseRepository.getAllCoursesOnce()
            val existingByName = existingCourses.associateBy { it.name }
            val courseIdMap = mutableMapOf<Long, Long>()

            for (courseJson in pending.courses) {
                val backupId = courseJson.optLong("id", -1L)
                if (backupId < 0) continue
                val name = courseJson.optString("name").trim()
                if (name.isEmpty()) continue

                val existing = existingByName[name]
                if (existing != null) {
                    courseIdMap[backupId] = existing.id
                } else {
                    val code = courseJson.optString("courseCode").ifEmpty { courseJson.optString("code") }
                    val instructor = courseJson.optString("professor").ifEmpty { courseJson.optString("instructor") }
                    val newId = courseRepository.insert(
                        Course(
                            name = name,
                            code = code,
                            instructor = instructor,
                            color = courseJson.optString("color", "#4A90D9"),
                            schedule = courseJson.optString("schedule"),
                            room = courseJson.optString("room"),
                            classMode = courseJson.optString("classMode", "In Person"),
                            meetingLink = courseJson.optString("meetingLink"),
                            platform = courseJson.optString("platform")
                        )
                    )
                    courseIdMap[backupId] = newId
                }
            }

            // ── Import tasks ────────────────────────────────────────────────────
            val existingTaskKeys = taskRepository.getAllTasksOnce()
                .map { Triple(it.courseId, it.title, it.dueDate) }
                .toHashSet()

            var tasksImported = 0
            var tasksSkipped = 0
            var tasksMissingClass = 0

            for (taskJson in pending.tasks) {
                val backupCourseId: Long = when {
                    taskJson.has("classId")  -> taskJson.getLong("classId")
                    taskJson.has("courseId") -> taskJson.getLong("courseId")
                    else -> { tasksSkipped++; continue }
                }

                val actualCourseId = courseIdMap[backupCourseId] ?: run {
                    tasksSkipped++; tasksMissingClass++; continue
                }

                val title = taskJson.optString("title").trim()
                if (title.isEmpty()) { tasksSkipped++; continue }

                val dueDate = parseDueDate(taskJson)

                val isCompleted: Boolean = when {
                    taskJson.has("isCompleted") -> taskJson.optBoolean("isCompleted", false)
                    taskJson.has("completed")   -> taskJson.optBoolean("completed", false)
                    taskJson.has("done")        -> taskJson.optBoolean("done", false)
                    else -> false
                }

                val typeStr: String = when {
                    taskJson.has("type")      -> taskJson.optString("type")
                    taskJson.has("taskType")  -> taskJson.optString("taskType")
                    taskJson.has("task_type") -> taskJson.optString("task_type")
                    else -> ""
                }

                if (!replaceMode && Triple(actualCourseId, title, dueDate) in existingTaskKeys) continue

                taskRepository.insert(
                    Task(
                        courseId = actualCourseId,
                        title = title,
                        description = taskJson.optString("description"),
                        dueDate = dueDate,
                        isCompleted = isCompleted,
                        priority = safeEnumValueOf(taskJson.optString("priority"), Priority.MEDIUM),
                        type = safeEnumValueOf(typeStr, TaskType.OTHER)
                    )
                )
                tasksImported++
            }

            // ── Build result message ────────────────────────────────────────────
            val classCount = courseIdMap.size
            val message = buildString {
                append(if (tasksSkipped == 0) "Backup imported successfully. " else "Backup imported with warnings. ")
                append("$classCount ${if (classCount == 1) "class" else "classes"} and ")
                append("$tasksImported ${if (tasksImported == 1) "task" else "tasks"} imported.")
                if (tasksMissingClass > 0) {
                    append(" $tasksMissingClass ${if (tasksMissingClass == 1) "task" else "tasks"} skipped — class not found in backup.")
                } else if (tasksSkipped > 0) {
                    append(" $tasksSkipped ${if (tasksSkipped == 1) "task" else "tasks"} skipped.")
                }
            }
            importSuccess.postValue(message)
            ReminderScheduler.scheduleAllEligibleReminders(getApplication())
        } catch (e: Exception) {
            importError.postValue("Could not import backup: ${e.message}")
        }
    }

    fun cancelImport() {
        pendingImport = null
        importConfirmation.value = null
    }

    fun consumeImportConfirmation(): ImportPreview? =
        importConfirmation.value.also { importConfirmation.value = null }

    fun consumeImportSuccess(): String? =
        importSuccess.value.also { importSuccess.value = null }

    fun consumeImportError(): String? =
        importError.value.also { importError.value = null }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private inline fun <reified T : Enum<T>> safeEnumValueOf(name: String, default: T): T =
        runCatching { enumValueOf<T>(name.trim().uppercase()) }.getOrDefault(default)

    private fun parseDueDate(taskJson: JSONObject): Long {
        val key = when {
            taskJson.has("dueDate")  -> "dueDate"
            taskJson.has("due_date") -> "due_date"
            taskJson.has("date")     -> "date"
            else                     -> return 0L
        }
        val raw = taskJson.opt(key) ?: return 0L
        return when (raw) {
            is Number -> raw.toLong()
            is String -> {
                if (raw.isEmpty()) return 0L
                runCatching {
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        .parse(raw.substring(0, 10)) ?: return@runCatching 0L
                    Calendar.getInstance().apply {
                        time = date
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                }.getOrDefault(0L)
            }
            else -> 0L
        }
    }

    private fun formatExportDate(dateStr: String): String {
        if (dateStr.isEmpty()) return "Unknown"
        return runCatching {
            val parsed = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(dateStr)
            SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(parsed!!)
        }.getOrDefault(dateStr)
    }

    companion object {
        const val BACKUP_VERSION = 1
    }
}
