package com.classflow.ui.tasks;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J$\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010%2\b\u0010&\u001a\u0004\u0018\u00010\'H\u0016J\u001a\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020!2\b\u0010&\u001a\u0004\u0018\u00010\'H\u0016J\b\u0010+\u001a\u00020)H\u0002J\b\u0010,\u001a\u00020)H\u0002J\b\u0010-\u001a\u00020)H\u0002J\b\u0010.\u001a\u00020)H\u0016R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u00058BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0007\u0010\bR\u001b\u0010\t\u001a\u00020\n8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\fR\u001b\u0010\u000f\u001a\u00020\u00108BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0013\u0010\u0014\u001a\u0004\b\u0011\u0010\u0012R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001b0\u001aX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u001cR\u0016\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u001aX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u001f\u00a8\u0006/"}, d2 = {"Lcom/classflow/ui/tasks/AddTaskFragment;", "Landroidx/fragment/app/Fragment;", "<init>", "()V", "_binding", "Lcom/classflow/databinding/FragmentAddTaskBinding;", "binding", "getBinding", "()Lcom/classflow/databinding/FragmentAddTaskBinding;", "viewModel", "Lcom/classflow/ui/tasks/AddTaskViewModel;", "getViewModel", "()Lcom/classflow/ui/tasks/AddTaskViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "args", "Lcom/classflow/ui/tasks/AddTaskFragmentArgs;", "getArgs", "()Lcom/classflow/ui/tasks/AddTaskFragmentArgs;", "args$delegate", "Landroidx/navigation/NavArgsLazy;", "selectedDueDate", "", "dateFormatter", "Ljava/text/SimpleDateFormat;", "typeValues", "", "Lcom/classflow/data/model/TaskType;", "[Lcom/classflow/data/model/TaskType;", "priorityValues", "Lcom/classflow/data/model/Priority;", "[Lcom/classflow/data/model/Priority;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onViewCreated", "", "view", "setupDropdowns", "setupDatePicker", "saveTask", "onDestroyView", "app_debug"})
public final class AddTaskFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable()
    private com.classflow.databinding.FragmentAddTaskBinding _binding;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy viewModel$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.navigation.NavArgsLazy args$delegate = null;
    private long selectedDueDate = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.text.SimpleDateFormat dateFormatter = null;
    @org.jetbrains.annotations.NotNull()
    private final com.classflow.data.model.TaskType[] typeValues = null;
    @org.jetbrains.annotations.NotNull()
    private final com.classflow.data.model.Priority[] priorityValues = null;
    
    public AddTaskFragment() {
        super();
    }
    
    private final com.classflow.databinding.FragmentAddTaskBinding getBinding() {
        return null;
    }
    
    private final com.classflow.ui.tasks.AddTaskViewModel getViewModel() {
        return null;
    }
    
    private final com.classflow.ui.tasks.AddTaskFragmentArgs getArgs() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull()
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable()
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupDropdowns() {
    }
    
    private final void setupDatePicker() {
    }
    
    private final void saveTask() {
    }
    
    @java.lang.Override()
    public void onDestroyView() {
    }
}