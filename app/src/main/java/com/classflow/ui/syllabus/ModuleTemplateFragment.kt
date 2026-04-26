package com.classflow.ui.syllabus

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.classflow.R
import com.classflow.data.model.Priority
import com.classflow.data.model.TaskType
import com.classflow.databinding.DialogEditTaskRuleBinding
import com.classflow.databinding.FragmentModuleTemplateBinding
import com.classflow.databinding.ItemTaskCheckboxRowBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ModuleTemplateFragment : Fragment() {

    private var _binding: FragmentModuleTemplateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SyllabusSetupViewModel by activityViewModels()

    private var module1StartMs: Long? = null
    private val dateFmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    private val configs: LinkedHashMap<BuiltInTaskDef, TaskConfig> = linkedMapOf()
    private val rowBindings: MutableMap<BuiltInTaskDef, ItemTaskCheckboxRowBinding> = mutableMapOf()

    private val selectorBuiltForCount: MutableMap<BuiltInTaskDef, Int> = mutableMapOf()
    private val selectorBuiltForMode: MutableMap<BuiltInTaskDef, ModuleMode> = mutableMapOf()

    private val dayNames = listOf("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")
    private val typeNames = TaskType.values().map { it.label() }
    private val priorityNames = Priority.values().map { it.label() }
    private val moduleModeNames = ModuleMode.values().map { it.label }
    private val starterLabels = StarterPattern.values().map { it.label }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModuleTemplateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCourseName.text = viewModel.selectedCourseName

        BuiltInTaskDef.values().forEach { task -> configs[task] = task.makeDefaultConfig() }

        setupStarterPatternDropdown()
        buildCheckboxRows()
        setupMainListeners()
        applyPattern(StarterPattern.DISCUSSION_ASSIGNMENT, rebuildRows = false)
    }

    // ── Starter pattern ──────────────────────────────────────────────────────

    private fun setupStarterPatternDropdown() {
        binding.actStarterPattern.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, starterLabels)
        )
        binding.actStarterPattern.setText(starterLabels[0], false)
        binding.actStarterPattern.setOnItemClickListener { _, _, pos, _ ->
            applyPattern(StarterPattern.values()[pos], rebuildRows = false)
        }
    }

    private fun applyPattern(pattern: StarterPattern, rebuildRows: Boolean) {
        val count = currentModuleCount()
        pattern.applyDefaults(configs, count)
        binding.tvPatternDesc.text = pattern.description
        selectorBuiltForCount.clear()
        selectorBuiltForMode.clear()
        if (rebuildRows) buildCheckboxRows() else refreshAllRowVisuals()
        updatePreview()
    }

    // ── Checkbox row building ─────────────────────────────────────────────────

    private fun buildCheckboxRows() {
        binding.llAllTasks.removeAllViews()
        rowBindings.clear()
        selectorBuiltForCount.clear()
        selectorBuiltForMode.clear()

        for (task in BuiltInTaskDef.values()) {
            val rb = ItemTaskCheckboxRowBinding.inflate(layoutInflater)
            rb.cbTask.text = task.displayName
            rb.cbTask.isChecked = configs[task]?.checked ?: false
            wireCheckboxRow(rb, task)
            binding.llAllTasks.addView(rb.root)
            rowBindings[task] = rb
            updateRowVisuals(task)
        }
    }

    private fun wireCheckboxRow(rb: ItemTaskCheckboxRowBinding, task: BuiltInTaskDef) {
        rb.cbTask.setOnCheckedChangeListener { _, checked ->
            configs[task]?.checked = checked
            // Auto-fill middle module when single-module tasks are first checked
            val isSingleMidterm = task == BuiltInTaskDef.MIDTERM_EXAM || task == BuiltInTaskDef.MIDTERM_QUIZ
            if (isSingleMidterm && checked) {
                val config = configs[task]
                if (config != null && config.moduleNumbers.isBlank()) {
                    val count = currentModuleCount()
                    if (count > 0) config.moduleNumbers = (count / 2).coerceAtLeast(1).toString()
                }
            }
            selectorBuiltForCount.remove(task)
            selectorBuiltForMode.remove(task)
            updateRowVisuals(task)
            updatePreview()
        }
        rb.btnEdit.setOnClickListener { showEditDialog(task) }
    }

    private fun updateRowVisuals(task: BuiltInTaskDef) {
        val rb = rowBindings[task] ?: return
        val config = configs[task] ?: return

        rb.cbTask.isChecked = config.checked
        rb.tvSummary.text = buildSummary(config)

        val needsSelector = config.needsModuleSelector()
        val count = currentModuleCount()
        if (needsSelector && count > 0) {
            rebuildModuleSelectorIfNeeded(task)
            rb.llModuleSelector.visibility = View.VISIBLE
        } else {
            rb.llModuleSelector.visibility = View.GONE
        }
    }

    private fun refreshAllRowVisuals() {
        BuiltInTaskDef.values().forEach { updateRowVisuals(it) }
    }

    private fun buildSummary(config: TaskConfig): String {
        val day = dayNames.getOrNull(config.dueDow - 1) ?: "Sunday"
        val pri = config.priority.label()
        val mode = when (config.moduleMode) {
            ModuleMode.EVERY -> "Every module"
            ModuleMode.LAST_ONLY -> "Last module"
            ModuleMode.SPECIFIC ->
                if (config.moduleNumbers.isNotBlank()) "Modules ${config.moduleNumbers}"
                else "Selected modules (none)"
            ModuleMode.SINGLE ->
                if (config.moduleNumbers.isNotBlank()) "Module ${config.moduleNumbers}"
                else "Single module (none)"
        }
        return "$day · $pri · $mode"
    }

    // ── Module chip selector ──────────────────────────────────────────────────

    private fun rebuildModuleSelectorIfNeeded(task: BuiltInTaskDef) {
        val config = configs[task] ?: return
        val count = currentModuleCount()
        if (count < 1) return
        val mode = config.moduleMode
        if (selectorBuiltForCount[task] == count && selectorBuiltForMode[task] == mode) return
        rebuildModuleSelector(task)
    }

    private fun rebuildModuleSelector(task: BuiltInTaskDef) {
        val rb = rowBindings[task] ?: return
        val config = configs[task] ?: return
        val count = currentModuleCount()
        if (count < 1) return

        val singleSelect = config.moduleMode == ModuleMode.SINGLE
        val selected = config.moduleNumbers.parseToModuleSet(count)

        buildModuleChips(rb.llModuleSelector, count, singleSelect, selected) { newSelected ->
            config.moduleNumbers = newSelected.sorted().joinToString(",")
            rb.tvSummary.text = buildSummary(config)
            updatePreview()
        }

        selectorBuiltForCount[task] = count
        selectorBuiltForMode[task] = config.moduleMode
    }

    private fun buildModuleChips(
        container: ViewGroup,
        moduleCount: Int,
        singleSelect: Boolean,
        selectedModules: Set<Int>,
        onChanged: (Set<Int>) -> Unit
    ) {
        container.removeAllViews()
        val ctx = requireContext()
        var suppress = false  // guard for bulk All/Clear operations

        // Label row with action buttons
        val headerRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val label = TextView(ctx).apply {
            text = if (singleSelect) "Module:" else "Modules:"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        headerRow.addView(label)

        // ChipGroup declared here so All/Clear can reference it
        val chipGroup = ChipGroup(ctx).apply {
            isSingleSelection = singleSelect
            isSelectionRequired = false
            chipSpacingHorizontal = 4.dp()
            chipSpacingVertical = 4.dp()
        }

        if (!singleSelect) {
            headerRow.addView(makeActionTextView(ctx, "All") {
                suppress = true
                for (i in 0 until chipGroup.childCount) {
                    (chipGroup.getChildAt(i) as? Chip)?.isChecked = true
                }
                suppress = false
                onChanged((1..moduleCount).toSet())
            })
        }

        headerRow.addView(makeActionTextView(ctx, "Clear") {
            suppress = true
            chipGroup.clearCheck()
            suppress = false
            onChanged(emptySet())
        })

        container.addView(headerRow)

        // Chip color state lists
        val primaryColor = ContextCompat.getColor(ctx, R.color.primary)
        val surfaceColor = ContextCompat.getColor(ctx, R.color.surface)
        val strokeColor = ContextCompat.getColor(ctx, R.color.text_secondary)
        val checkedBg = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(primaryColor, surfaceColor)
        )
        val checkedText = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(Color.WHITE, ContextCompat.getColor(ctx, R.color.text_primary))
        )
        val checkedStroke = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(primaryColor, strokeColor)
        )
        // Subtle ripple: semi-transparent primary
        val ripple = ColorStateList.valueOf(Color.argb(40, 61, 90, 254))

        for (moduleNum in 1..moduleCount) {
            val chip = Chip(ctx).apply {
                id = View.generateViewId()
                tag = moduleNum
                text = moduleNum.toString()
                isCheckable = true
                isChecked = moduleNum in selectedModules
                textSize = 13f
                setEnsureMinTouchTargetSize(false)
                chipMinHeight = 32.dp().toFloat()
                elevation = 0f
                stateListAnimator = null
                chipBackgroundColor = checkedBg
                setTextColor(checkedText)
                chipStrokeColor = checkedStroke
                chipStrokeWidth = 1.dp().toFloat()
                rippleColor = ripple
            }
            chipGroup.addView(chip)
        }

        // Listener after chips added — won't fire for initial checked states
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (suppress) return@setOnCheckedStateChangeListener
            val selected = checkedIds.mapNotNull { id ->
                group.findViewById<Chip>(id)?.tag as? Int
            }.toSet()
            onChanged(selected)
        }

        container.addView(chipGroup)
    }

    private fun makeActionTextView(
        ctx: android.content.Context,
        label: String,
        onClick: () -> Unit
    ): TextView = TextView(ctx).apply {
        text = label
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        setTextColor(ContextCompat.getColor(ctx, R.color.primary))
        val hPad = 8.dp()
        val vPad = 4.dp()
        setPadding(hPad, vPad, hPad, vPad)
        isClickable = true
        isFocusable = true
        val attrs = intArrayOf(android.R.attr.selectableItemBackgroundBorderless)
        val ta = ctx.obtainStyledAttributes(attrs)
        background = ta.getDrawable(0)
        ta.recycle()
        setOnClickListener { onClick() }
    }

    // ── Edit dialog ───────────────────────────────────────────────────────────

    private fun showEditDialog(task: BuiltInTaskDef) {
        val config = configs[task] ?: return
        val dialogBinding = DialogEditTaskRuleBinding.inflate(layoutInflater)

        dialogBinding.etTitlePattern.setText(config.titlePattern)

        dialogBinding.actType.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, typeNames)
        )
        dialogBinding.actType.setText(config.taskType.label(), false)

        dialogBinding.actPriority.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, priorityNames)
        )
        dialogBinding.actPriority.setText(config.priority.label(), false)

        dialogBinding.actDueDay.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, dayNames)
        )
        dialogBinding.actDueDay.setText(dayNames.getOrNull(config.dueDow - 1) ?: "Sunday", false)

        dialogBinding.actModuleMode.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, moduleModeNames)
        )
        dialogBinding.actModuleMode.setText(config.moduleMode.label, false)

        // Title pattern section toggle
        dialogBinding.btnCustomizeTitle.setOnClickListener {
            val show = dialogBinding.tilTitlePattern.visibility != View.VISIBLE
            dialogBinding.tilTitlePattern.visibility = if (show) View.VISIBLE else View.GONE
            dialogBinding.btnCustomizeTitle.text = if (show) "▾ Customize title" else "▸ Customize title"
        }

        // Module selector in dialog
        var dialogModuleNumbers = config.moduleNumbers

        fun refreshDialogSelector(mode: ModuleMode) {
            val container = dialogBinding.flModuleSelectorContainer
            val needsSelector = mode == ModuleMode.SPECIFIC || mode == ModuleMode.SINGLE
            container.visibility = if (needsSelector) View.VISIBLE else View.GONE
            container.removeAllViews()
            if (!needsSelector) return
            val count = currentModuleCount()
            if (count < 1) return
            val singleSelect = mode == ModuleMode.SINGLE
            val selected = dialogModuleNumbers.parseToModuleSet(count)
            val holder = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
            }
            buildModuleChips(holder, count, singleSelect, selected) { newSelected ->
                dialogModuleNumbers = newSelected.sorted().joinToString(",")
            }
            container.addView(holder)
        }

        refreshDialogSelector(config.moduleMode)

        dialogBinding.actModuleMode.setOnItemClickListener { _, _, pos, _ ->
            refreshDialogSelector(ModuleMode.values()[pos])
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit ${task.displayName}")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                config.titlePattern = dialogBinding.etTitlePattern.text?.toString()?.trim()
                    ?.takeIf { it.isNotBlank() } ?: config.titlePattern

                typeNames.indexOf(dialogBinding.actType.text.toString())
                    .takeIf { it >= 0 }?.let { config.taskType = TaskType.values()[it] }

                priorityNames.indexOf(dialogBinding.actPriority.text.toString())
                    .takeIf { it >= 0 }?.let { config.priority = Priority.values()[it] }

                dayNames.indexOf(dialogBinding.actDueDay.text.toString())
                    .takeIf { it >= 0 }?.let { config.dueDow = it + 1 }

                moduleModeNames.indexOf(dialogBinding.actModuleMode.text.toString())
                    .takeIf { it >= 0 }?.let { config.moduleMode = ModuleMode.values()[it] }

                if (config.moduleMode == ModuleMode.SPECIFIC || config.moduleMode == ModuleMode.SINGLE) {
                    config.moduleNumbers = dialogModuleNumbers
                }

                selectorBuiltForCount.remove(task)
                selectorBuiltForMode.remove(task)
                updateRowVisuals(task)
                updatePreview()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ── Main form listeners ───────────────────────────────────────────────────

    private fun setupMainListeners() {
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
        binding.btnPickStartDate.setOnClickListener { showDatePicker() }
        binding.btnGenerate.setOnClickListener { onGenerate() }

        binding.etModuleCount.addTextChangedListener(simpleWatcher {
            selectorBuiltForCount.clear()
            refreshAllRowVisuals()
            updatePreview()
        })
    }

    // ── Date picker ───────────────────────────────────────────────────────────

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        module1StartMs?.let { cal.timeInMillis = it }
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selected = Calendar.getInstance().apply {
                    set(year, month, day, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                module1StartMs = selected.timeInMillis
                binding.tvStartDate.text = dateFmt.format(selected.time)
                updatePreview()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // ── Preview ───────────────────────────────────────────────────────────────

    private fun updatePreview() {
        val count = currentModuleCount()
        if (count <= 0) { binding.tvPreview.text = "Enter number of modules above."; return }
        if (module1StartMs == null) { binding.tvPreview.text = "Select a start date to preview."; return }

        val enabled = configs.entries.filter { it.value.checked }
        if (enabled.isEmpty()) { binding.tvPreview.text = "Select at least one task type."; return }

        var total = 0
        val lines = mutableListOf<String>()
        for ((task, config) in enabled) {
            val n = when (config.moduleMode) {
                ModuleMode.EVERY -> count
                ModuleMode.LAST_ONLY -> 1
                ModuleMode.SPECIFIC -> config.moduleNumbers.parseToModuleSet(count).size
                ModuleMode.SINGLE ->
                    if (config.moduleNumbers.trim().toIntOrNull()?.let { it in 1..count } == true) 1 else 0
            }
            if (n == 0) continue
            total += n
            val modeStr = when (config.moduleMode) {
                ModuleMode.EVERY -> "×$count"
                ModuleMode.LAST_ONLY -> "last"
                ModuleMode.SPECIFIC -> "modules ${config.moduleNumbers}"
                ModuleMode.SINGLE -> "module ${config.moduleNumbers}"
            }
            lines += "• ${task.displayName} [$modeStr]"
        }

        binding.tvPreview.text = if (total == 0) "No valid tasks configured."
        else "$total tasks to generate:\n${lines.joinToString("\n")}"
    }

    // ── Generate ──────────────────────────────────────────────────────────────

    private fun onGenerate() {
        val count = currentModuleCount()
        if (count < 1) {
            Toast.makeText(requireContext(), "Enter number of modules (at least 1).", Toast.LENGTH_SHORT).show()
            return
        }
        val startMs = module1StartMs
        if (startMs == null) {
            Toast.makeText(requireContext(), "Pick a Module 1 start date.", Toast.LENGTH_SHORT).show()
            return
        }

        val enabled = configs.entries.filter { it.value.checked }
        if (enabled.isEmpty()) {
            Toast.makeText(requireContext(), "Select at least one task type.", Toast.LENGTH_SHORT).show()
            return
        }

        for ((task, config) in enabled) {
            when (config.moduleMode) {
                ModuleMode.SPECIFIC -> {
                    if (config.moduleNumbers.parseToModuleSet(count).isEmpty()) {
                        Toast.makeText(requireContext(),
                            "Select at least one module for ${task.displayName}.", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
                ModuleMode.SINGLE -> {
                    val n = config.moduleNumbers.trim().toIntOrNull()
                    if (n == null || n < 1 || n > count) {
                        Toast.makeText(requireContext(),
                            "Select one module for ${task.displayName}.", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
                else -> {}
            }
        }

        val drafts = mutableListOf<SyllabusTaskDraft>()
        for ((_, config) in enabled) {
            val modules: List<Int> = when (config.moduleMode) {
                ModuleMode.EVERY -> (1..count).toList()
                ModuleMode.LAST_ONLY -> listOf(count)
                ModuleMode.SPECIFIC -> config.moduleNumbers.parseToModuleSet(count).sorted()
                ModuleMode.SINGLE -> {
                    val n = config.moduleNumbers.trim().toIntOrNull() ?: continue
                    listOf(n)
                }
            }
            if (modules.isEmpty()) continue
            for (module in modules) {
                val title = config.titlePattern.replace("{n}", module.toString()).trim()
                if (title.isBlank()) continue
                val weekStartMs = startMs + (module - 1).toLong() * 7 * 24 * 60 * 60 * 1000L
                drafts += SyllabusTaskDraft(
                    title = title,
                    dueDate = dateForDayInWeek(weekStartMs, config.dueDow),
                    type = config.taskType,
                    priority = config.priority
                )
            }
        }

        if (drafts.isEmpty()) {
            Toast.makeText(requireContext(), "No tasks to generate. Check your selections.", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.appendDrafts(drafts)
        Toast.makeText(requireContext(),
            "${drafts.size} tasks generated. Review and edit before saving.", Toast.LENGTH_LONG).show()
        findNavController().popBackStack()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun currentModuleCount(): Int =
        binding.etModuleCount.text?.toString()?.trim()?.toIntOrNull() ?: 0

    private fun dateForDayInWeek(weekStartMs: Long, targetDow: Int): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = weekStartMs }
        val startDow = cal.get(Calendar.DAY_OF_WEEK)
        var offset = targetDow - startDow
        if (offset < 0) offset += 7
        cal.add(Calendar.DAY_OF_YEAR, offset)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun simpleWatcher(afterChanged: () -> Unit): TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) = Unit
        override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) = Unit
        override fun afterTextChanged(s: Editable?) { afterChanged() }
    }

    private fun Int.dp(): Int = (this * resources.displayMetrics.density + 0.5f).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// ── File-level extensions ─────────────────────────────────────────────────────

private fun TaskConfig.needsModuleSelector(): Boolean =
    checked && (moduleMode == ModuleMode.SPECIFIC || moduleMode == ModuleMode.SINGLE)

private fun String.parseToModuleSet(maxModule: Int): Set<Int> =
    if (isBlank()) emptySet()
    else split(",").mapNotNull { it.trim().toIntOrNull() }
        .filter { it in 1..maxModule }.toSet()

private fun TaskType.label() = when (this) {
    TaskType.ASSIGNMENT -> "Assignment"
    TaskType.DISCUSSION -> "Discussion"
    TaskType.RESPONSES -> "Responses"
    TaskType.QUIZ -> "Quiz"
    TaskType.EXAM -> "Exam"
    TaskType.PROJECT -> "Project"
    TaskType.OTHER -> "Other"
}

private fun Priority.label() =
    name.lowercase().replaceFirstChar { it.uppercase() }
