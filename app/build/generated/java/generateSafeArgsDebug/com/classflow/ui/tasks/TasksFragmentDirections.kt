package com.classflow.ui.tasks

import android.os.Bundle
import androidx.`annotation`.CheckResult
import androidx.navigation.NavDirections
import com.classflow.R
import kotlin.Int
import kotlin.Long
import kotlin.String

public class TasksFragmentDirections private constructor() {
  private data class ActionTasksFragmentToAddTaskFragment(
    public val courseId: Long,
    public val courseName: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_tasksFragment_to_addTaskFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putLong("courseId", this.courseId)
        result.putString("courseName", this.courseName)
        return result
      }
  }

  private data class ActionTasksFragmentToTaskDetailFragment(
    public val taskId: Long,
    public val courseName: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_tasksFragment_to_taskDetailFragment

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
    public fun actionTasksFragmentToAddTaskFragment(courseId: Long, courseName: String): NavDirections = ActionTasksFragmentToAddTaskFragment(courseId, courseName)

    @CheckResult
    public fun actionTasksFragmentToTaskDetailFragment(taskId: Long, courseName: String): NavDirections = ActionTasksFragmentToTaskDetailFragment(taskId, courseName)
  }
}
