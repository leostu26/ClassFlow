# ClassFlow

**Version:** 1.0.0-beta

**ClassFlow** is an Android mobile app that helps students manage classes, assignments, deadlines, and academic progress in one place.

## Purpose
ClassFlow makes it easier for students to organize coursework and keep track of academic responsibilities throughout the term. Instead of relying on scattered notes, PDFs, and class portals, users can manage everything in a single app.

## Problem
Students often miss deadlines or lose track of assignments because course information is spread across multiple systems. ClassFlow addresses this by giving users a structured way to manage classes, tasks, and deadlines from one mobile platform.

## Platform and Tools
- **Platform:** Android
- **IDE:** Android Studio
- **Language:** Kotlin
- **Storage:** Room Database (SQLite)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Navigation:** Jetpack Navigation Component
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 35 (Android 15)

## Build Configuration
- Kotlin 2.0.21
- KSP (Kotlin Symbol Processing) — replaces kapt
- Gradle Kotlin DSL (.kts)
- Version Catalog (libs.versions.toml)

## Features Implemented (v1.0.0-beta)

### Core
- Add, view, and delete classes with course name, code, instructor, schedule, room, and color
- Add, view, edit, and delete tasks per class
- Set due dates, task type (Assignment, Quiz, Exam, Project, Other), and priority (Low, Medium, High)
- Mark tasks as completed with a checkbox
- Cascade delete — deleting a class removes all its tasks

### Home Screen
- Time-of-day greeting (Good Morning / Good Afternoon / Good Evening)
- Course count and pending task count summary cards
- **Due Today** section — tasks due today with date highlighted in red
- **Due This Week** section — tasks due in the next 7 days
- **Future Assignments** section — tasks beyond 7 days
- "Good job!" empty state message for each section when no tasks are due
- Tap any task card to open and edit it directly from the home screen
- Course name shown on each task card

### Classes Screen
- List of all courses with color indicator, code, instructor, and schedule
- Add Class button in the top toolbar
- Tap a class to open its task list
- Delete a class with the trash icon

### Tasks Screen
- Per-class task list with progress bar showing completion percentage
- Priority color bar on each task card (red = high, yellow = medium, green = low)
- Overdue dates highlighted in red
- Add Task button in the top toolbar
- Tap a task to open Task Detail for editing

### Task Detail Screen
- Edit title, description, due date, task type, priority, and completion status
- Delete task with confirmation
- Save changes navigates back automatically

### Navigation
- Bottom navigation bar (Home / Classes)
- Back navigation fixed — Home button reliably pops the back stack from any screen
- Toolbar back arrow on all sub-screens

### App Icon & Splash Screen
- Custom ClassFlow icon across all mipmap densities
- Branded splash screen on launch (1.5 second display)

## Planned Advanced Features
- Gantt chart view
- Simplified RACI matrix
- Weekly workload tracker
- Syllabus-to-task setup

## Screen Plan
| Screen | Description |
|---|---|
| Home | Greeting, stats, tasks due today / this week / future |
| Class List | All courses with color, code, instructor info |
| Add Class | Form with name, code, instructor, schedule, room, color picker |
| Tasks | Per-class task list with progress bar |
| Add Task | Form with title, description, date picker, type, priority |
| Task Detail | View and edit an existing task |
| Splash | Branded loading screen |

## Project Status
**Status:** Beta — Core features complete and functional

## GitHub
This project is tracked in GitHub Classroom.
- README: this file
- Wiki: project outline and documentation
- Repository: source code with full commit history
