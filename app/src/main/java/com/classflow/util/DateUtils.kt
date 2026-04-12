package com.classflow.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val shortFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())

    fun formatDate(timestamp: Long): String {
        if (timestamp == 0L) return "No due date"
        return dateFormatter.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        if (timestamp == 0L) return "—"
        return shortFormatter.format(Date(timestamp))
    }

    fun isDueSoon(timestamp: Long, days: Int = 3): Boolean {
        if (timestamp == 0L) return false
        val now = System.currentTimeMillis()
        val threshold = now + (days * 24 * 60 * 60 * 1000L)
        return timestamp in now..threshold
    }

    fun isOverdue(timestamp: Long): Boolean {
        if (timestamp == 0L) return false
        return timestamp < System.currentTimeMillis()
    }

    fun todayStart(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun todayEnd(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    fun tomorrowStart(): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun daysFromNow(days: Int): Long {
        return System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000L)
    }
}
