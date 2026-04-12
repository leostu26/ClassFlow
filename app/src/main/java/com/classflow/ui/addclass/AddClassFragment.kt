package com.classflow.ui.addclass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.classflow.databinding.FragmentAddClassBinding

class AddClassFragment : Fragment() {

    private var _binding: FragmentAddClassBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddClassViewModel by viewModels()

    private val colorOptions = listOf(
        "#4A90D9", "#E74C3C", "#2ECC71", "#F39C12",
        "#9B59B6", "#1ABC9C", "#E67E22", "#34495E"
    )
    private var selectedColor = "#4A90D9"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddClassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupColorPicker()

        binding.btnSaveClass.setOnClickListener {
            saveClass()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupColorPicker() {
        val colorViews = listOf(
            binding.color1, binding.color2, binding.color3, binding.color4,
            binding.color5, binding.color6, binding.color7, binding.color8
        )
        colorViews.forEachIndexed { index, view ->
            val color = colorOptions[index]
            view.setBackgroundColor(android.graphics.Color.parseColor(color))
            view.setOnClickListener {
                selectedColor = color
                colorViews.forEach { it.alpha = 0.4f }
                view.alpha = 1.0f
            }
        }
        colorViews[0].alpha = 1.0f
        colorViews.drop(1).forEach { it.alpha = 0.4f }
    }

    private fun saveClass() {
        val name = binding.etCourseName.text.toString().trim()
        val code = binding.etCourseCode.text.toString().trim()
        val instructor = binding.etInstructor.text.toString().trim()
        val schedule = binding.etSchedule.text.toString().trim()
        val room = binding.etRoom.text.toString().trim()

        if (name.isEmpty()) {
            binding.etCourseName.error = "Course name is required"
            return
        }
        if (code.isEmpty()) {
            binding.etCourseCode.error = "Course code is required"
            return
        }

        viewModel.saveCourse(name, code, instructor, schedule, room, selectedColor)
        Toast.makeText(requireContext(), "$name added!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
