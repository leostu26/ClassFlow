package com.classflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.classflow.data.dao.CourseDao
import com.classflow.data.dao.TaskDao
import com.classflow.data.model.Course
import com.classflow.data.model.Task
import com.classflow.util.Converters

@Database(
    entities = [Course::class, Task::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ClassFlowDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: ClassFlowDatabase? = null

        fun getDatabase(context: Context): ClassFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClassFlowDatabase::class.java,
                    "classflow_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
