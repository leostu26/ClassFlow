package com.classflow.ui.gantt

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.classflow.R
import com.classflow.databinding.FragmentGanttChartBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GanttChartFragment : Fragment() {

    private var _binding: FragmentGanttChartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GanttChartViewModel by viewModels()
    private lateinit var adapter: GanttAdapter

    private val windowFmt = SimpleDateFormat("MMM d", Locale.getDefault())
    private val windowFmtYear = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    private var currentWinStart: Long = 0L
    private var currentDueCounts: Map<Long, Int> = emptyMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGanttChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GanttAdapter()
        binding.rvGantt.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGantt.adapter = adapter

        // Navigation
        binding.btnPrevious.setOnClickListener { viewModel.previousWindow() }
        binding.btnNext.setOnClickListener { viewModel.nextWindow() }
        binding.btnToday.setOnClickListener { viewModel.goToToday() }

        // View-mode toggle: suppress listener when restoring state programmatically
        var suppressToggle = false
        binding.toggleViewMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked && !suppressToggle) {
                viewModel.setViewMode(
                    if (checkedId == R.id.btnAllTasks) ViewMode.ALL_TASKS else ViewMode.BY_CLASS
                )
            }
        }

        // Restore toggle state when ViewModel survives fragment recreation
        viewModel.viewMode.observe(viewLifecycleOwner) { mode ->
            val targetId = if (mode == ViewMode.ALL_TASKS) R.id.btnAllTasks else R.id.btnByClass
            if (binding.toggleViewMode.checkedButtonId != targetId) {
                suppressToggle = true
                binding.toggleViewMode.check(targetId)
                suppressToggle = false
            }
        }

        viewModel.windowStart.observe(viewLifecycleOwner) { winStart ->
            currentWinStart = winStart
            updateWindowHeader(winStart)
        }

        viewModel.ganttItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            val empty = items.isEmpty()
            binding.tvEmpty.visibility = if (empty) View.VISIBLE else View.GONE
            binding.rvGantt.visibility = if (empty) View.GONE else View.VISIBLE
        }

        viewModel.windowSummary.observe(viewLifecycleOwner) { summary ->
            binding.tvWindowSummary.text = buildString {
                append("${summary.total} task${if (summary.total == 1) "" else "s"}")
                if (summary.highPriority > 0) append(" • ${summary.highPriority} high priority")
                if (summary.dueToday > 0) append(" • ${summary.dueToday} due today")
            }
        }

        viewModel.dueCounts.observe(viewLifecycleOwner) { counts ->
            currentDueCounts = counts
            buildDayMarkers(currentWinStart, counts)
        }
    }

    private fun updateWindowHeader(winStart: Long) {
        val lastDay = winStart + (GanttChartViewModel.WINDOW_DAYS - 1) * GanttChartViewModel.DAY_MS

        val startCal = Calendar.getInstance().apply { timeInMillis = winStart }
        val endCal = Calendar.getInstance().apply { timeInMillis = lastDay }

        val startLabel = if (startCal.get(Calendar.YEAR) != endCal.get(Calendar.YEAR))
            windowFmtYear.format(Date(winStart)) else windowFmt.format(Date(winStart))

        binding.tvWindowLabel.text = "$startLabel – ${windowFmtYear.format(Date(lastDay))}"

        buildDayMarkers(winStart, currentDueCounts)
    }

    private fun buildDayMarkers(winStart: Long, counts: Map<Long, Int> = emptyMap()) {
        val row = binding.dayMarkersRow
        row.removeAllViews()

        val todayMs = GanttChartViewModel.todayMidnight()
        val primaryColor = requireContext().getColor(R.color.primary)
        val secondaryColor = requireContext().getColor(R.color.text_secondary)
        val density = resources.displayMetrics.density

        for (i in 0 until GanttChartViewModel.WINDOW_DAYS) {
            val dayMs = winStart + i * GanttChartViewModel.DAY_MS
            val cal = Calendar.getInstance().apply { timeInMillis = dayMs }
            val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
            val isToday = dayMs == todayMs
            val showMonth = i == 0 || dayOfMonth == 1

            val cell = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                if (isToday) setBackgroundColor(Color.parseColor("#1A3D5AFE"))
            }

            cell.addView(TextView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gravity = Gravity.CENTER
                textSize = 7f
                text = if (showMonth) SimpleDateFormat("MMM", Locale.getDefault()).format(Date(dayMs)) else ""
                setTextColor(if (isToday) primaryColor else secondaryColor)
            })

            cell.addView(TextView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gravity = Gravity.CENTER
                textSize = 9f
                text = dayOfMonth.toString()
                setTextColor(if (isToday) primaryColor else secondaryColor)
                if (isToday) setTypeface(null, Typeface.BOLD)
            })

            val count = counts[dayMs] ?: 0
            if (count > 0) {
                val label = if (count >= 3) "3+" else count.toString()
                val hPad = (3 * density).toInt()
                val vPad = (1 * density).toInt()
                cell.addView(TextView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER_HORIZONTAL
                        topMargin = (2 * density).toInt()
                    }
                    text = label
                    textSize = 7f
                    gravity = Gravity.CENTER
                    setPadding(hPad, vPad, hPad, vPad)
                    setTextColor(if (isToday) Color.WHITE else primaryColor)
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 4 * density
                        setColor(
                            if (isToday) primaryColor
                            else Color.argb(30, 61, 90, 254)
                        )
                    }
                })
            }

            row.addView(cell)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
