package com.classflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.classflow.data.dao.CourseDao
import com.classflow.data.dao.TaskDao
import com.classflow.data.model.Course
import com.classflow.data.model.Task
import com.classflow.util.Converters

@Database(
    entities = [Course::class, Task::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ClassFlowDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: ClassFlowDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE courses ADD COLUMN classMode TEXT NOT NULL DEFAULT 'In Person'")
                db.execSQL("ALTER TABLE courses ADD COLUMN meetingLink TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE courses ADD COLUMN platform TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): ClassFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClassFlowDatabase::class.java,
                    "classflow_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
