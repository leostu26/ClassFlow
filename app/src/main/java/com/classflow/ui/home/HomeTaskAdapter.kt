package com.classflow.ui.home

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classflow.R
import com.classflow.data.model.TaskWithCourseName
import com.classflow.databinding.ItemTaskHomeBinding
import com.classflow.util.DateUtils
import com.classflow.util.TaskColorUtils

class HomeTaskAdapter(
    private val highlightDateRed: Boolean = false,
    private val onItemClick: (TaskWithCourseName) -> Unit
) : ListAdapter<TaskWithCourseName, HomeTaskAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTaskHomeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemTaskHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TaskWithCourseName) {
            val ctx = binding.root.context

            binding.tvTaskTitle.text = item.title
            binding.tvDueDate.text = DateUtils.formatDate(item.dueDate)
            binding.tvCourseName.text = item.courseName
            binding.tvTaskType.text = item.type.name.lowercase()
                .replaceFirstChar { it.uppercase() }

            // Left bar = class color
            binding.viewPriority.setBackgroundColor(TaskColorUtils.safeColor(item.courseColor))

            // Priority chip = priority color
            binding.tvPriorityChip.text = item.priority.name.lowercase()
                .replaceFirstChar { it.uppercase() }
            binding.tvPriorityChip.setTextColor(TaskColorUtils.priorityColorInt(item.priority, ctx))

            // Overdue due date
            val isOverdue = !item.isCompleted && DateUtils.isOverdue(item.dueDate)
            if (highlightDateRed || isOverdue) {
                binding.tvDueDate.setTextColor(ContextCompat.getColor(ctx, R.color.overdue))
            } else {
                binding.tvDueDate.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary))
            }

            // Completed strikethrough
            if (item.isCompleted) {
                binding.tvTaskTitle.paintFlags =
                    binding.tvTaskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvTaskTitle.alpha = 0.5f
            } else {
                binding.tvTaskTitle.paintFlags =
                    binding.tvTaskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.tvTaskTitle.alpha = 1.0f
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<TaskWithCourseName>() {
        override fun areItemsTheSame(a: TaskWithCourseName, b: TaskWithCourseName) =
            a.taskId == b.taskId
        override fun areContentsTheSame(a: TaskWithCourseName, b: TaskWithCourseName) = a == b
    }
}
