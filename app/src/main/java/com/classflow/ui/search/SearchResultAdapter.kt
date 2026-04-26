package com.classflow.ui.search

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classflow.R
import com.classflow.data.model.Priority
import com.classflow.data.model.TaskWithCourseInfo
import com.classflow.databinding.ItemTaskHomeBinding
import com.classflow.util.DateUtils

class SearchResultAdapter(
    private val onItemClick: (TaskWithCourseInfo) -> Unit
) : ListAdapter<TaskWithCourseInfo, SearchResultAdapter.ViewHolder>(DiffCallback) {

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

        fun bind(item: TaskWithCourseInfo) {
            binding.tvTaskTitle.text = item.title
            binding.tvCourseName.text = item.courseName
            binding.tvTaskType.text = item.type.name.lowercase().replaceFirstChar { it.uppercase() }
            binding.tvDueDate.text = DateUtils.formatDate(item.dueDate)

            val priorityColor = when (item.priority) {
                Priority.HIGH -> R.color.priority_high
                Priority.MEDIUM -> R.color.priority_medium
                Priority.LOW -> R.color.priority_low
            }
            binding.viewPriority.setBackgroundColor(
                ContextCompat.getColor(binding.root.context, priorityColor)
            )

            if (!item.isCompleted && DateUtils.isOverdue(item.dueDate)) {
                binding.tvDueDate.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.overdue)
                )
            } else {
                binding.tvDueDate.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.text_secondary)
                )
            }

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

    companion object DiffCallback : DiffUtil.ItemCallback<TaskWithCourseInfo>() {
        override fun areItemsTheSame(a: TaskWithCourseInfo, b: TaskWithCourseInfo) =
            a.taskId == b.taskId
        override fun areContentsTheSame(a: TaskWithCourseInfo, b: TaskWithCourseInfo) = a == b
    }
}
