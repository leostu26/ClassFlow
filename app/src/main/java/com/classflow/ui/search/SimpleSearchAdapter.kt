package com.classflow.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classflow.R
import com.classflow.data.model.TaskWithCourseInfo

class SimpleSearchAdapter(
    private val onClick: (TaskWithCourseInfo) -> Unit
) : ListAdapter<TaskWithCourseInfo, SimpleSearchAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        android.util.Log.d("SearchTasks", "Adapter: onCreateViewHolder pos=$itemCount")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result_simple, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        android.util.Log.d("SearchTasks", "Adapter: onBindViewHolder pos=$position title=${getItem(position).title}")
        holder.bind(getItem(position))
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvClassName: TextView = itemView.findViewById(R.id.tv_class_name)
        private val tvMeta: TextView = itemView.findViewById(R.id.tv_meta)

        fun bind(item: TaskWithCourseInfo) {
            tvTitle.text = item.title
            tvClassName.text = item.courseName.ifBlank { "No class" }
            tvMeta.text = "${item.type.name.lowercase().replaceFirstChar { it.uppercase() }}  ·  ${item.priority.name.lowercase().replaceFirstChar { it.uppercase() }}"
            itemView.setOnClickListener { onClick(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<TaskWithCourseInfo>() {
            override fun areItemsTheSame(a: TaskWithCourseInfo, b: TaskWithCourseInfo) =
                a.taskId == b.taskId
            override fun areContentsTheSame(a: TaskWithCourseInfo, b: TaskWithCourseInfo) =
                a == b
        }
    }
}
