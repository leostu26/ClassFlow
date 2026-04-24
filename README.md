# ClassFlow

**Version:** 1.1.0-beta

**ClassFlow** is an Android mobile app that helps students manage classes, assignments, deadlines, timelines, and academic workload in one place.

## Purpose

ClassFlow makes it easier for students to organize coursework and keep track of academic responsibilities throughout the term. Instead of relying on scattered notes, PDFs, class portals, and separate planning tools, users can manage classes, tasks, deadlines, Gantt timelines, and weekly workload summaries from one mobile platform.

## Problem

Students often miss deadlines or lose track of assignments because course information is spread across multiple systems. ClassFlow addresses this by giving users a structured way to manage classes, tasks, deadlines, and academic workload from one mobile app.

## Screenshots

<div align="center">

| Splash | Home (Empty) | Home |
|:------:|:------------:|:----:|
| <img src="screenshots/splash.png" width="160"/> | <img src="screenshots/blankhome.png" width="160"/> | <img src="screenshots/home.png" width="160"/> |

| Classes | Tasks | Task Detail |
|:-------:|:-----:|:-----------:|
| <img src="screenshots/classes.png" width="160"/> | <img src="screenshots/tasks.png" width="160"/> | <img src="screenshots/task_detail.png" width="160"/> |

| Add Class | Gantt Timeline | Weekly Workload |
|:---------:|:--------------:|:---------------:|
| <img src="screenshots/add_class.png" width="160"/> | <img src="screenshots/gantt_timeline.png" width="160"/> | <img src="screenshots/weekly_workload.png" width="160"/> |

</div>

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

## Features Implemented (v1.1.0-beta)

### Core

- Add, view, and delete classes with course name, code, instructor, schedule, room, and color
- Add, view, edit, and delete tasks per class
- Set due dates, task type, and priority
- Supported task types:
  - Assignment
  - Quiz
  - Exam
  - Project
  - Other
- Supported priorities:
  - Low
  - Medium
  - High
- Mark tasks as completed with a checkbox
- Cascade delete — deleting a class removes all its tasks

### Home Screen

- Time-of-day greeting
- Course count and pending task count summary cards
- **Due Today** section — tasks due today with date highlighted
- **Due This Week** section — tasks due in the next 7 days
- **Future Assignments** section — tasks beyond 7 days
- Empty-state messages when no tasks are due
- Tap any task card to open and edit it directly from the home screen
- Course name shown on each task card

### Classes Screen

- List of all courses with color indicator, code, instructor, and schedule
- Add Class button in the top toolbar
- Tap a class to open its task list
- Delete a class with the trash icon

### Tasks Screen

- Per-class task list with progress bar showing completion percentage
- Priority color bar on each task card
- Overdue dates highlighted
- Add Task button in the top toolbar
- Tap a task to open Task Detail for editing

### Task Detail Screen

- Edit title, description, due date, task type, priority, and completion status
- Delete task with confirmation
- Save changes navigates back automatically

### Timeline / Gantt Chart View

ClassFlow now includes a mobile-friendly Gantt timeline for academic planning.

Implemented Gantt features:

- 2-week timeline window
- Previous 2 Weeks navigation
- Next 2 Weeks navigation
- Today button to return to the current timeline window
- Shared 14-day date header
- Workload density count badges under dates
- All Tasks view
- By Class view
- All Tasks view set as the default
- Task bars aligned to the shared date scale
- Task bars clipped at the visible timeline window edge
- Task duration estimated from task type and priority
- Class color dots for quick course identification
- Due-status chips
- Completed task styling
- Overdue task styling
- Empty-state handling for date windows with no visible tasks

The Gantt Chart View answers:

```text
When are my tasks happening?
```

### Weekly Workload Tracker

ClassFlow now includes a Weekly Workload Tracker inside the Timeline section.

Implemented workload features:

- Gantt / Workload tabs inside the Timeline section
- 1-week workload view
- Previous Week navigation
- Next Week navigation
- Today button
- Weekly workload score
- Workload levels:
  - Light
  - Moderate
  - Heavy
  - Overloaded
- Total active task count
- High-priority task count
- Due-today task count
- Completed task count
- Overdue task count when applicable
- Most loaded day
- Task type breakdown
- Daily workload breakdown
- Daily workload bars
- Completed tasks excluded from active workload scoring
- Empty-state handling for weeks with no tasks

The Weekly Workload Tracker answers:

```text
How heavy is my week?
```

### Navigation

