package com.classflow.ui.home;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\f0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u001d\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u00120\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000e\u00a8\u0006\u0015"}, d2 = {"Lcom/classflow/ui/home/HomeViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "<init>", "(Landroid/app/Application;)V", "courseRepository", "Lcom/classflow/data/repository/CourseRepository;", "taskRepository", "Lcom/classflow/data/repository/TaskRepository;", "courseCount", "Landroidx/lifecycle/LiveData;", "", "getCourseCount", "()Landroidx/lifecycle/LiveData;", "pendingTaskCount", "getPendingTaskCount", "tasksDueSoon", "", "Lcom/classflow/data/model/TaskWithCourseName;", "getTasksDueSoon", "app_debug"})
public final class HomeViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.classflow.data.repository.CourseRepository courseRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.classflow.data.repository.TaskRepository taskRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.Integer> courseCount = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.Integer> pendingTaskCount = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.TaskWithCourseName>> tasksDueSoon = null;
    
    public HomeViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Integer> getCourseCount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Integer> getPendingTaskCount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.TaskWithCourseName>> getTasksDueSoon() {
        return null;
    }
}