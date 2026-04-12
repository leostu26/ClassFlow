package com.classflow.ui.home;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\u0018\u0000 \u00122\u0012\u0012\u0004\u0012\u00020\u0002\u0012\b\u0012\u00060\u0003R\u00020\u00000\u0001:\u0002\u0011\u0012B\u001b\u0012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ\u001c\u0010\t\u001a\u00060\u0003R\u00020\u00002\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u001c\u0010\u000e\u001a\u00020\u00062\n\u0010\u000f\u001a\u00060\u0003R\u00020\u00002\u0006\u0010\u0010\u001a\u00020\rH\u0016R\u001a\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/classflow/ui/home/HomeTaskAdapter;", "Landroidx/recyclerview/widget/ListAdapter;", "Lcom/classflow/data/model/TaskWithCourseName;", "Lcom/classflow/ui/home/HomeTaskAdapter$ViewHolder;", "onItemClick", "Lkotlin/Function1;", "", "<init>", "(Lkotlin/jvm/functions/Function1;)V", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "", "onBindViewHolder", "holder", "position", "ViewHolder", "DiffCallback", "app_debug"})
public final class HomeTaskAdapter extends androidx.recyclerview.widget.ListAdapter<com.classflow.data.model.TaskWithCourseName, com.classflow.ui.home.HomeTaskAdapter.ViewHolder> {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function1<com.classflow.data.model.TaskWithCourseName, kotlin.Unit> onItemClick = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.classflow.ui.home.HomeTaskAdapter.DiffCallback DiffCallback = null;
    
    public HomeTaskAdapter(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.classflow.data.model.TaskWithCourseName, kotlin.Unit> onItemClick) {
        super(null);
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.classflow.ui.home.HomeTaskAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.classflow.ui.home.HomeTaskAdapter.ViewHolder holder, int position) {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u0018\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u0002H\u0016J\u0018\u0010\t\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u0002H\u0016\u00a8\u0006\n"}, d2 = {"Lcom/classflow/ui/home/HomeTaskAdapter$DiffCallback;", "Landroidx/recyclerview/widget/DiffUtil$ItemCallback;", "Lcom/classflow/data/model/TaskWithCourseName;", "<init>", "()V", "areItemsTheSame", "", "a", "b", "areContentsTheSame", "app_debug"})
    public static final class DiffCallback extends androidx.recyclerview.widget.DiffUtil.ItemCallback<com.classflow.data.model.TaskWithCourseName> {
        
        private DiffCallback() {
            super();
        }
        
        @java.lang.Override()
        public boolean areItemsTheSame(@org.jetbrains.annotations.NotNull()
        com.classflow.data.model.TaskWithCourseName a, @org.jetbrains.annotations.NotNull()
        com.classflow.data.model.TaskWithCourseName b) {
            return false;
        }
        
        @java.lang.Override()
        public boolean areContentsTheSame(@org.jetbrains.annotations.NotNull()
        com.classflow.data.model.TaskWithCourseName a, @org.jetbrains.annotations.NotNull()
        com.classflow.data.model.TaskWithCourseName b) {
            return false;
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/classflow/ui/home/HomeTaskAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "binding", "Lcom/classflow/databinding/ItemTaskHomeBinding;", "<init>", "(Lcom/classflow/ui/home/HomeTaskAdapter;Lcom/classflow/databinding/ItemTaskHomeBinding;)V", "bind", "", "item", "Lcom/classflow/data/model/TaskWithCourseName;", "app_debug"})
    public final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final com.classflow.databinding.ItemTaskHomeBinding binding = null;
        
        public ViewHolder(@org.jetbrains.annotations.NotNull()
        com.classflow.databinding.ItemTaskHomeBinding binding) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull()
        com.classflow.data.model.TaskWithCourseName item) {
        }
    }
}