package com.classflow.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.classflow.R
import com.classflow.data.model.TaskWithCourseName
import com.classflow.databinding.FragmentHomeBinding
import com.classflow.util.TaskSwipeCallback
import com.google.android.material.snackbar.Snackbar
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

        setupToolbarMenu()
        setupGreeting()
        setupRecyclerViews()
        observeViewModel()

        binding.btnViewClasses.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_classListFragment)
        }

        binding.cardSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchTasksFragment)
        }

        binding.btnViewAllTasks.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_classListFragment)
        }

        binding.btnSeeAllToday.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_classListFragment)
        }

        binding.btnSeeAllFuture.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_classListFragment)
        }
    }

    private fun setupToolbarMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> {
                        findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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

    private fun navigateToDetail(item: TaskWithCourseName) {
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

        attachSwipeActions()
    }

    private fun attachSwipeActions() {
        fun attach(rv: RecyclerView, getItem: (Int) -> TaskWithCourseName?) {
            val callback = TaskSwipeCallback(
                onSwipeLeft = { pos ->
                    val item = getItem(pos)
                    if (item != null) {
                        if (item.isCompleted) {
                            Snackbar.make(binding.root, "Already marked complete", Snackbar.LENGTH_SHORT).show()
                            rv.adapter?.notifyItemChanged(pos)
                        } else {
                            viewModel.setTaskCompleted(item.taskId, true)
                            Snackbar.make(binding.root, "Task marked complete", Snackbar.LENGTH_LONG)
                                .setAction("Undo") { viewModel.setTaskCompleted(item.taskId, false) }
                                .show()
                        }
                    }
                },
                onSwipeRight = { pos ->
                    val item = getItem(pos)
                    if (item != null) navigateToDetail(item)
                }
            )
            ItemTouchHelper(callback).attachToRecyclerView(rv)
        }

        attach(binding.rvDueToday) { pos -> todayAdapter.currentList.getOrNull(pos) }
        attach(binding.rvUpcomingTasks) { pos -> weekAdapter.currentList.getOrNull(pos) }
        attach(binding.rvFutureTasks) { pos -> futureAdapter.currentList.getOrNull(pos) }
    }

    private fun observeViewModel() {
        viewModel.courseCount.observe(viewLifecycleOwner) { count ->
            binding.tvCourseCount.text = "$count ${if (count == 1) "Course" else "Courses"}"
        }

        viewModel.pendingTaskCount.observe(viewLifecycleOwner) { count ->
            binding.tvPendingCount.text = "$count Pending"
        }

        viewModel.tasksDueToday.observe(viewLifecycleOwner) { tasks ->
            todayAdapter.submitList(tasks.take(3))
            binding.btnSeeAllToday.visibility = if (tasks.size > 3) View.VISIBLE else View.GONE
            if (tasks.isEmpty()) {
                binding.tvNoToday.visibility = View.VISIBLE
                binding.rvDueToday.visibility = View.GONE
            } else {
                binding.tvNoToday.visibility = View.GONE
                binding.rvDueToday.visibility = View.VISIBLE
            }
        }

        viewModel.tasksDueThisWeek.observe(viewLifecycleOwner) { tasks ->
            weekAdapter.submitList(tasks.take(3))
            binding.btnViewAllTasks.visibility = if (tasks.size > 3) View.VISIBLE else View.GONE
            if (tasks.isEmpty()) {
                binding.tvNoWeek.visibility = View.VISIBLE
                binding.rvUpcomingTasks.visibility = View.GONE
            } else {
                binding.tvNoWeek.visibility = View.GONE
                binding.rvUpcomingTasks.visibility = View.VISIBLE
            }
        }

        viewModel.tasksFuture.observe(viewLifecycleOwner) { tasks ->
            futureAdapter.submitList(tasks.take(2))
            binding.btnSeeAllFuture.visibility = if (tasks.size > 2) View.VISIBLE else View.GONE
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
