package com.classflow.ui.syllabus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.classflow.R
import com.classflow.databinding.FragmentSyllabusSetupBinding
import com.google.android.material.snackbar.Snackbar

class SyllabusSetupFragment : Fragment() {

    private var _binding: FragmentSyllabusSetupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SyllabusSetupViewModel by activityViewModels()
    private val args: SyllabusSetupFragmentArgs by navArgs()
    private lateinit var adapter: SyllabusTaskRowAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSyllabusSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val existingDrafts = viewModel.drafts.value
        if (viewModel.selectedCourseId != args.courseId || existingDrafts.isNullOrEmpty()) {
            viewModel.resetForCourse(args.courseId, args.courseName)
        }

        binding.tvClassName.text = args.courseName

        setupRecyclerView()
        observeViewModel()
        setupButtons()
    }

    private fun setupRecyclerView() {
        adapter = SyllabusTaskRowAdapter(viewModel)
        binding.rvDrafts.adapter = adapter
        binding.rvDrafts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDrafts.itemAnimator = null
    }

    private fun observeViewModel() {
        var lastCount = 0
        viewModel.drafts.observe(viewLifecycleOwner) { drafts ->
            val grew = drafts.size > lastCount
            lastCount = drafts.size
            adapter.submitList(drafts.toList()) {
                if (grew && drafts.isNotEmpty()) {
                    binding.rvDrafts.scrollToPosition(drafts.size - 1)
                }
            }
            binding.tvRowCount.text = "${drafts.size} row${if (drafts.size == 1) "" else "s"}"
        }

        viewModel.creationSuccess.observe(viewLifecycleOwner) { count ->
            if (count != null && count > 0) {
                viewModel.consumeCreationSuccess()
                Snackbar.make(
                    binding.root,
                    "$count task${if (count == 1) "" else "s"} added successfully",
                    Snackbar.LENGTH_LONG
                ).show()
                findNavController().navigateUp()
            }
        }

        viewModel.submitError.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                viewModel.consumeSubmitError()
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.dateHint.observe(viewLifecycleOwner) { hint ->
            if (!hint.isNullOrBlank()) {
                viewModel.consumeDateHint()
                Toast.makeText(requireContext(), hint, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupButtons() {
        binding.btnAddRow.setOnClickListener {
            viewModel.addEmptyRow()
        }

        binding.btnSubmit.setOnClickListener {
            viewModel.submitValidRows()
        }

        binding.btnModuleTemplate.setOnClickListener {
            findNavController().navigate(
                R.id.action_syllabusSetupFragment_to_moduleTemplateFragment
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
