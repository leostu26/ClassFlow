package com.classflow.data.dao;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u001c\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u0014\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0014\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J$\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u000b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\u0007H\'J$\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u00040\u00032\u0006\u0010\u000b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\u0007H\'J\u0016\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u0016\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00100\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u000e\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00100\u0003H\'J\u0018\u0010\u0013\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0014\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u0016\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u0017\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u0016\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u0017\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u0016\u0010\u001b\u001a\u00020\u001a2\u0006\u0010\u0017\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u001e\u0010\u001c\u001a\u00020\u001a2\u0006\u0010\u0014\u001a\u00020\u00072\u0006\u0010\u001d\u001a\u00020\u001eH\u00a7@\u00a2\u0006\u0002\u0010\u001f\u00a8\u0006 \u00c0\u0006\u0003"}, d2 = {"Lcom/classflow/data/dao/TaskDao;", "", "getTasksForCourse", "Landroidx/lifecycle/LiveData;", "", "Lcom/classflow/data/model/Task;", "courseId", "", "getAllTasks", "getUpcomingTasks", "getTasksDueSoon", "start", "end", "getTasksWithCourseNameDueSoon", "Lcom/classflow/data/model/TaskWithCourseName;", "getPendingTaskCount", "", "getTotalTaskCount", "getTotalPendingCount", "getTaskById", "taskId", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertTask", "task", "(Lcom/classflow/data/model/Task;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateTask", "", "deleteTask", "setTaskCompleted", "completed", "", "(JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface TaskDao {
    
    @androidx.room.Query(value = "SELECT * FROM tasks WHERE courseId = :courseId ORDER BY dueDate ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.Task>> getTasksForCourse(long courseId);
    
    @androidx.room.Query(value = "SELECT * FROM tasks ORDER BY dueDate ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.Task>> getAllTasks();
    
    @androidx.room.Query(value = "SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC LIMIT 5")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.Task>> getUpcomingTasks();
    
    @androidx.room.Query(value = "SELECT * FROM tasks WHERE dueDate BETWEEN :start AND :end AND isCompleted = 0 ORDER BY dueDate ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.Task>> getTasksDueSoon(long start, long end);
    
    @androidx.room.Query(value = "\n        SELECT t.id as taskId, t.courseId, t.title, t.description, t.dueDate, \n               t.isCompleted, t.priority, t.type, c.name as courseName\n        FROM tasks t\n        INNER JOIN courses c ON t.courseId = c.id\n        WHERE t.dueDate BETWEEN :start AND :end AND t.isCompleted = 0\n        ORDER BY t.dueDate ASC\n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.TaskWithCourseName>> getTasksWithCourseNameDueSoon(long start, long end);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM tasks WHERE courseId = :courseId AND isCompleted = 0")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.lang.Integer> getPendingTaskCount(long courseId);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM tasks WHERE courseId = :courseId")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.lang.Integer> getTotalTaskCount(long courseId);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.lang.Integer> getTotalPendingCount();
    
    @androidx.room.Query(value = "SELECT * FROM tasks WHERE id = :taskId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTaskById(long taskId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.classflow.data.model.Task> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertTask(@org.jetbrains.annotations.NotNull()
    com.classflow.data.model.Task task, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateTask(@org.jetbrains.annotations.NotNull()
    com.classflow.data.model.Task task, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteTask(@org.jetbrains.annotations.NotNull()
    com.classflow.data.model.Task task, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE tasks SET isCompleted = :completed WHERE id = :taskId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setTaskCompleted(long taskId, boolean completed, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}