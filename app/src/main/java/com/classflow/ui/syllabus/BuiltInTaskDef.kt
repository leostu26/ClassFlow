package com.classflow.ui.syllabus

import com.classflow.data.model.Priority
import com.classflow.data.model.TaskType
import java.util.Calendar

enum class BuiltInTaskDef(
    val displayName: String,
    val titleTemplate: String,
    val taskType: TaskType,
    val defaultPriority: Priority,
    val defaultDueDow: Int,
    val defaultMode: ModuleMode
) {
    DISCUSSION_POST(
        "Discussion Post", "Module {n} Discussion Post",
        TaskType.DISCUSSION, Priority.MEDIUM, Calendar.THURSDAY, ModuleMode.EVERY
    ),
    DISCUSSION_RESPONSES(
        "Discussion Responses", "Module {n} Discussion Responses",
        TaskType.RESPONSES, Priority.LOW, Calendar.SUNDAY, ModuleMode.EVERY
    ),
    ASSIGNMENT(
        "Assignment", "Module {n} Assignment",
        TaskType.ASSIGNMENT, Priority.MEDIUM, Calendar.SUNDAY, ModuleMode.EVERY
    ),
    MIDTERM_QUIZ(
        "Midterm Quiz", "Module {n} Midterm Quiz",
        TaskType.QUIZ, Priority.HIGH, Calendar.SUNDAY, ModuleMode.SINGLE
    ),
    QUIZ(
        "Quiz", "Module {n} Quiz",
        TaskType.QUIZ, Priority.HIGH, Calendar.SUNDAY, ModuleMode.SPECIFIC
    ),
    FINAL_QUIZ(
        "Final Quiz", "Module {n} Final Quiz",
        TaskType.QUIZ, Priority.HIGH, Calendar.SUNDAY, ModuleMode.LAST_ONLY
    ),
    HOMEWORK(
        "Homework", "Module {n} Homework",
        TaskType.ASSIGNMENT, Priority.MEDIUM, Calendar.FRIDAY, ModuleMode.EVERY
    ),
    PROGRAMMING_EXERCISE(
        "Programming Exercise", "Module {n} Programming Exercise",
        TaskType.PROJECT, Priority.MEDIUM, Calendar.SUNDAY, ModuleMode.EVERY
    ),
    LAB(
        "Lab", "Module {n} Lab",
        TaskType.PROJECT, Priority.MEDIUM, Calendar.SUNDAY, ModuleMode.EVERY
    ),
    EXAM(
        "Exam", "Module {n} Exam",
        TaskType.EXAM, Priority.HIGH, Calendar.SUNDAY, ModuleMode.SPECIFIC
    ),
    FINAL_EXAM(
        "Final Exam", "Module {n} Final Exam",
        TaskType.EXAM, Priority.HIGH, Calendar.SUNDAY, ModuleMode.LAST_ONLY
    ),
    PROJECT(
        "Project", "Module {n} Project",
        TaskType.PROJECT, Priority.HIGH, Calendar.SUNDAY, ModuleMode.SPECIFIC
    ),
    PROJECT_PAPER(
        "Project Paper", "Module {n} Project Paper",
        TaskType.PROJECT, Priority.HIGH, Calendar.SUNDAY, ModuleMode.SINGLE
    ),
    MIDTERM_EXAM(
        "Midterm Exam", "Module {n} Midterm Exam",
        TaskType.EXAM, Priority.HIGH, Calendar.SUNDAY, ModuleMode.SINGLE
    );

    fun makeDefaultConfig() = TaskConfig(
        checked = false,
        titlePattern = titleTemplate,
        taskType = taskType,
        priority = defaultPriority,
        dueDow = defaultDueDow,
        moduleMode = defaultMode
    )
}
