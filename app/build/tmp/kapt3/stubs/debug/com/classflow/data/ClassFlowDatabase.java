package com.classflow.data;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \b2\u00020\u0001:\u0001\bB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&\u00a8\u0006\t"}, d2 = {"Lcom/classflow/data/ClassFlowDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "courseDao", "Lcom/classflow/data/dao/CourseDao;", "taskDao", "Lcom/classflow/data/dao/TaskDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.classflow.data.model.Course.class, com.classflow.data.model.Task.class}, version = 1, exportSchema = false)
@androidx.room.TypeConverters(value = {com.classflow.util.Converters.class})
public abstract class ClassFlowDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.classflow.data.ClassFlowDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.classflow.data.ClassFlowDatabase.Companion Companion = null;
    
    public ClassFlowDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.classflow.data.dao.CourseDao courseDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.classflow.data.dao.TaskDao taskDao();
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\bR\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/classflow/data/ClassFlowDatabase$Companion;", "", "<init>", "()V", "INSTANCE", "Lcom/classflow/data/ClassFlowDatabase;", "getDatabase", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.classflow.data.ClassFlowDatabase getDatabase(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}