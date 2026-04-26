package com.classflow.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val code: String,
    val instructor: String = "",
    val color: String = "#4A90D9",
    val schedule: String = "",
    val room: String = "",
    val classMode: String = "In Person",
    val meetingLink: String = "",
    val platform: String = ""
)
