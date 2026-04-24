package com.classflow.ui.tasks

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.classflow.data.model.Priority
import com.classflow.data.model.TaskType
import com.classflow.databinding.FragmentAddTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class AddTaskFragment : Fragment() {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTaskViewModel by viewModels()
    private val args: AddTaskFragmentArgs by navArgs()

    private var selectedDueDate: Long = 0L
    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCourseLabel.text = "Adding task to: ${args.courseName}"

        setupSpinners()
        setupDatePicker()

        binding.btnSaveTask.setOnClickListener { saveTask() }
        binding.btnCancel.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupSpinners() {
        val priorityAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Priority.values().map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPriority.adapter = priorityAdapter
        binding.spinnerPriority.setSelection(1) // Default MEDIUM

        val typeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            TaskType.values().map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
        )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = typeAdapter
    }

    private fun setupDatePicker() {
        binding.btnPickDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    cal.set(year, month, day, 23, 59, 59)
                    selectedDueDate = cal.timeInMillis
                    binding.tvSelectedDate.text = dateFormatter.format(cal.time)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun saveTask() {
        val title = binding.etTaskTitle.text.toString().trim()
        val description = binding.etTaskDescription.text.toString().trim()

        if (title.isEmpty()) {
            binding.etTaskTitle.error = "Task title is required"
            return
        }

        val priority = Priority.values()[binding.spinnerPriority.selectedItemPosition]
        val type = TaskType.values()[binding.spinnerType.selectedItemPosition]

        viewModel.saveTask(args.courseId, title, description, selectedDueDate, priority, type)
        Toast.makeText(requireContext(), "Task added!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
