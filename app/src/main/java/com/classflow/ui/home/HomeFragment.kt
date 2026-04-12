package com.classflow.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.classflow.R
import com.classflow.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var taskAdapter: HomeTaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGreeting()
        setupRecyclerView()
        observeViewModel()

        binding.btnViewClasses.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_classListFragment)
        }

        binding.btnViewAllTasks.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_classListFragment)
        }
    }

    private fun setupGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
        binding.tvGreeting.text = greeting
        binding.tvDate.text = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
            .format(Date())
    }

    private fun setupRecyclerView() {
        taskAdapter = HomeTaskAdapter { taskWithCourse ->
            val action = HomeFragmentDirections
                .actionHomeFragmentToTaskDetailFragment(
                    taskId = taskWithCourse.taskId,
                    courseName = taskWithCourse.courseName
                )
            findNavController().navigate(action)
        }
        binding.rvUpcomingTasks.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewModel.courseCount.observe(viewLifecycleOwner) { count ->
            binding.tvCourseCount.text = "$count ${if (count == 1) "Course" else "Courses"}"
        }

        viewModel.pendingTaskCount.observe(viewLifecycleOwner) { count ->
            binding.tvPendingCount.text = "$count Pending"
        }

        viewModel.tasksDueSoon.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
            binding.tvNoUpcoming.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
            binding.rvUpcomingTasks.visibility = if (tasks.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

