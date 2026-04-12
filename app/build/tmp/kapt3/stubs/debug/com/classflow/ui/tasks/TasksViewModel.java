package com.classflow.ui.tasks;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\nJ\u000e\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u000eJ\u000e\u0010\u001c\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u000eR\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00120\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0010\u00a8\u0006\u001d"}, d2 = {"Lcom/classflow/ui/tasks/TasksViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "<init>", "(Landroid/app/Application;)V", "repository", "Lcom/classflow/data/repository/TaskRepository;", "_courseId", "Landroidx/lifecycle/MutableLiveData;", "", "tasks", "Landroidx/lifecycle/LiveData;", "", "Lcom/classflow/data/model/Task;", "getTasks", "()Landroidx/lifecycle/LiveData;", "pendingCount", "", "getPendingCount", "totalCount", "getTotalCount", "setCourseId", "", "courseId", "toggleComplete", "Lkotlinx/coroutines/Job;", "task", "deleteTask", "app_debug"})
public final class TasksViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.classflow.data.repository.TaskRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.Long> _courseId = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.Task>> tasks = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.Integer> pendingCount = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.Integer> totalCount = null;
    
    public TasksViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.Task>> getTasks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Integer> getPendingCount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Integer> getTotalCount() {
        return null;
    }
    
    public final void setCourseId(long courseId) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job toggleComplete(@org.jetbrains.annotations.NotNull()
    com.classflow.data.model.Task task) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job deleteTask(@org.jetbrains.annotations.NotNull()
    com.classflow.data.model.Task task) {
        return null;
    }
}