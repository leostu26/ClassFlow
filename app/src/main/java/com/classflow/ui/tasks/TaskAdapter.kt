package com.classflow.ui.tasks

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classflow.R
import com.classflow.data.model.Task
import com.classflow.databinding.ItemTaskBinding
import com.classflow.util.DateUtils
import com.classflow.util.TaskColorUtils

class TaskAdapter(
    private val onChecked: (Task, Boolean) -> Unit,
    private val onItemClick: (Task) -> Unit,
    private val onDeleteClick: ((Task) -> Unit)? = null
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            val ctx = binding.root.context

            binding.tvTaskTitle.text = task.title
            binding.tvDueDate.text = DateUtils.formatDate(task.dueDate)
            binding.tvTaskType.text = task.type.name.lowercase()
                .replaceFirstChar { it.uppercase() }

            // Left bar = priority color (Tasks screen is scoped to one class)
            binding.viewPriority.setBackgroundColor(
                TaskColorUtils.priorityColorInt(task.priority, ctx)
            )

            // Priority chip = priority color
            binding.tvPriorityChip.text = task.priority.name.lowercase()
                .replaceFirstChar { it.uppercase() }
            binding.tvPriorityChip.setTextColor(TaskColorUtils.priorityColorInt(task.priority, ctx))

            // Overdue due date
            if (!task.isCompleted && DateUtils.isOverdue(task.dueDate)) {
                binding.tvDueDate.setTextColor(ContextCompat.getColor(ctx, R.color.overdue))
            } else {
                binding.tvDueDate.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary))
            }

            // Completed styling
            binding.cbComplete.setOnCheckedChangeListener(null)
            binding.cbComplete.isChecked = task.isCompleted
            if (task.isCompleted) {
                binding.tvTaskTitle.paintFlags =
                    binding.tvTaskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvTaskTitle.alpha = 0.5f
            } else {
                binding.tvTaskTitle.paintFlags =
                    binding.tvTaskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.tvTaskTitle.alpha = 1.0f
            }

            binding.cbComplete.setOnCheckedChangeListener { _, isChecked ->
                onChecked(task, isChecked)
            }

            binding.root.setOnClickListener { onItemClick(task) }

            onDeleteClick?.let { listener ->
                binding.btnDeleteTask.visibility = ViewGroup.VISIBLE
                binding.btnDeleteTask.setOnClickListener { listener(task) }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }
}
