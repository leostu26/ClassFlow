package com.classflow.ui.settings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

class DevToolsViewModel(application: Application) : AndroidViewModel(application) {

    private val courseRepository: CourseRepository
    private val taskRepository: TaskRepository

    init {
        val db = ClassFlowDatabase.getDatabase(application)
        courseRepository = CourseRepository(db.courseDao())
        taskRepository = TaskRepository(db.taskDao())
    }

    // ── Export Test Backup ────────────────────────────────────────────────────

    val exportFilename = MutableLiveData<String?>()
    val exportSuccess = MutableLiveData<Boolean>()
    val exportError = MutableLiveData<String?>()
    private var pendingExportJson: String? = null

    fun exportTestBackup() = viewModelScope.launch {
        try {
            val courses = courseRepository.getAllCoursesOnce()
            val tasks = taskRepository.getAllTasksOnce()
            val isoDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val isoDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

            val root = JSONObject().apply {
                put("appName", "ClassFlow")
                put("backupVersion", SettingsViewModel.BACKUP_VERSION)
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

            pendingExportJson = root.toString(2)
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            exportFilename.value = "classflow_backup_$timestamp.json"
        } catch (e: Exception) {
            exportError.value = "Could not create backup file."
        }
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

    // ── Import Sample Backup ──────────────────────────────────────────────────

    val sampleImportPreview = MutableLiveData<ImportPreview?>()
    val sampleImportSuccess = MutableLiveData<String?>()
    val sampleImportError = MutableLiveData<String?>()

    private data class PendingImport(val courses: List<JSONObject>, val tasks: List<JSONObject>)
    private var pendingSampleImport: PendingImport? = null

    fun parseSampleBackup() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val root = JSONObject(SAMPLE_BACKUP_JSON)
            val coursesArr = root.optJSONArray("classes") ?: JSONArray()
            val tasksArr = root.optJSONArray("tasks") ?: JSONArray()
            val courses = (0 until coursesArr.length()).map { coursesArr.getJSONObject(it) }
            val tasks = (0 until tasksArr.length()).map { tasksArr.getJSONObject(it) }
            pendingSampleImport = PendingImport(courses, tasks)
            sampleImportPreview.postValue(ImportPreview(courses.size, tasks.size, "Sample Data"))
        } catch (e: Exception) {
            sampleImportError.postValue("Could not load sample backup.")
        }
    }

    fun confirmSampleImport(replaceMode: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        val pending = pendingSampleImport ?: return@launch
        pendingSampleImport = null
        try {
            if (replaceMode) {
                taskRepository.deleteAllTasks()
                courseRepository.deleteAllCourses()
                ReminderScheduler.cancelAllTaskReminders(getApplication())
            }

            val existingCourses = courseRepository.getAllCoursesOnce()
            val existingByName = existingCourses.associateBy { it.name }
            val courseIdMap = mutableMapOf<Long, Long>()

            for (json in pending.courses) {
                val backupId = json.optLong("id", -1L)
                if (backupId < 0) continue
                val name = json.optString("name").trim()
                if (name.isEmpty()) continue
                val existing = existingByName[name]
                if (existing != null) {
                    courseIdMap[backupId] = existing.id
                } else {
                    val code = json.optString("courseCode").ifEmpty { json.optString("code") }
                    val instructor = json.optString("professor").ifEmpty { json.optString("instructor") }
                    val newId = courseRepository.insert(Course(
                        name = name, code = code, instructor = instructor,
                        color = json.optString("color", "#4A90D9"),
                        schedule = json.optString("schedule"),
                        room = json.optString("room"),
                        classMode = json.optString("classMode", "In Person"),
                        meetingLink = json.optString("meetingLink"),
                        platform = json.optString("platform")
                    ))
                    courseIdMap[backupId] = newId
                }
            }

            val existingTaskKeys = taskRepository.getAllTasksOnce()
                .map { Triple(it.courseId, it.title, it.dueDate) }.toHashSet()

            var imported = 0
            for (json in pending.tasks) {
                val backupCourseId: Long = when {
                    json.has("classId")  -> json.getLong("classId")
                    json.has("courseId") -> json.getLong("courseId")
                    else -> -1L
                }
                if (backupCourseId < 0) continue
                val actualCourseId = courseIdMap[backupCourseId] ?: continue
                val title = json.optString("title").trim()
                if (title.isEmpty()) continue
                val dueDate = parseDueDate(json)
                if (!replaceMode && Triple(actualCourseId, title, dueDate) in existingTaskKeys) continue
                taskRepository.insert(Task(
                    courseId = actualCourseId,
                    title = title,
                    description = json.optString("description"),
                    dueDate = dueDate,
                    isCompleted = json.optBoolean("isCompleted", false),
                    priority = safeEnumValueOf(json.optString("priority"), Priority.MEDIUM),
                    type = safeEnumValueOf(json.optString("type"), TaskType.OTHER)
                ))
                imported++
            }

            val classCount = courseIdMap.size
            sampleImportSuccess.postValue(
                "Sample backup imported. $classCount ${if (classCount == 1) "class" else "classes"} " +
                "and $imported ${if (imported == 1) "task" else "tasks"} imported."
            )
            ReminderScheduler.scheduleAllEligibleReminders(getApplication())
        } catch (e: Exception) {
            sampleImportError.postValue("Sample import failed: ${e.message}")
        }
    }

