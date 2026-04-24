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

    private lateinit var todayAdapter: HomeTaskAdapter
    private lateinit var weekAdapter: HomeTaskAdapter
    private lateinit var futureAdapter: HomeTaskAdapter

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
        setupRecyclerViews()
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
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else      -> "Good Evening"
        }
        binding.tvGreeting.text = greeting
        binding.tvDate.text = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
            .format(Date())
    }

    private fun navigateToDetail(item: com.classflow.data.model.TaskWithCourseName) {
        val action = HomeFragmentDirections
            .actionHomeFragmentToTaskDetailFragment(
                taskId = item.taskId,
                courseName = item.courseName
            )
        findNavController().navigate(action)
    }

    private fun setupRecyclerViews() {
        todayAdapter = HomeTaskAdapter(highlightDateRed = true) { navigateToDetail(it) }
        binding.rvDueToday.apply {
            adapter = todayAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        weekAdapter = HomeTaskAdapter { navigateToDetail(it) }
        binding.rvUpcomingTasks.apply {
            adapter = weekAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        futureAdapter = HomeTaskAdapter { navigateToDetail(it) }
        binding.rvFutureTasks.apply {
            adapter = futureAdapter
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

        viewModel.tasksDueToday.observe(viewLifecycleOwner) { tasks ->
            todayAdapter.submitList(tasks)
            if (tasks.isEmpty()) {
                binding.tvNoToday.visibility = View.VISIBLE
                binding.rvDueToday.visibility = View.GONE
            } else {
                binding.tvNoToday.visibility = View.GONE
                binding.rvDueToday.visibility = View.VISIBLE
            }
        }

        viewModel.tasksDueThisWeek.observe(viewLifecycleOwner) { tasks ->
            weekAdapter.submitList(tasks)
            if (tasks.isEmpty()) {
                binding.tvNoWeek.visibility = View.VISIBLE
                binding.rvUpcomingTasks.visibility = View.GONE
            } else {
                binding.tvNoWeek.visibility = View.GONE
                binding.rvUpcomingTasks.visibility = View.VISIBLE
            }
        }

        viewModel.tasksFuture.observe(viewLifecycleOwner) { tasks ->
            futureAdapter.submitList(tasks)
            if (tasks.isEmpty()) {
                binding.tvNoFuture.visibility = View.VISIBLE
                binding.rvFutureTasks.visibility = View.GONE
            } else {
                binding.tvNoFuture.visibility = View.GONE
                binding.rvFutureTasks.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
