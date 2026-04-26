package com.classflow.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.classflow.R
import com.classflow.data.model.Priority
import com.classflow.data.model.TaskType
import com.classflow.databinding.FragmentTasksBinding
import com.classflow.util.TaskSwipeCallback
import com.google.android.material.snackbar.Snackbar

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
        setupSearchAndFilters()
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
                    R.id.action_syllabus_setup -> {
                        val action = TasksFragmentDirections
                            .actionTasksFragmentToSyllabusSetupFragment(args.courseId, args.courseName)
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

        val swipeCallback = TaskSwipeCallback(
            onSwipeLeft = { pos ->
                val task = taskAdapter.currentList.getOrNull(pos)
                if (task != null) {
                    if (task.isCompleted) {
                        Snackbar.make(binding.root, "Already marked complete", Snackbar.LENGTH_SHORT).show()
                        binding.rvTasks.adapter?.notifyItemChanged(pos)
                    } else {
                        viewModel.setTaskCompleted(task.id, true)
                        Snackbar.make(binding.root, "Task marked complete", Snackbar.LENGTH_LONG)
                            .setAction("Undo") { viewModel.setTaskCompleted(task.id, false) }
                            .show()
                    }
                }
            },
            onSwipeRight = { pos ->
                val task = taskAdapter.currentList.getOrNull(pos)
                if (task != null) {
                    val action = TasksFragmentDirections
                        .actionTasksFragmentToTaskDetailFragment(
                            taskId = task.id,
                            courseName = args.courseName
                        )
                    findNavController().navigate(action)
                }
            }
        )
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.rvTasks)
    }

    private fun setupSearchAndFilters() {
        binding.etSearch.doAfterTextChanged { viewModel.setSearchQuery(it?.toString() ?: "") }

        binding.chipPriority.setOnClickListener {
            PopupMenu(requireContext(), it).apply {
                menu.add(0, 0, 0, "All")
                Priority.values().forEachIndexed { i, p ->
                    menu.add(0, i + 1, i + 1, p.name.lowercase().replaceFirstChar { c -> c.uppercase() })
                }
                setOnMenuItemClickListener { item ->
                    viewModel.setPriorityFilter(
                        if (item.itemId == 0) null else Priority.values()[item.itemId - 1]
                    )
                    true
                }
                show()
            }
        }

        binding.chipType.setOnClickListener {
            PopupMenu(requireContext(), it).apply {
                menu.add(0, 0, 0, "All")
                TaskType.values().forEachIndexed { i, t ->
                    menu.add(0, i + 1, i + 1, t.name.lowercase().replaceFirstChar { c -> c.uppercase() })
                }
                setOnMenuItemClickListener { item ->
                    viewModel.setTypeFilter(
                        if (item.itemId == 0) null else TaskType.values()[item.itemId - 1]
                    )
                    true
                }
                show()
            }
        }

        binding.chipCompletion.setOnClickListener {
            PopupMenu(requireContext(), it).apply {
                menu.add(0, 0, 0, "All")
                menu.add(0, 1, 1, "Pending")
                menu.add(0, 2, 2, "Completed")
                setOnMenuItemClickListener { item ->
                    viewModel.setCompletionFilter(when (item.itemId) {
                        1 -> CompletionFilter.PENDING
                        2 -> CompletionFilter.COMPLETED
                        else -> CompletionFilter.ALL
                    })
                    true
                }
                show()
            }
        }

        binding.chipDue.setOnClickListener {
            PopupMenu(requireContext(), it).apply {
                menu.add(0, 0, 0, "All")
                menu.add(0, 1, 1, "Overdue")
                menu.add(0, 2, 2, "Due Today")
                menu.add(0, 3, 3, "Due This Week")
                menu.add(0, 4, 4, "No Date")
                setOnMenuItemClickListener { item ->
                    viewModel.setDueFilter(when (item.itemId) {
                        1 -> DueFilter.OVERDUE
                        2 -> DueFilter.DUE_TODAY
                        3 -> DueFilter.DUE_THIS_WEEK
                        4 -> DueFilter.NO_DATE
                        else -> DueFilter.ALL
                    })
                    true
                }
                show()
            }
        }

        binding.btnClearFilters.setOnClickListener {
            viewModel.clearFilters()
            binding.etSearch.text?.clear()
        }
    }

    private fun observeViewModel() {
        viewModel.filteredTasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
            if (tasks.isEmpty()) {
                binding.tvNoTasks.text = if (viewModel.isFiltering.value == true)
                    "No matching tasks."
                else
                    "No tasks yet.\nTap + to add an assignment!\nUse the 3 dots to bulk add tasks."
                binding.tvNoTasks.visibility = View.VISIBLE
                binding.rvTasks.visibility = View.GONE
            } else {
                binding.tvNoTasks.visibility = View.GONE
                binding.rvTasks.visibility = View.VISIBLE
            }
        }

        viewModel.isFiltering.observe(viewLifecycleOwner) { filtering ->
            binding.btnClearFilters.visibility = if (filtering) View.VISIBLE else View.GONE
        }

        viewModel.priorityFilter.observe(viewLifecycleOwner) { priority ->
            binding.chipPriority.isChecked = priority != null
            binding.chipPriority.text = if (priority == null) "Priority"
                else "Priority: ${priority.name.lowercase().replaceFirstChar { it.uppercase() }}"
        }

        viewModel.typeFilter.observe(viewLifecycleOwner) { type ->
            binding.chipType.isChecked = type != null
            binding.chipType.text = if (type == null) "Type"
                else "Type: ${type.name.lowercase().replaceFirstChar { it.uppercase() }}"
        }

        viewModel.completionFilter.observe(viewLifecycleOwner) { completion ->
            binding.chipCompletion.isChecked = completion != CompletionFilter.ALL
            binding.chipCompletion.text = when (completion) {
                CompletionFilter.ALL -> "Status"
                CompletionFilter.PENDING -> "Status: Pending"
                CompletionFilter.COMPLETED -> "Status: Completed"
            }
        }

        viewModel.dueFilter.observe(viewLifecycleOwner) { due ->
            binding.chipDue.isChecked = due != DueFilter.ALL
            binding.chipDue.text = when (due) {
                DueFilter.ALL -> "Due"
                DueFilter.OVERDUE -> "Due: Overdue"
                DueFilter.DUE_TODAY -> "Due: Today"
                DueFilter.DUE_THIS_WEEK -> "Due: This Week"
                DueFilter.NO_DATE -> "Due: No Date"
            }
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
