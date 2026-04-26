package com.classflow.ui.home

import android.os.Bundle
import androidx.`annotation`.CheckResult
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.classflow.R
import kotlin.Int
import kotlin.Long
import kotlin.String

public class HomeFragmentDirections private constructor() {
  private data class ActionHomeFragmentToTaskDetailFragment(
    public val taskId: Long,
    public val courseName: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_homeFragment_to_taskDetailFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putLong("taskId", this.taskId)
        result.putString("courseName", this.courseName)
        return result
      }
  }

  public companion object {
    @CheckResult
    public fun actionHomeFragmentToClassListFragment(): NavDirections = ActionOnlyNavDirections(R.id.action_homeFragment_to_classListFragment)

    @CheckResult
    public fun actionHomeFragmentToTaskDetailFragment(taskId: Long, courseName: String): NavDirections = ActionHomeFragmentToTaskDetailFragment(taskId, courseName)

    @CheckResult
    public fun actionHomeFragmentToSettingsFragment(): NavDirections = ActionOnlyNavDirections(R.id.action_homeFragment_to_settingsFragment)

    @CheckResult
    public fun actionHomeFragmentToSearchTasksFragment(): NavDirections = ActionOnlyNavDirections(R.id.action_homeFragment_to_searchTasksFragment)
  }
}
