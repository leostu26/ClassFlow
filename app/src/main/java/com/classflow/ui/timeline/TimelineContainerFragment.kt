package com.classflow.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.classflow.R
import com.classflow.databinding.FragmentTimelineContainerBinding
import com.classflow.ui.gantt.GanttChartFragment
import com.classflow.ui.workload.WorkloadFragment

class TimelineContainerFragment : Fragment() {

    private var _binding: FragmentTimelineContainerBinding? = null
    private val binding get() = _binding!!

    private var currentTab = TAB_GANTT

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimelineContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentTab = savedInstanceState?.getString(KEY_TAB) ?: TAB_GANTT

        if (childFragmentManager.findFragmentByTag(TAB_GANTT) == null) {
            // First creation: add both child fragments, hide workload
            val ganttFrag = GanttChartFragment()
            val workloadFrag = WorkloadFragment()
            childFragmentManager.beginTransaction()
                .add(R.id.tabContentContainer, ganttFrag, TAB_GANTT)
                .add(R.id.tabContentContainer, workloadFrag, TAB_WORKLOAD)
                .hide(workloadFrag)
                .commitNow()
        }

        // After rotation the FM restores show/hide state, but enforce it explicitly
        // to match whatever tab was saved (in case the restored state differs).
        applyTabVisibility(currentTab)

        // Sync toggle to current tab without firing the listener
        var suppressToggle = false
        val targetId = if (currentTab == TAB_GANTT) R.id.btnTabGantt else R.id.btnTabWorkload
        if (binding.tabToggle.checkedButtonId != targetId) {
            suppressToggle = true
            binding.tabToggle.check(targetId)
            suppressToggle = false
        }

        binding.tabToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked && !suppressToggle) {
                currentTab = if (checkedId == R.id.btnTabGantt) TAB_GANTT else TAB_WORKLOAD
                applyTabVisibility(currentTab)
            }
        }
    }

    private fun applyTabVisibility(tab: String) {
        val gantt = childFragmentManager.findFragmentByTag(TAB_GANTT) ?: return
        val workload = childFragmentManager.findFragmentByTag(TAB_WORKLOAD) ?: return
        childFragmentManager.beginTransaction().apply {
            if (tab == TAB_GANTT) { show(gantt); hide(workload) }
            else { show(workload); hide(gantt) }
        }.commitNow()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TAB, currentTab)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAB_GANTT = "gantt"
        private const val TAB_WORKLOAD = "workload"
        private const val KEY_TAB = "tab"
    }
}
