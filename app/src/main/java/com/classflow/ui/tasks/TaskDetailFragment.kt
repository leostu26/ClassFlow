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
import com.classflow.databinding.FragmentTaskDetailBinding
import com.classflow.util.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskDetailViewModel by viewModels()
    private val args: TaskDetailFragmentArgs by navArgs()

    private var selectedDueDate: Long = 0L
    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCourseLabel.text = "Course: ${args.courseName}"

        setupSpinners()
        setupDatePicker()
        viewModel.loadTask(args.taskId)

        viewModel.task.observe(viewLifecycleOwner) { task ->
            task ?: return@observe
            binding.etTaskTitle.setText(task.title)
            binding.etDescription.setText(task.description)
            selectedDueDate = task.dueDate
            binding.tvSelectedDate.text = if (task.dueDate == 0L) "No date set"
                else dateFormatter.format(Date(task.dueDate))
            binding.spinnerPriority.setSelection(task.priority.ordinal)
            binding.spinnerType.setSelection(task.type.ordinal)
            binding.cbCompleted.isChecked = task.isCompleted
        }

        binding.btnSave.setOnClickListener { saveTask() }

        binding.btnDelete.setOnClickListener {
            viewModel.task.value?.let { task ->
                viewModel.deleteTask(task)
                Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun setupSpinners() {
        val priorityAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Priority.values().map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPriority.adapter = priorityAdapter

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
            if (selectedDueDate != 0L) cal.timeInMillis = selectedDueDate
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
            binding.etTaskTitle.error = "Title is required"
            return
        }
        val task = viewModel.task.value ?: return
        val priority = Priority.values()[binding.spinnerPriority.selectedItemPosition]
        val type = TaskType.values()[binding.spinnerType.selectedItemPosition]

        viewModel.saveTask(
            taskId = task.id,
            courseId = task.courseId,
            title = title,
            description = binding.etDescription.text.toString().trim(),
            dueDate = selectedDueDate,
            priority = priority,
            type = type,
            isCompleted = binding.cbCompleted.isChecked
        )
        Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
