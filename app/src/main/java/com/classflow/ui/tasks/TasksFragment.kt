package com.classflow.ui.tasks

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.classflow.R
import com.classflow.databinding.FragmentTasksBinding

class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TasksViewModel by viewModels()
    private val args: TasksFragmentArgs by navArgs()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCourseTitle.text = args.courseName
        viewModel.setCourseId(args.courseId)

        setupToolbarMenu()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbarMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_tasks, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_add_task -> {
                        val action = TasksFragmentDirections
                            .actionTasksFragmentToAddTaskFragment(args.courseId, args.courseName)
                        findNavController().navigate(action)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onChecked = { task, _ -> viewModel.toggleComplete(task) },
            onItemClick = { task ->
                val action = TasksFragmentDirections
                    .actionTasksFragmentToTaskDetailFragment(
                        taskId = task.id,
                        courseName = args.courseName
                    )
                findNavController().navigate(action)
            },
            onDeleteClick = { task -> viewModel.deleteTask(task) }
        )
        binding.rvTasks.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
            binding.tvNoTasks.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
            binding.rvTasks.visibility = if (tasks.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.pendingCount.observe(viewLifecycleOwner) { pending ->
            viewModel.totalCount.observe(viewLifecycleOwner) { total ->
                binding.tvProgress.text = "${total - pending}/$total completed"
                val progress = if (total > 0) ((total - pending) * 100 / total) else 0
                binding.progressBar.progress = progress
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

