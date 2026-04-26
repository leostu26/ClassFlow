package com.classflow.ui.syllabus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.classflow.R
import com.classflow.data.model.Priority
import com.classflow.databinding.FragmentSyllabusReviewBinding
import com.classflow.databinding.ItemSyllabusReviewTaskBinding
import com.classflow.util.DateUtils

class SyllabusReviewFragment : Fragment() {

    private var _binding: FragmentSyllabusReviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SyllabusSetupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSyllabusReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val drafts = viewModel.drafts.value.orEmpty()
        val totalWarnings = viewModel.getTotalWarnings()
        val hasErrors = viewModel.hasBlockingErrors()

        buildSummaryCard(drafts.size, totalWarnings, hasErrors)
        buildReviewList(drafts)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCreate.isEnabled = !hasErrors
        binding.btnCreate.setOnClickListener {
            viewModel.createTasks()
        }

        viewModel.creationSuccess.observe(viewLifecycleOwner) { count ->
            if (count != null && count > 0) {
                viewModel.consumeCreationSuccess()
                Toast.makeText(
                    requireContext(),
                    "$count syllabus task${if (count == 1) "" else "s"} created.",
                    Toast.LENGTH_LONG
                ).show()
                findNavController().popBackStack(R.id.tasksFragment, false)
            }
        }
    }

    private fun buildSummaryCard(count: Int, warnings: Int, hasErrors: Boolean) {
        binding.tvReviewSummary.text =
            "$count task${if (count == 1) "" else "s"} ready"

        if (warnings > 0) {
            binding.tvReviewWarnings.visibility = View.VISIBLE
            binding.tvReviewWarnings.text =
                "$warnings warning${if (warnings == 1) "" else "s"} — review before creating"
        } else {
            binding.tvReviewWarnings.visibility = View.GONE
        }

        if (hasErrors) {
            binding.tvReviewErrors.visibility = View.VISIBLE
            binding.tvReviewErrors.text = "Fix all errors before creating tasks"
        } else {
            binding.tvReviewErrors.visibility = View.GONE
        }
    }

    private fun buildReviewList(drafts: List<SyllabusTaskDraft>) {
        binding.llReviewItems.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        for (draft in drafts) {
            val itemBinding = ItemSyllabusReviewTaskBinding.inflate(inflater, binding.llReviewItems, false)

            itemBinding.tvReviewTitle.text = draft.title.ifBlank { "(No title)" }

            val dateText = if (draft.dueDate != null) DateUtils.formatShortDate(draft.dueDate)
            else "No date"
            itemBinding.tvReviewDate.text = dateText

            itemBinding.tvReviewType.text = draft.type.name.lowercase()
                .replaceFirstChar { it.uppercase() }

            itemBinding.tvReviewPriority.text = draft.priority.name.lowercase()
                .replaceFirstChar { it.uppercase() }

            val priorityColorRes = when (draft.priority) {
                Priority.HIGH -> R.color.priority_high
                Priority.MEDIUM -> R.color.priority_medium
                Priority.LOW -> R.color.priority_low
            }
            itemBinding.viewPriorityBar.setBackgroundColor(
                ContextCompat.getColor(requireContext(), priorityColorRes)
            )

            val allIssues = draft.errors + draft.warnings
            if (allIssues.isNotEmpty()) {
                itemBinding.tvReviewWarnings.visibility = View.VISIBLE
                itemBinding.tvReviewWarnings.text = allIssues.joinToString("\n") { "⚠ $it" }
                val issueColor = if (draft.errors.isNotEmpty()) "#EF4444" else "#F59E0B"
                itemBinding.tvReviewWarnings.setTextColor(
                    android.graphics.Color.parseColor(issueColor)
                )
            } else {
                itemBinding.tvReviewWarnings.visibility = View.GONE
            }

            binding.llReviewItems.addView(itemBinding.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