    fun cancelSampleImport() {
        pendingSampleImport = null
        sampleImportPreview.value = null
    }

    fun consumeSampleImportPreview(): ImportPreview? =
        sampleImportPreview.value.also { sampleImportPreview.value = null }

    fun consumeSampleImportSuccess(): String? =
        sampleImportSuccess.value.also { sampleImportSuccess.value = null }

    fun consumeSampleImportError(): String? =
        sampleImportError.value.also { sampleImportError.value = null }

    // ── Clear All Data ────────────────────────────────────────────────────────

    val clearAllSuccess = MutableLiveData<Boolean>()
    val clearAllError = MutableLiveData<String?>()

    fun clearAllData() = viewModelScope.launch(Dispatchers.IO) {
        try {
            taskRepository.deleteAllTasks()
            courseRepository.deleteAllCourses()
            ReminderScheduler.cancelAllTaskReminders(getApplication())
            clearAllSuccess.postValue(true)
        } catch (e: Exception) {
            clearAllError.postValue("Delete failed: ${e.message}")
        }
    }

    fun consumeClearAllSuccess() { clearAllSuccess.value = false }
    fun consumeClearAllError(): String? = clearAllError.value.also { clearAllError.value = null }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private inline fun <reified T : Enum<T>> safeEnumValueOf(name: String, default: T): T =
        runCatching { enumValueOf<T>(name.trim().uppercase()) }.getOrDefault(default)

    private fun parseDueDate(json: JSONObject): Long {
        val key = when {
            json.has("dueDate")  -> "dueDate"
            json.has("due_date") -> "due_date"
            json.has("date")     -> "date"
            else                 -> return 0L
        }
        val raw = json.opt(key) ?: return 0L
        return when (raw) {
            is Number -> raw.toLong()
            is String -> {
                if (raw.isEmpty()) return 0L
                runCatching {
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        .parse(raw.substring(0, 10)) ?: return@runCatching 0L
                    Calendar.getInstance().apply {
                        time = date
                        set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                }.getOrDefault(0L)
            }
            else -> 0L
        }
    }

    companion object {
        private val SAMPLE_BACKUP_JSON = """
{
  "appName": "ClassFlow",
  "backupVersion": 1,
  "exportedAt": "2026-01-01T09:00:00",
  "classes": [
    {
      "id": 1,
      "name": "Introduction to Programming",
      "courseCode": "CS-101",
      "professor": "Dr. Johnson",
      "schedule": "Mon/Wed 9:00 AM",
      "room": "Hall B-210",
      "color": "#3B5BFF",
      "classMode": "In Person",
      "meetingLink": "",
      "platform": ""
    },
    {
      "id": 2,
      "name": "Calculus I",
      "courseCode": "MATH-101",
      "professor": "Prof. Chen",
      "schedule": "Tue/Thu 11:00 AM",
      "room": "",
      "color": "#FF6B35",
      "classMode": "Online",
      "meetingLink": "https://zoom.us/j/example",
      "platform": "Zoom"
    }
  ],
  "tasks": [
    {
      "id": 1,
      "classId": 1,
      "title": "Hello World Program",
      "description": "Write your first Java program",
      "dueDate": "2026-05-01",
      "type": "Assignment",
      "priority": "Low",
      "isCompleted": false
    },
    {
      "id": 2,
      "classId": 1,
      "title": "Midterm Exam",
      "description": "",
      "dueDate": "2026-05-15",
      "type": "Exam",
      "priority": "High",
      "isCompleted": false
    },
    {
      "id": 3,
      "classId": 2,
      "title": "Problem Set 1",
      "description": "Chapter 1-3 exercises",
      "dueDate": "2026-05-03",
      "type": "Assignment",
      "priority": "Medium",
      "isCompleted": true
    }
  ]
}""".trimIndent()
    }
}
