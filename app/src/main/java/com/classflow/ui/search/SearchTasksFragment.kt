package com.classflow.ui.search

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.classflow.R
import com.classflow.data.model.Priority
import com.classflow.data.model.TaskType
import com.classflow.data.model.TaskWithCourseInfo
import com.classflow.databinding.FragmentSearchTasksBinding
import com.classflow.ui.tasks.CompletionFilter
import com.classflow.ui.tasks.DueFilter
import com.classflow.util.TaskColorUtils
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchTasksFragment : Fragment() {

    private var _binding: FragmentSearchTasksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchTasksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchAndFilters()
        observeViewModel()

        binding.etSearch.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupSearchAndFilters() {
        binding.etSearch.doAfterTextChanged { viewModel.setSearchQuery(it?.toString() ?: "") }

        binding.chipPriority.setOnClickListener {
            PopupMenu(requireContext(), it).apply {
                menu.add(0, 0, 0, "All")
                Priority.values().forEachIndexed { i, p ->
                    menu.add(0, i + 1, i + 1, p.name.lowercase().replaceFirstChar { c -> c.uppercase() })
                }
                setOnMenuItemClickListener { item ->
                    viewModel.setPriorityFilter(
                        if (item.itemId == 0) null else Priority.values()[item.itemId - 1]
                    )
                    true
                }
                show()
            }
        }

        binding.chipType.setOnClickListener {
            PopupMenu(requireContext(), it).apply {
                menu.add(0, 0, 0, "All")
                TaskType.values().forEachIndexed { i, t ->
                    menu.add(0, i + 1, i + 1, t.name.lowercase().replaceFirstChar { c -> c.uppercase() })
                }
                setOnMenuItemClickListener { item ->
                    viewModel.setTypeFilter(
                        if (item.itemId == 0) null else TaskType.values()[item.itemId - 1]
                    )
                    true
                }
                show()
            }
        }

        binding.chipCompletion.setOnClickListener {
            PopupMenu(requireContext(), it).apply {
                menu.add(0, 0, 0, "All")
                menu.add(0, 1, 1, "Incomplete")
                menu.add(0, 2, 2, "Completed")
                setOnMenuItemClickListener { item ->
                    viewModel.setCompletionFilter(when (item.itemId) {
                        1 -> CompletionFilter.PENDING
                        2 -> CompletionFilter.COMPLETED
                        else -> CompletionFilter.ALL
                    })
                    true
                }
                show()
            }
        }

        binding.chipDue.setOnClickListener {
            PopupMenu(requireContext(), it).apply {
                menu.add(0, 0, 0, "All")
                menu.add(0, 1, 1, "Overdue")
                menu.add(0, 2, 2, "Due Today")
                menu.add(0, 3, 3, "Due This Week")
                menu.add(0, 4, 4, "No Date")
                setOnMenuItemClickListener { item ->
                    viewModel.setDueFilter(when (item.itemId) {
                        1 -> DueFilter.OVERDUE
                        2 -> DueFilter.DUE_TODAY
                        3 -> DueFilter.DUE_THIS_WEEK
                        4 -> DueFilter.NO_DATE
                        else -> DueFilter.ALL
                    })
                    true
                }
                show()
            }
        }

        binding.btnClearFilters.setOnClickListener {
            viewModel.clearFilters()
            binding.etSearch.text?.clear()
        }
    }

    private fun renderResults(results: List<TaskWithCourseInfo>) {
        val container = binding.llResults
        container.removeAllViews()

        if (results.isEmpty()) {
            binding.scrollResults.visibility = View.GONE
            return
        }

        val dp = resources.displayMetrics.density
        val ctx = requireContext()
        val dateFmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val now = System.currentTimeMillis()
        val padH = (8 * dp).toInt()
        val padV = (2 * dp).toInt()
        val cornerR = 12 * dp

        for (task in results) {
            val isOverdue = !task.isCompleted && task.dueDate > 0 && task.dueDate < now

            val card = MaterialCardView(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = (8 * dp).toInt() }
                setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.surface))
                radius = 10 * dp
                cardElevation = 2 * dp
                strokeColor = ContextCompat.getColor(ctx, R.color.text_secondary)
                strokeWidth = maxOf((0.5f * dp).toInt(), 1)
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    val action = SearchTasksFragmentDirections
                        .actionSearchTasksFragmentToTaskDetailFragment(
                            taskId = task.taskId,
                            courseName = task.courseName
                        )
                    findNavController().navigate(action)
                }
            }

            // Horizontal: left class color bar + content
            val row = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            // Left class color bar
            row.addView(View(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (5 * dp).toInt(),
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                minimumHeight = (70 * dp).toInt()
                setBackgroundColor(TaskColorUtils.safeColor(task.courseColor))
            })

            // Content
            val content = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                val p = (12 * dp).toInt()
                setPadding(p, p, p, p)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Title
            content.addView(TextView(ctx).apply {
                text = task.title
                setTextColor(ContextCompat.getColor(ctx, R.color.text_primary))
                textSize = 15f
                setTypeface(null, android.graphics.Typeface.BOLD)
                if (task.isCompleted) {
                    paintFlags = paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                    alpha = 0.5f
                }
            })

            // Course name
            content.addView(TextView(ctx).apply {
                text = task.courseName.ifBlank { "No class" }
                setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary))
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.topMargin = (3 * dp).toInt() }
            })

            // Chips row: type chip · priority chip · due date
            val chipsRow = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.topMargin = (6 * dp).toInt() }
            }

            // Type chip (neutral blue)
            chipsRow.addView(TextView(ctx).apply {
                text = task.type.name.lowercase().replaceFirstChar { it.uppercase() }
                setTextColor(ContextCompat.getColor(ctx, R.color.primary))
                textSize = 11f
                setPadding(padH, padV, padH, padV)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = cornerR
                    setColor(0x1A3D5AFE)
                    setStroke(1, 0x333D5AFE)
                }
            })

            // Priority chip (priority color)
            val priorityColor = TaskColorUtils.priorityColorInt(task.priority, ctx)
            val priorityBg = ContextCompat.getColor(ctx, R.color.priority_chip_bg)
            chipsRow.addView(TextView(ctx).apply {
                text = task.priority.name.lowercase().replaceFirstChar { it.uppercase() }
                setTextColor(priorityColor)
                textSize = 11f
                setPadding(padH, padV, padH, padV)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = cornerR
                    setColor(priorityBg)
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.marginStart = (6 * dp).toInt() }
            })

            // Due date
            val dueDateStr = if (task.dueDate > 0L) dateFmt.format(Date(task.dueDate)) else "No due date"
            val dueDateColor = if (isOverdue) ContextCompat.getColor(ctx, R.color.overdue)
                else ContextCompat.getColor(ctx, R.color.text_secondary)
            chipsRow.addView(TextView(ctx).apply {
                text = dueDateStr
                setTextColor(dueDateColor)
                textSize = 12f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.marginStart = (8 * dp).toInt() }
            })

            content.addView(chipsRow)
            row.addView(content)
            card.addView(row)
            container.addView(card)
        }

        binding.scrollResults.visibility = View.VISIBLE
    }

    private fun updateEmptyState(loading: Boolean, count: Int) {
        when {
            loading -> {
                binding.tvNoResults.text = "Loading tasks..."
                binding.tvNoResults.visibility = View.VISIBLE
                binding.scrollResults.visibility = View.GONE
            }
            count > 0 -> {
                binding.tvNoResults.visibility = View.GONE
            }
            else -> {
                binding.tvNoResults.text = "No matching tasks."
                binding.tvNoResults.visibility = View.VISIBLE
                binding.scrollResults.visibility = View.GONE
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            updateEmptyState(loading, viewModel.results.value?.size ?: 0)
        }

        viewModel.results.observe(viewLifecycleOwner) { results ->
            android.util.Log.d("SearchTasks", "Fragment results: count=${results.size}")
            renderResults(results)
            updateEmptyState(viewModel.isLoading.value == true, results.size)
        }

        viewModel.isFiltering.observe(viewLifecycleOwner) { filtering ->
            binding.btnClearFilters.visibility = if (filtering) View.VISIBLE else View.GONE
        }

        viewModel.priorityFilter.observe(viewLifecycleOwner) { priority ->
            binding.chipPriority.isChecked = priority != null
            binding.chipPriority.text = if (priority == null) "Priority"
                else "Priority: ${priority.name.lowercase().replaceFirstChar { it.uppercase() }}"
        }

        viewModel.typeFilter.observe(viewLifecycleOwner) { type ->
            binding.chipType.isChecked = type != null
            binding.chipType.text = if (type == null) "Type"
                else "Type: ${type.name.lowercase().replaceFirstChar { it.uppercase() }}"
        }

        viewModel.completionFilter.observe(viewLifecycleOwner) { completion ->
            binding.chipCompletion.isChecked = completion != CompletionFilter.ALL
            binding.chipCompletion.text = when (completion) {
                CompletionFilter.ALL -> "Status"
                CompletionFilter.PENDING -> "Status: Incomplete"
                CompletionFilter.COMPLETED -> "Status: Completed"
            }
        }

        viewModel.dueFilter.observe(viewLifecycleOwner) { due ->
            binding.chipDue.isChecked = due != DueFilter.ALL
            binding.chipDue.text = when (due) {
                DueFilter.ALL -> "Due"
                DueFilter.OVERDUE -> "Due: Overdue"
                DueFilter.DUE_TODAY -> "Due: Today"
                DueFilter.DUE_THIS_WEEK -> "Due: This Week"
                DueFilter.NO_DATE -> "Due: No Date"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