- Bottom navigation bar:
  - Home
  - Classes
  - Timeline
- Timeline section includes:
  - Gantt
  - Workload
- Back navigation fixed — Home button reliably pops the back stack from any screen
- Toolbar back arrow on all sub-screens

### App Icon and Splash Screen

- Custom ClassFlow icon across all mipmap densities
- Branded splash screen on launch

## Advanced Features Status

### Implemented

1. **Gantt Chart View**
   - Added a mobile-friendly 2-week Gantt timeline.
   - Added All Tasks and By Class views.
   - Added task bars aligned to a shared date scale.
   - Added due-status chips, class color indicators, and workload density badges.

2. **Weekly Workload Tracker**
   - Added weekly workload scoring.
   - Added workload levels: Light, Moderate, Heavy, and Overloaded.
   - Added weekly navigation.
   - Added most loaded day, task type breakdown, and daily workload breakdown.

### Deferred to Next Week

3. **Syllabus Task Setup**
   - Manual bulk-entry syllabus setup was planned but not implemented this week.
   - This feature will allow users to add multiple syllabus tasks at once.
   - PDF parsing, OCR, and AI syllabus extraction are not part of the first version.

### Removed from Plan

4. **Simplified RACI Matrix**
   - Removed from the planned feature list.
   - ClassFlow is focused on individual student planning, not group-project role management.

### Added to Future Features

5. **Settings Screen**
   - Planned for a future update.
   - This screen may include theme options, notification preferences, default task priority, default task type, week-start preference, and app information.

## Planned / Future Features

### 1. Syllabus Task Setup

Manual bulk-entry feature for adding multiple syllabus tasks at once.

Planned first version:

- Select a class
- Add multiple task rows
- Enter task title, type, priority, due date, and optional description
- Review tasks before saving
- Create all tasks in one action
- Add validation for blank titles, missing dates, and possible duplicates

Future versions may include:

- Paste syllabus text and convert lines into task rows
- Syllabus PDF import
- OCR support
- AI-assisted syllabus parsing

### 2. Settings Screen

Planned settings options:

- Theme:
  - System
  - Light
  - Dark
- Default task priority:
  - Low
  - Medium
  - High
- Default task type:
  - Assignment
  - Quiz
  - Exam
  - Project
  - Other
- Week starts on:
  - Sunday
  - Monday
- Show completed tasks:
  - On
  - Off
- Notification reminders:
  - On
  - Off
- Reminder timing:
  - Same day
  - 1 day before
  - 3 days before
- About ClassFlow:
  - Version
  - Project description
  - GitHub repository link

### 3. Notification Reminders

Planned reminder feature for upcoming and overdue tasks.

Possible options:

- Due today reminders
- 1-day-before reminders
- 3-day-before reminders
- High-priority task reminders
- Overdue task reminders

### 4. Calendar View

Possible future calendar-based task view.

Planned concept:

- Monthly calendar
- Task count indicators on each day
- Tap a day to view tasks due that day
- Completed and overdue visual states

## Screen Plan

| Screen | Description |
|---|---|
| Splash | Branded loading screen |
| Home | Greeting, stats, tasks due today / this week / future |
| Class List | All courses with color, code, instructor info |
| Add Class | Form with name, code, instructor, schedule, room, color picker |
| Tasks | Per-class task list with progress bar |
| Add Task | Form with title, description, date picker, type, priority |
| Task Detail | View and edit an existing task |
| Timeline / Gantt | 2-week Gantt chart with All Tasks and By Class views |
| Weekly Workload | Weekly workload score, task breakdown, and daily workload summary |
| Syllabus Task Setup | Future manual bulk-entry screen for syllabus tasks |
| Settings | Future app settings and preferences screen |

## Current Development Update

This week, ClassFlow was expanded beyond the core class and task management features. The Gantt Chart View and Weekly Workload Tracker were added as advanced planning tools.

The Gantt Chart View provides a 2-week visual timeline with both All Tasks and By Class views. The Weekly Workload Tracker summarizes task load by week using task type, priority, due dates, and completion status.

The Manual Bulk Syllabus Task Setup feature was planned but not completed this week. It has been deferred to next week. A future Settings screen was also added to the project plan.

## Project Status

**Status:** Beta — Core features complete, Gantt Chart View implemented, Weekly Workload Tracker implemented, Syllabus Task Setup deferred.

## GitHub

This project is tracked in GitHub Classroom.

- README: this file
- Wiki: project outline and documentation
- Repository: source code with full commit history
