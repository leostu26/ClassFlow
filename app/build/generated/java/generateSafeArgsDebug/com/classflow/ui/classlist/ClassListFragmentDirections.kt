package com.classflow.ui.classlist

import android.os.Bundle
import androidx.`annotation`.CheckResult
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.classflow.R
import kotlin.Int
import kotlin.Long
import kotlin.String

public class ClassListFragmentDirections private constructor() {
  private data class ActionClassListFragmentToTasksFragment(
    public val courseId: Long,
    public val courseName: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_classListFragment_to_tasksFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putLong("courseId", this.courseId)
        result.putString("courseName", this.courseName)
        return result
      }
  }

  public companion object {
    @CheckResult
    public fun actionClassListFragmentToAddClassFragment(): NavDirections = ActionOnlyNavDirections(R.id.action_classListFragment_to_addClassFragment)

    @CheckResult
    public fun actionClassListFragmentToTasksFragment(courseId: Long, courseName: String): NavDirections = ActionClassListFragmentToTasksFragment(courseId, courseName)
  }
}
