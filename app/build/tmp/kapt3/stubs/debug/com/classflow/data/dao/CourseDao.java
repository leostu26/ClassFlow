package com.classflow.data.dao;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0018\u0010\u0006\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u000e\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u0003H\'\u00a8\u0006\u0012\u00c0\u0006\u0003"}, d2 = {"Lcom/classflow/data/dao/CourseDao;", "", "getAllCourses", "Landroidx/lifecycle/LiveData;", "", "Lcom/classflow/data/model/Course;", "getCourseById", "courseId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertCourse", "course", "(Lcom/classflow/data/model/Course;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateCourse", "", "deleteCourse", "getCourseCount", "", "app_debug"})
@androidx.room.Dao()
public abstract interface CourseDao {
    
    @androidx.room.Query(value = "SELECT * FROM courses ORDER BY name ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.classflow.data.model.Course>> getAllCourses();
    
    @androidx.room.Query(value = "SELECT * FROM courses WHERE id = :courseId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCourseById(long courseId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.classflow.data.model.Course> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertCourse(@org.jetbrains.annotations.NotNull()
    com.classflow.data.model.Course course, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateCourse(@org.jetbrains.annotations.NotNull()
    com.classflow.data.model.Course course, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteCourse(@org.jetbrains.annotations.NotNull()
    com.classflow.data.model.Course course, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM courses")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.lang.Integer> getCourseCount();
}