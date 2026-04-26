package com.classflow.data.repository;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u001a\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u00072\u0006\u0010\u0017\u001a\u00020\u0018J\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00072\u0006\u0010\u0017\u001a\u00020\u0018J\u0014\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00072\u0006\u0010\u0017\u001a\u00020\u0018J\"\u0010\u001b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u00072\u0006\u0010\u001c\u001a\u00020\u00182\u0006\u0010\u001d\u001a\u00020\u0018J\"\u0010\u001e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001f0\b0\u00072\u0006\u0010\u001c\u001a\u00020\u00182\u0006\u0010\u001d\u001a\u00020\u0018J\u001a\u0010 \u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001f0\b0\u00072\u0006\u0010!\u001a\u00020\u0018J\u0018\u0010\"\u001a\u0004\u0018\u00010\t2\u0006\u0010#\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010$J\u0016\u0010%\u001a\u00020\u00182\u0006\u0010&\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\'J\u001c\u0010(\u001a\u00020)2\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0086@\u00a2\u0006\u0002\u0010+J\u001c\u0010,\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\u0017\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010$J\u0014\u0010-\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0086@\u00a2\u0006\u0002\u0010.J\u0016\u0010/\u001a\u00020)2\u0006\u0010&\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\'J\u0016\u00100\u001a\u00020)2\u0006\u0010&\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\'J\u001e\u00101\u001a\u00020)2\u0006\u0010#\u001a\u00020\u00182\u0006\u00102\u001a\u000203H\u0086@\u00a2\u0006\u0002\u00104J\u000e\u00105\u001a\u00020)H\u0086@\u00a2\u0006\u0002\u0010.R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001d\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000bR\u001d\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00120\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000bR\u001d\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00120\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000b\u00a8\u00066"}, d2 = {"Lcom/classflow/data/repository/TaskRepository;", "", "taskDao", "Lcom/classflow/data/dao/TaskDao;", "<init>", "(Lcom/classflow/data/dao/TaskDao;)V", "allTasks", "Landroidx/lifecycle/LiveData;", "", "Lcom/classflow/data/model/Task;", "getAllTasks", "()Landroidx/lifecycle/LiveData;", "upcomingTasks", "getUpcomingTasks", "totalPendingCount", "", "getTotalPendingCount", "allTasksWithCourseInfo", "Lcom/classflow/data/model/TaskWithCourseInfo;", "getAllTasksWithCourseInfo", "allTasksWithCourseInfoForSearch", "getAllTasksWithCourseInfoForSearch", "getTasksForCourse", "courseId", "", "getPendingTaskCount", "getTotalTaskCount", "getTasksDueSoon", "start", "end", "getTasksWithCourseNameDueSoon", "Lcom/classflow/data/model/TaskWithCourseName;", "getTasksWithCourseNameFuture", "afterDate", "getTaskById", "taskId", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "task", "(Lcom/classflow/data/model/Task;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertAll", "", "tasks", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTasksForCourseOnce", "getAllTasksOnce", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "delete", "setCompleted", "completed", "", "(JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAllTasks", "app_debug"})
public final class TaskRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.classflow.data.dao.TaskDao taskDao = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.Task>> allTasks = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.Task>> upcomingTasks = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.Integer> totalPendingCount = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.TaskWithCourseInfo>> allTasksWithCourseInfo = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.TaskWithCourseInfo>> allTasksWithCourseInfoForSearch = null;
    
    public TaskRepository(@org.jetbrains.annotations.NotNull()
    com.classflow.data.dao.TaskDao taskDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.Task>> getAllTasks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.Task>> getUpcomingTasks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Integer> getTotalPendingCount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.TaskWithCourseInfo>> getAllTasksWithCourseInfo() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.TaskWithCourseInfo>> getAllTasksWithCourseInfoForSearch() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.Task>> getTasksForCourse(long courseId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Integer> getPendingTaskCount(long courseId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Integer> getTotalTaskCount(long courseId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.Task>> getTasksDueSoon(long start, long end) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.TaskWithCourseName>> getTasksWithCourseNameDueSoon(long start, long end) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.TaskWithCourseName>> getTasksWithCourseNameFuture(long afterDate) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getTaskById(long taskId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.classflow.data.model.Task> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.classflow.data.model.Task task, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertAll(@org.jetbrains.annotations.NotNull()
    java.util.List<com.classflow.data.model.Task> tasks, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getTasksForCourseOnce(long courseId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.classflow.data.model.Task>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllTasksOnce(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.classflow.data.model.Task>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.classflow.data.model.Task task, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.classflow.data.model.Task task, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setCompleted(long taskId, boolean completed, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAllTasks(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}