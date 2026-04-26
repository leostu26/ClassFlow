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
import com.classflow.ui.settings.SettingsRepository
import java.text.SimpleDateFormat
import java.util.*

class AddTaskFragment : Fragment() {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTaskViewModel by viewModels()
    private val args: AddTaskFragmentArgs by navArgs()

    private var selectedDueDate: Long = 0L
    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    private val typeValues = TaskType.values()
    private val priorityValues = Priority.values()

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

        setupDropdowns()
        setupDatePicker()

        binding.btnSaveTask.setOnClickListener { saveTask() }
        binding.btnCancel.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupDropdowns() {
        val defaults = SettingsRepository(requireContext()).getUserSettings()

        val typeLabels = typeValues.map { it.label() }
        binding.actType.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, typeLabels)
        )
        val defaultTypeIdx = typeValues
            .indexOfFirst { it.name.equals(defaults.defaultTaskType, ignoreCase = true) }
            .coerceAtLeast(0)
        binding.actType.setText(typeLabels[defaultTypeIdx], false)

        val priorityLabels = priorityValues.map { it.label() }
        binding.actPriority.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, priorityLabels)
        )
        val defaultPriorityIdx = priorityValues
            .indexOfFirst { it.name.equals(defaults.defaultPriority, ignoreCase = true) }
            .coerceAtLeast(0)
        binding.actPriority.setText(priorityLabels[defaultPriorityIdx], false)
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

        if (title.isEmpty()) {
            binding.etTaskTitle.error = "Task title is required"
            return
        }

        val selectedType = binding.actType.text.toString()
        val type = typeValues.firstOrNull { it.label() == selectedType } ?: TaskType.ASSIGNMENT

        val selectedPriority = binding.actPriority.text.toString()
        val priority = priorityValues.firstOrNull { it.label() == selectedPriority } ?: Priority.MEDIUM

        viewModel.saveTask(args.courseId, args.courseName, title,
            binding.etTaskDescription.text.toString().trim(),
            selectedDueDate, priority, type)
        Toast.makeText(requireContext(), "Task added!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private fun TaskType.label(): String = name.lowercase().replaceFirstChar { it.uppercase() }
private fun Priority.label(): String = name.lowercase().replaceFirstChar { it.uppercase() }
