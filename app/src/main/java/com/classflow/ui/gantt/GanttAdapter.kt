package com.classflow.ui.gantt

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classflow.data.model.Priority
import com.classflow.databinding.ItemGanttAllTaskRowBinding
import com.classflow.databinding.ItemGanttHeaderBinding
import com.classflow.databinding.ItemGanttTaskRowBinding
import com.classflow.util.TaskColorUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GanttAdapter : ListAdapter<GanttListItem, RecyclerView.ViewHolder>(DIFF) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_BY_CLASS_TASK = 1
        private const val TYPE_ALL_TASK = 2
        private const val DAY_MS = 24 * 60 * 60 * 1000L

        private val DIFF = object : DiffUtil.ItemCallback<GanttListItem>() {
            override fun areItemsTheSame(old: GanttListItem, new: GanttListItem) = when {
                old is GanttListItem.Header && new is GanttListItem.Header ->
                    old.courseName == new.courseName
                old is GanttListItem.TaskRow && new is GanttListItem.TaskRow ->
                    old.task.taskId == new.task.taskId
                old is GanttListItem.AllTaskRow && new is GanttListItem.AllTaskRow ->
                    old.task.taskId == new.task.taskId
                else -> false
            }
            override fun areContentsTheSame(old: GanttListItem, new: GanttListItem) = old == new
        }

        private fun setWeight(view: View, weight: Float) {
            (view.layoutParams as LinearLayout.LayoutParams).also {
                it.weight = weight
                view.layoutParams = it
            }
        }

        private fun applyBar(
            spacerStart: View,
            barView: View,
            spacerEnd: View,
            startDate: Long,
            dueDate: Long,
            windowStart: Long,
            windowEnd: Long,
            barColor: Int
        ) {
            val windowMs = (windowEnd - windowStart).toFloat().coerceAtLeast(1f)
            val taskEndMs = dueDate + DAY_MS
            val visibleStart = maxOf(startDate, windowStart)
            val visibleEnd = minOf(taskEndMs, windowEnd)

            val leftW = ((visibleStart - windowStart) / windowMs * 1000f)
                .coerceIn(0f, 998f).toInt()
            val rawBarMs = (visibleEnd - visibleStart).toFloat().coerceAtLeast(DAY_MS.toFloat())
            val barW = (rawBarMs / windowMs * 1000f)
                .coerceIn(2f, (1000f - leftW)).toInt()
            val rightW = (1000 - leftW - barW).coerceAtLeast(0)

            setWeight(spacerStart, leftW.toFloat())
            setWeight(barView, barW.toFloat())
            setWeight(spacerEnd, rightW.toFloat())

            barView.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 8f
                setColor(barColor)
            }
        }
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is GanttListItem.Header -> TYPE_HEADER
        is GanttListItem.TaskRow -> TYPE_BY_CLASS_TASK
        is GanttListItem.AllTaskRow -> TYPE_ALL_TASK
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inf = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderVH(ItemGanttHeaderBinding.inflate(inf, parent, false))
            TYPE_ALL_TASK -> AllTaskVH(ItemGanttAllTaskRowBinding.inflate(inf, parent, false))
            else -> TaskVH(ItemGanttTaskRowBinding.inflate(inf, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is GanttListItem.Header -> (holder as HeaderVH).bind(item)
            is GanttListItem.TaskRow -> (holder as TaskVH).bind(item)
            is GanttListItem.AllTaskRow -> (holder as AllTaskVH).bind(item)
        }
    }

    // ── Header (By Class) ─────────────────────────────────────────────────────

    inner class HeaderVH(private val b: ItemGanttHeaderBinding) :
        RecyclerView.ViewHolder(b.root) {

        private val fmt = SimpleDateFormat("MMM d", Locale.getDefault())

        fun bind(item: GanttListItem.Header) {
            b.tvCourseName.text = item.courseName
            b.tvDateRange.text = if (item.startRange > 0 && item.endRange > 0)
                "${fmt.format(Date(item.startRange))} – ${fmt.format(Date(item.endRange))}"
            else ""
            b.colorDot.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(TaskColorUtils.safeColor(item.courseColor))
            }
        }
    }

    // ── Task row (By Class) ───────────────────────────────────────────────────

    inner class TaskVH(private val b: ItemGanttTaskRowBinding) :
        RecyclerView.ViewHolder(b.root) {

        private val dateFmt = SimpleDateFormat("MMM d", Locale.getDefault())

        fun bind(item: GanttListItem.TaskRow) {
            val task = item.task
            val ctx = b.root.context
            val now = System.currentTimeMillis()
            val isOverdue = !task.isCompleted && task.dueDate > 0 && task.dueDate < now

            b.tvTaskTitle.text = task.title
            b.tvTaskTitle.paintFlags = strikeIf(task.isCompleted, b.tvTaskTitle.paintFlags)

            b.tvDueDate.text = if (task.dueDate > 0) dateFmt.format(Date(task.dueDate)) else "—"
            b.tvDueDate.setTextColor(
                if (isOverdue) TaskColorUtils.safeColor("#EF4444")
                else Color.parseColor("#6B7280")
            )

            b.tvPriority.text = task.priority.label()
            b.tvPriority.setTextColor(TaskColorUtils.priorityColorInt(task.priority, ctx))

            b.tvType.text = task.type.label()

            b.tvStatusChip.text = item.daysLabel
            b.tvStatusChip.setTextColor(when {
                task.isCompleted -> Color.parseColor("#10B981")
                isOverdue -> Color.parseColor("#EF4444")
                item.daysLabel == "Due today" -> Color.parseColor("#F59E0B")
                else -> Color.parseColor("#6B7280")
            })

            b.root.alpha = if (task.isCompleted) 0.55f else 1.0f
            val strokePx = (2f * b.root.context.resources.displayMetrics.density).toInt()
            b.root.strokeColor = if (isOverdue) Color.parseColor("#EF4444") else Color.TRANSPARENT
            b.root.strokeWidth = if (isOverdue) strokePx else 0

            val barColor = TaskColorUtils.ganttBarColor(task.isCompleted, isOverdue, task.courseColor)
            applyBar(b.spacerStart, b.barView, b.spacerEnd,
                item.startDate, task.dueDate, item.windowStart, item.windowEnd, barColor)
        }
    }

    // ── All Task row ──────────────────────────────────────────────────────────

    inner class AllTaskVH(private val b: ItemGanttAllTaskRowBinding) :
        RecyclerView.ViewHolder(b.root) {

        private val dateFmt = SimpleDateFormat("MMM d", Locale.getDefault())

        fun bind(item: GanttListItem.AllTaskRow) {
            val task = item.task
            val ctx = b.root.context
            val now = System.currentTimeMillis()
            val isOverdue = !task.isCompleted && task.dueDate > 0 && task.dueDate < now

            // Class color dot
            b.colorDot.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(TaskColorUtils.safeColor(task.courseColor))
            }

            // Title
            b.tvTitle.text = task.title
            b.tvTitle.paintFlags = strikeIf(task.isCompleted, b.tvTitle.paintFlags)

            // Due date
            b.tvDueDate.text = if (task.dueDate > 0) dateFmt.format(Date(task.dueDate)) else "—"
            b.tvDueDate.setTextColor(
                if (isOverdue) TaskColorUtils.safeColor("#EF4444")
                else Color.parseColor("#6B7280")
            )

            // Class name
            b.tvClassName.text = task.courseName

            // Priority chip = priority color
            b.tvPriorityChip.text = task.priority.label()
            b.tvPriorityChip.setTextColor(TaskColorUtils.priorityColorInt(task.priority, ctx))

            // Type chip
            b.tvTypeChip.text = task.type.label()

            // Days chip
            b.tvDaysChip.text = item.daysLabel
            b.tvDaysChip.setTextColor(when {
                task.isCompleted -> Color.parseColor("#10B981")
                isOverdue -> Color.parseColor("#EF4444")
                item.daysLabel == "Due today" -> Color.parseColor("#F59E0B")
                else -> Color.parseColor("#6B7280")
            })

            // Card alpha + overdue stroke
            b.root.alpha = if (task.isCompleted) 0.55f else 1.0f
            val strokePx = (2f * b.root.context.resources.displayMetrics.density).toInt()
            b.root.strokeColor = if (isOverdue) Color.parseColor("#EF4444") else Color.TRANSPARENT
            b.root.strokeWidth = if (isOverdue) strokePx else 0

            // Gantt bar = class color (gray if done, red if overdue)
            val barColor = TaskColorUtils.ganttBarColor(task.isCompleted, isOverdue, task.courseColor)
            applyBar(b.spacerStart, b.barView, b.spacerEnd,
                item.startDate, task.dueDate, item.windowStart, item.windowEnd, barColor)
        }
    }

    // ── Shared helpers ────────────────────────────────────────────────────────

    private fun strikeIf(strike: Boolean, flags: Int): Int =
        if (strike) flags or Paint.STRIKE_THRU_TEXT_FLAG
        else flags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

    private fun Priority.label() = name.lowercase().replaceFirstChar { it.uppercase() }
    private fun com.classflow.data.model.TaskType.label() =
        name.lowercase().replaceFirstChar { it.uppercase() }
}
