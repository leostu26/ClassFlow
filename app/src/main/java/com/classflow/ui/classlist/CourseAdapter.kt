package com.classflow.ui.classlist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classflow.data.model.Course
import com.classflow.databinding.ItemCourseBinding

class CourseAdapter(
    private val onItemClick: (Course) -> Unit,
    private val onDeleteClick: (Course) -> Unit
) : ListAdapter<Course, CourseAdapter.CourseViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CourseViewHolder(private val binding: ItemCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(course: Course) {
            binding.tvCourseName.text = course.name
            binding.tvCourseCode.text = course.code
            binding.tvInstructor.text = if (course.instructor.isNotBlank()) course.instructor else "No instructor set"
            binding.tvSchedule.text = if (course.schedule.isNotBlank()) course.schedule else "No schedule set"
            try {
                binding.viewColorBar.setBackgroundColor(Color.parseColor(course.color))
            } catch (e: Exception) {
                binding.viewColorBar.setBackgroundColor(Color.parseColor("#4A90D9"))
            }
            binding.root.setOnClickListener { onItemClick(course) }
            binding.btnDelete.setOnClickListener { onDeleteClick(course) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Course, newItem: Course) = oldItem == newItem
    }
}
