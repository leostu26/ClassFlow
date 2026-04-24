package com.classflow.ui.tasks

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.Long
import kotlin.String
import kotlin.jvm.JvmStatic

public data class TaskDetailFragmentArgs(
  public val taskId: Long,
  public val courseName: String,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putLong("taskId", this.taskId)
    result.putString("courseName", this.courseName)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("taskId", this.taskId)
    result.set("courseName", this.courseName)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): TaskDetailFragmentArgs {
      bundle.setClassLoader(TaskDetailFragmentArgs::class.java.classLoader)
      val __taskId : Long
      if (bundle.containsKey("taskId")) {
        __taskId = bundle.getLong("taskId")
      } else {
        throw IllegalArgumentException("Required argument \"taskId\" is missing and does not have an android:defaultValue")
      }
      val __courseName : String?
      if (bundle.containsKey("courseName")) {
        __courseName = bundle.getString("courseName")
        if (__courseName == null) {
          throw IllegalArgumentException("Argument \"courseName\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"courseName\" is missing and does not have an android:defaultValue")
      }
      return TaskDetailFragmentArgs(__taskId, __courseName)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): TaskDetailFragmentArgs {
      val __taskId : Long?
      if (savedStateHandle.contains("taskId")) {
        __taskId = savedStateHandle["taskId"]
        if (__taskId == null) {
          throw IllegalArgumentException("Argument \"taskId\" of type long does not support null values")
        }
      } else {
        throw IllegalArgumentException("Required argument \"taskId\" is missing and does not have an android:defaultValue")
      }
      val __courseName : String?
      if (savedStateHandle.contains("courseName")) {
        __courseName = savedStateHandle["courseName"]
        if (__courseName == null) {
          throw IllegalArgumentException("Argument \"courseName\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"courseName\" is missing and does not have an android:defaultValue")
      }
      return TaskDetailFragmentArgs(__taskId, __courseName)
    }
  }
}
