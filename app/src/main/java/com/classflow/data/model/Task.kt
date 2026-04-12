package com.classflow.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = Course::class,
        parentColumns = ["id"],
        childColumns = ["courseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("courseId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val courseId: Long,
    val title: String,
    val description: String = "",
    val dueDate: Long = 0L,  // Unix timestamp in millis
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val type: TaskType = TaskType.ASSIGNMENT
)

enum class Priority { LOW, MEDIUM, HIGH }
enum class TaskType { ASSIGNMENT, QUIZ, EXAM, PROJECT, OTHER }
