package com.classflow.ui.syllabus

import android.app.DatePickerDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classflow.R
import com.classflow.data.model.Priority
import com.classflow.data.model.TaskType
import com.classflow.databinding.ItemSyllabusTaskRowBinding
import com.classflow.util.DateUtils
import java.util.Calendar

class SyllabusTaskRowAdapter(
    private val viewModel: SyllabusSetupViewModel
) : ListAdapter<SyllabusTaskDraft, SyllabusTaskRowAdapter.RowViewHolder>(DiffCallback) {

    private val typeLabels = TaskType.values().map { it.label() }
    private val priorityLabels = Priority.values().map { it.label() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val binding = ItemSyllabusTaskRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class RowViewHolder(val binding: ItemSyllabusTaskRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var isBinding = false
        var currentLocalId: String = ""

        private val titleWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isBinding) viewModel.updateTitle(currentLocalId, s?.toString() ?: "")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        private val descWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isBinding) viewModel.updateDescription(currentLocalId, s?.toString() ?: "")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        init {
            val ctx = binding.root.context

            binding.actType.setAdapter(
                ArrayAdapter(ctx, android.R.layout.simple_dropdown_item_1line, typeLabels)
            )
            // setOnItemClickListener fires only on user selection, not programmatic setText
            binding.actType.setOnItemClickListener { _, _, pos, _ ->
                viewModel.updateType(currentLocalId, TaskType.values()[pos])
            }

            binding.actPriority.setAdapter(
                ArrayAdapter(ctx, android.R.layout.simple_dropdown_item_1line, priorityLabels)
            )
            binding.actPriority.setOnItemClickListener { _, _, pos, _ ->
                viewModel.updatePriority(currentLocalId, Priority.values()[pos])
            }

            binding.etTitle.addTextChangedListener(titleWatcher)
            binding.etDescription.addTextChangedListener(descWatcher)
        }

        fun bind(draft: SyllabusTaskDraft, position: Int) {
            isBinding = true
            currentLocalId = draft.localId

            binding.tvRowNumber.text = "ROW ${position + 1}"

            if (binding.etTitle.text.toString() != draft.title) {
                binding.etTitle.setText(draft.title)
            }
            binding.tilTitle.error = draft.errors.firstOrNull()

            binding.actType.setText(draft.type.label(), false)
            binding.actPriority.setText(draft.priority.label(), false)

            if (draft.dueDate != null) {
                binding.tvDueDate.text = DateUtils.formatDate(draft.dueDate)
                val color = if (draft.dueDate < System.currentTimeMillis())
                    R.color.overdue else R.color.text_secondary
                binding.tvDueDate.setTextColor(ContextCompat.getColor(binding.root.context, color))
            } else {
                binding.tvDueDate.text = "No date selected"
                binding.tvDueDate.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.text_secondary)
                )
            }

            binding.llDateShortcuts.visibility = if (position > 0) View.VISIBLE else View.GONE

            if (binding.etDescription.text.toString() != draft.description) {
                binding.etDescription.setText(draft.description)
            }

            val allWarnings = draft.warnings
            if (allWarnings.isNotEmpty()) {
                binding.tvRowWarnings.visibility = View.VISIBLE
                binding.tvRowWarnings.text = allWarnings.joinToString("\n") { "⚠ $it" }
            } else {
                binding.tvRowWarnings.visibility = View.GONE
            }

            binding.root.post { isBinding = false }

            binding.btnRemove.setOnClickListener { viewModel.removeRow(currentLocalId) }
            binding.btnDuplicate.setOnClickListener { viewModel.duplicateRow(currentLocalId) }
            binding.btnClear.setOnClickListener { viewModel.clearRow(currentLocalId) }
            binding.btnPickDate.setOnClickListener { showDatePicker() }
            binding.btnCopyPrev.setOnClickListener { viewModel.copyPreviousDueDate(currentLocalId) }
            binding.btnPlus1.setOnClickListener { viewModel.addDaysToCurrentRow(currentLocalId, 1) }
            binding.btnPlus7.setOnClickListener { viewModel.addDaysToCurrentRow(currentLocalId, 7) }
        }

        private fun showDatePicker() {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                binding.root.context,
                { _, year, month, day ->
                    cal.set(year, month, day, 23, 59, 59)
                    viewModel.updateDueDate(currentLocalId, cal.timeInMillis)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<SyllabusTaskDraft>() {
        override fun areItemsTheSame(old: SyllabusTaskDraft, new: SyllabusTaskDraft) =
            old.localId == new.localId

        override fun areContentsTheSame(old: SyllabusTaskDraft, new: SyllabusTaskDraft) =
            old.dueDate == new.dueDate &&
                old.type == new.type &&
                old.priority == new.priority &&
                old.errors == new.errors &&
                old.warnings == new.warnings
    }
}

private fun TaskType.label(): String = name.lowercase().replaceFirstChar { it.uppercase() }
private fun Priority.label(): String = name.lowercase().replaceFirstChar { it.uppercase() }
