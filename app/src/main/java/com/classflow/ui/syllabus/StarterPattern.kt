package com.classflow.ui.syllabus

enum class StarterPattern(val label: String, val description: String) {
    DISCUSSION_ASSIGNMENT(
        "Discussion + Assignment",
        "Discussion post, peer responses, and a weekly assignment per module."
    ),
    DISCUSSION_QUIZ(
        "Discussion + Quiz",
        "Discussion post, peer responses, and a quiz every module."
    ),
    HOMEWORK_QUIZ(
        "Homework + Quiz",
        "Weekly homework assignment and a quiz every module."
    ),
    LAB_PROGRAMMING(
        "Lab / Programming",
        "Programming exercise each module with optional quizzes and exams."
    ),
    PROJECT_BASED(
        "Project-Based",
        "Discussion, project milestones (last module default), and optional final exam."
    ),
    EXAM_BASED(
        "Exam-Based",
        "Study material each module, midterm exam, and final exam."
    ),
    CUSTOM(
        "Custom",
        "No preset — current selections are preserved."
    );

    fun applyDefaults(configs: MutableMap<BuiltInTaskDef, TaskConfig>, moduleCount: Int) {
        // Custom preserves whatever the user has already configured
        if (this == CUSTOM) return

        // Reset everything to factory defaults before applying pattern
        BuiltInTaskDef.values().forEach { task ->
            configs[task]?.let { c ->
                c.checked = false
                c.moduleNumbers = ""
                c.titlePattern = task.titleTemplate
                c.taskType = task.taskType
                c.priority = task.defaultPriority
                c.dueDow = task.defaultDueDow
                c.moduleMode = task.defaultMode
            }
        }

        when (this) {
            DISCUSSION_ASSIGNMENT -> {
                configs[BuiltInTaskDef.DISCUSSION_POST]?.checked = true
                configs[BuiltInTaskDef.DISCUSSION_RESPONSES]?.checked = true
                configs[BuiltInTaskDef.ASSIGNMENT]?.checked = true
            }
            DISCUSSION_QUIZ -> {
                configs[BuiltInTaskDef.DISCUSSION_POST]?.checked = true
                configs[BuiltInTaskDef.DISCUSSION_RESPONSES]?.checked = true
                configs[BuiltInTaskDef.QUIZ]?.apply {
                    checked = true
                    moduleMode = ModuleMode.EVERY  // quiz every module for this pattern
                }
            }
            HOMEWORK_QUIZ -> {
                configs[BuiltInTaskDef.HOMEWORK]?.checked = true
                configs[BuiltInTaskDef.QUIZ]?.apply {
                    checked = true
                    moduleMode = ModuleMode.EVERY  // quiz every module for this pattern
                }
            }
            LAB_PROGRAMMING -> {
                configs[BuiltInTaskDef.DISCUSSION_POST]?.checked = true
                configs[BuiltInTaskDef.DISCUSSION_RESPONSES]?.checked = true
                configs[BuiltInTaskDef.PROGRAMMING_EXERCISE]?.checked = true
            }
            PROJECT_BASED -> {
                configs[BuiltInTaskDef.DISCUSSION_POST]?.checked = true
                configs[BuiltInTaskDef.DISCUSSION_RESPONSES]?.checked = true
                configs[BuiltInTaskDef.PROJECT]?.apply {
                    checked = true
                    // Default to last module so the selector shows something useful
                    if (moduleCount > 0) moduleNumbers = moduleCount.toString()
                }
                // Final exam left unchecked — user opts in manually
            }
            EXAM_BASED -> {
                configs[BuiltInTaskDef.ASSIGNMENT]?.checked = true
                configs[BuiltInTaskDef.MIDTERM_EXAM]?.apply {
                    checked = true
                    moduleNumbers = (moduleCount / 2).coerceAtLeast(1).toString()
                }
                configs[BuiltInTaskDef.FINAL_EXAM]?.checked = true
            }
            CUSTOM -> { /* handled by early return above */ }
        }
    }
}
