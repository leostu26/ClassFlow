package com.classflow.ui.workload

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.classflow.R
import com.classflow.data.model.Priority
import com.classflow.data.model.TaskType
import com.classflow.data.model.TaskWithCourseInfo
import com.classflow.databinding.FragmentWorkloadBinding
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkloadFragment : Fragment() {

    private var _binding: FragmentWorkloadBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WorkloadViewModel by viewModels()

    private val dateFmt = SimpleDateFormat("MMM d", Locale.getDefault())
    private val dateFmtYear = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    private val dayOfWeekFmt = SimpleDateFormat("EEEE", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPrevWeek.setOnClickListener { viewModel.previousWeek() }
        binding.btnNextWeek.setOnClickListener { viewModel.nextWeek() }
        binding.btnWeekToday.setOnClickListener { viewModel.goToCurrentWeek() }
        binding.btnThisWeek.setOnClickListener { viewModel.goToCurrentWeek() }
        binding.btnJumpNextWeek.setOnClickListener { viewModel.goToNextWeek() }

        binding.tvScoreInfoToggle.setOnClickListener {
            val nowVisible = binding.tvScoreInfoDetail.visibility == View.VISIBLE
            binding.tvScoreInfoDetail.visibility = if (nowVisible) View.GONE else View.VISIBLE
            binding.tvScoreInfoToggle.text =
                if (nowVisible) "How is score calculated?" else "How is score calculated? ▲"
        }

        viewModel.weekStart.observe(viewLifecycleOwner) { weekStart ->
            val weekEnd = weekStart + 6 * WorkloadViewModel.DAY_MS
            binding.tvWeekLabel.text = "${dateFmt.format(Date(weekStart))} – ${dateFmtYear.format(Date(weekEnd))}"
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            bindUiState(state)
        }
    }

    private fun bindUiState(state: WorkloadUiState) {
        val allEmpty = state.isEmpty
        val activeEmpty = !allEmpty && state.activeTasks == 0 // completed-only week

        // Fully empty: no tasks at all
        if (allEmpty) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvEmpty.text = "No tasks scheduled for this week.\nUse ‹ or › to view another week."
            binding.cardSummary.visibility = View.GONE
            binding.cardMostLoaded.visibility = View.GONE
            binding.cardTypeBreakdown.visibility = View.GONE
            binding.tvDailyHeader.visibility = View.GONE
            binding.llDailyContainer.visibility = View.GONE
            return
        }

        binding.tvEmpty.visibility = View.GONE
        binding.cardSummary.visibility = View.VISIBLE
        binding.cardMostLoaded.visibility = View.VISIBLE
        binding.cardTypeBreakdown.visibility = View.VISIBLE
        binding.tvDailyHeader.visibility = View.VISIBLE
        binding.llDailyContainer.visibility = View.VISIBLE

        // Summary card — level
        if (activeEmpty) {
            binding.tvWorkloadLevel.text = "All Done"
            binding.tvWorkloadLevel.setTextColor(Color.parseColor("#10B981"))
        } else {
            binding.tvWorkloadLevel.text = state.workloadLevel.label
            binding.tvWorkloadLevel.setTextColor(levelColor(state.workloadLevel))
        }

        // Summary line — overdue integrated, not a separate warning
        binding.tvTaskSummary.text = buildString {
            append("${state.activeTasks} active task${if (state.activeTasks == 1) "" else "s"}")
            if (state.highPriorityCount > 0) append(" • ${state.highPriorityCount} high priority")
            if (state.dueTodayCount > 0) append(" • ${state.dueTodayCount} due today")
            if (state.overdueCount > 0) append(" • ${state.overdueCount} overdue")
            if (state.completedCount > 0) append(" • ${state.completedCount} completed")
        }

        binding.tvWorkloadScore.text = if (activeEmpty) "All tasks completed" else "Score: ${state.totalScore} pts"

        // Most loaded day
        binding.tvMostLoadedDay.text = state.mostLoadedDay?.let { day ->
            val count = day.activeTasks.size
            "${dateFmt.format(Date(day.dayMs))} • ${dayOfWeekFmt.format(Date(day.dayMs))} • $count task${if (count == 1) "" else "s"} • ${day.points} pts"
        } ?: "None"

        // Type breakdown
        binding.tvCountAssignments.text = "Assignments: ${state.typeBreakdown[TaskType.ASSIGNMENT] ?: 0}"
        binding.tvCountQuizzes.text = "Quizzes: ${state.typeBreakdown[TaskType.QUIZ] ?: 0}"
        binding.tvCountExams.text = "Exams: ${state.typeBreakdown[TaskType.EXAM] ?: 0}"
        binding.tvCountProjects.text = "Projects: ${state.typeBreakdown[TaskType.PROJECT] ?: 0}"
        binding.tvCountOther.text = "Other: ${state.typeBreakdown[TaskType.OTHER] ?: 0}"

        // Daily breakdown
        buildDailyRows(state.dailyBreakdown)
    }

    private fun buildDailyRows(days: List<DayWorkload>) {
        val container = binding.llDailyContainer
        container.removeAllViews()

        val d = resources.displayMetrics.density
        val todayMs = WorkloadViewModel.todayMidnight()
        val primaryColor = requireContext().getColor(R.color.primary)
        val textPrimary = requireContext().getColor(R.color.text_primary)
        val textSecondary = requireContext().getColor(R.color.text_secondary)
        val surfaceColor = requireContext().getColor(R.color.surface)

        for (day in days) {
            val isToday = day.dayMs == todayMs
            val hasAny = day.activeTasks.isNotEmpty() || day.completedTasks.isNotEmpty()

            val card = MaterialCardView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins((12*d).toInt(), (4*d).toInt(), (12*d).toInt(), (4*d).toInt()) }
                radius = 8 * d
                cardElevation = 2 * d
                setCardBackgroundColor(surfaceColor)
                if (isToday) {
                    strokeColor = primaryColor
                    strokeWidth = (1.5f * d).toInt()
                }
            }

            val inner = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding((16*d).toInt(), (12*d).toInt(), (16*d).toInt(), (12*d).toInt())
            }

            // Date header row
            inner.addView(LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                addView(TextView(requireContext()).apply {
                    text = "${dateFmt.format(Date(day.dayMs))} • ${dayOfWeekFmt.format(Date(day.dayMs))}"
                    textSize = 13f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(if (isToday) primaryColor else textPrimary)
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                })
                if (isToday) {
                    addView(makeBadge("Today", Color.argb(30, 61, 90, 254), primaryColor, d))
                }
            })

            if (!hasAny) {
                inner.addView(TextView(requireContext()).apply {
                    text = "No tasks"
                    textSize = 12f
                    setTextColor(textSecondary)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { topMargin = (3*d).toInt() }
                })
            } else {
                // Stats summary line
                val parts = mutableListOf<String>()
                if (day.activeTasks.isNotEmpty()) {
                    parts += "${day.activeTasks.size} active • ${day.points} pts"
                } else if (day.completedTasks.isNotEmpty()) {
                    parts += "No active tasks"
                }
                if (day.completedTasks.isNotEmpty()) parts += "${day.completedTasks.size} completed"
                inner.addView(TextView(requireContext()).apply {
                    text = parts.joinToString(" • ")
                    textSize = 12f
                    setTextColor(textSecondary)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { topMargin = (2*d).toInt() }
                })

                // Workload bar
                if (day.points > 0) inner.addView(makeWorkloadBar(day.points, d))

                // Active task rows
                for (task in day.activeTasks) {
                    val isOverdue = task.dueDate > 0 && task.dueDate < todayMs
                    inner.addView(makeTaskRow(task, completed = false, overdue = isOverdue, d = d,
                        textPrimary = textPrimary, textSecondary = textSecondary))
                }

                // Completed task rows
                for (task in day.completedTasks) {
                    inner.addView(makeTaskRow(task, completed = true, overdue = false, d = d,
                        textPrimary = textPrimary, textSecondary = textSecondary))
                }
            }

            card.addView(inner)
            container.addView(card)
        }
    }

    private fun makeWorkloadBar(points: Int, d: Float): LinearLayout {
        val barWeight = when {
            points <= 5 -> 300f
            points <= 10 -> 600f
            else -> 900f
        }
        val barColor = when {
            points <= 5 -> Color.parseColor("#10B981")
            points <= 10 -> Color.parseColor("#F59E0B")
            else -> Color.parseColor("#EF4444")
        }
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 1000f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (6*d).toInt()
            ).apply { topMargin = (6*d).toInt(); bottomMargin = (3*d).toInt() }
            addView(View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, barWeight)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 3 * d
                    setColor(barColor)
                }
            })
            addView(View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1000f - barWeight)
            })
        }
    }

    private fun makeTaskRow(
        task: TaskWithCourseInfo,
        completed: Boolean,
        overdue: Boolean,
        d: Float,
        textPrimary: Int,
        textSecondary: Int
    ): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = (5*d).toInt() }
            if (completed) alpha = 0.6f

            if (!completed) {
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    findNavController().navigate(
                        R.id.action_ganttChartFragment_to_taskDetailFragment,
                        Bundle().apply {
                            putLong("taskId", task.taskId)
                            putString("courseName", task.courseName)
                        }
                    )
                }
            }

            // Course color dot
            addView(View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams((8*d).toInt(), (8*d).toInt()).apply {
                    rightMargin = (6*d).toInt()
                    gravity = Gravity.CENTER_VERTICAL
                }
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(if (completed) Color.parseColor("#9CA3AF") else safeColor(task.courseColor))
                }
            })

            // Task title
            addView(TextView(requireContext()).apply {
                text = task.title
                textSize = 13f
                setTextColor(
                    when {
                        completed -> textSecondary
                        overdue -> Color.parseColor("#EF4444")
                        else -> textPrimary
                    }
                )
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
                if (completed) paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            })

            if (completed) {
                // Done chip
                addView(makeBadge("Done", Color.argb(24, 0, 0, 0), Color.parseColor("#10B981"), d).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { leftMargin = (6*d).toInt() }
                })
            } else {
                // Priority chip
                addView(makeBadge(
                    task.priority.name.lowercase().replaceFirstChar { it.uppercase() },
                    Color.argb(24, 0, 0, 0), priorityColor(task.priority), d
                ).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { leftMargin = (6*d).toInt() }
                })
                // Overdue chip (only for active overdue tasks)
                if (overdue) {
                    addView(makeBadge("Overdue", Color.argb(30, 220, 38, 38), Color.parseColor("#EF4444"), d).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { leftMargin = (4*d).toInt() }
                    })
                }
            }
        }
    }

    private fun makeBadge(text: String, bgColor: Int, textColor: Int, d: Float): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 10f
            setTextColor(textColor)
            setPadding((5*d).toInt(), (2*d).toInt(), (5*d).toInt(), (2*d).toInt())
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 4 * d
                setColor(bgColor)
            }
        }
    }

    private fun safeColor(hex: String): Int =
        try { Color.parseColor(hex) } catch (e: Exception) { Color.parseColor("#4A90D9") }

    private fun priorityColor(priority: Priority): Int = Color.parseColor(
        when (priority) {
            Priority.HIGH -> "#EF4444"
            Priority.MEDIUM -> "#F59E0B"
            Priority.LOW -> "#10B981"
        }
    )

    private fun levelColor(level: WorkloadLevel): Int = Color.parseColor(
        when (level) {
            WorkloadLevel.LIGHT -> "#10B981"
            WorkloadLevel.MODERATE -> "#F59E0B"
            WorkloadLevel.HEAVY -> "#EF4444"
            WorkloadLevel.OVERLOADED -> "#DC2626"
        }
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
