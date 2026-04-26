package com.classflow.util

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.classflow.R
import com.classflow.data.model.Priority

object TaskColorUtils {

    fun priorityColorRes(priority: Priority): Int = when (priority) {
        Priority.HIGH -> R.color.priority_high
        Priority.MEDIUM -> R.color.priority_medium
        Priority.LOW -> R.color.priority_low
    }

    fun priorityColorInt(priority: Priority, context: Context): Int =
        ContextCompat.getColor(context, priorityColorRes(priority))

    fun safeColor(hex: String, fallback: String = "#4A90D9"): Int =
        try { Color.parseColor(hex) } catch (_: Exception) { Color.parseColor(fallback) }

    fun ganttBarColor(completed: Boolean, isOverdue: Boolean, courseColor: String): Int = when {
        completed -> Color.parseColor("#9CA3AF")
        isOverdue -> Color.parseColor("#EF4444")
        else -> safeColor(courseColor)
    }
}
