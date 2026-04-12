package com.classflow.data;

import androidx.annotation.NonNull;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenDelegate;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;
import com.classflow.data.dao.CourseDao;
import com.classflow.data.dao.CourseDao_Impl;
import com.classflow.data.dao.TaskDao;
import com.classflow.data.dao.TaskDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class ClassFlowDatabase_Impl extends ClassFlowDatabase {
  private volatile CourseDao _courseDao;

  private volatile TaskDao _taskDao;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(1, "49a2e6b8c68abbbc1885a28c5552c422", "9816fba175b5275f159be0e75aead621") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `courses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `code` TEXT NOT NULL, `instructor` TEXT NOT NULL, `color` TEXT NOT NULL, `schedule` TEXT NOT NULL, `room` TEXT NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `tasks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `courseId` INTEGER NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `dueDate` INTEGER NOT NULL, `isCompleted` INTEGER NOT NULL, `priority` TEXT NOT NULL, `type` TEXT NOT NULL, FOREIGN KEY(`courseId`) REFERENCES `courses`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_tasks_courseId` ON `tasks` (`courseId`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '49a2e6b8c68abbbc1885a28c5552c422')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `courses`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `tasks`");
      }

      @Override
      public void onCreate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      public void onOpen(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(connection);
      }

      @Override
      public void onPreMigrate(@NonNull final SQLiteConnection connection) {
        DBUtil.dropFtsSyncTriggers(connection);
      }

      @Override
      public void onPostMigrate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      @NonNull
      public RoomOpenDelegate.ValidationResult onValidateSchema(
          @NonNull final SQLiteConnection connection) {
        final Map<String, TableInfo.Column> _columnsCourses = new HashMap<String, TableInfo.Column>(7);
        _columnsCourses.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCourses.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCourses.put("code", new TableInfo.Column("code", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCourses.put("instructor", new TableInfo.Column("instructor", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCourses.put("color", new TableInfo.Column("color", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCourses.put("schedule", new TableInfo.Column("schedule", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCourses.put("room", new TableInfo.Column("room", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysCourses = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesCourses = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCourses = new TableInfo("courses", _columnsCourses, _foreignKeysCourses, _indicesCourses);
        final TableInfo _existingCourses = TableInfo.read(connection, "courses");
        if (!_infoCourses.equals(_existingCourses)) {
          return new RoomOpenDelegate.ValidationResult(false, "courses(com.classflow.data.model.Course).\n"
                  + " Expected:\n" + _infoCourses + "\n"
                  + " Found:\n" + _existingCourses);
        }
        final Map<String, TableInfo.Column> _columnsTasks = new HashMap<String, TableInfo.Column>(8);
        _columnsTasks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("courseId", new TableInfo.Column("courseId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("dueDate", new TableInfo.Column("dueDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("isCompleted", new TableInfo.Column("isCompleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("priority", new TableInfo.Column("priority", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysTasks = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysTasks.add(new TableInfo.ForeignKey("courses", "CASCADE", "NO ACTION", Arrays.asList("courseId"), Arrays.asList("id")));
        final Set<TableInfo.Index> _indicesTasks = new HashSet<TableInfo.Index>(1);
        _indicesTasks.add(new TableInfo.Index("index_tasks_courseId", false, Arrays.asList("courseId"), Arrays.asList("ASC")));
        final TableInfo _infoTasks = new TableInfo("tasks", _columnsTasks, _foreignKeysTasks, _indicesTasks);
        final TableInfo _existingTasks = TableInfo.read(connection, "tasks");
        if (!_infoTasks.equals(_existingTasks)) {
          return new RoomOpenDelegate.ValidationResult(false, "tasks(com.classflow.data.model.Task).\n"
                  + " Expected:\n" + _infoTasks + "\n"
                  + " Found:\n" + _existingTasks);
        }
        return new RoomOpenDelegate.ValidationResult(true, null);
      }
    };
    return _openDelegate;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final Map<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final Map<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "courses", "tasks");
  }

  @Override
  public void clearAllTables() {
    super.performClear(true, "courses", "tasks");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CourseDao.class, CourseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TaskDao.class, TaskDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final Set<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public CourseDao courseDao() {
    if (_courseDao != null) {
      return _courseDao;
    } else {
      synchronized(this) {
        if(_courseDao == null) {
          _courseDao = new CourseDao_Impl(this);
        }
        return _courseDao;
      }
    }
  }

  @Override
  public TaskDao taskDao() {
    if (_taskDao != null) {
      return _taskDao;
    } else {
      synchronized(this) {
        if(_taskDao == null) {
          _taskDao = new TaskDao_Impl(this);
        }
        return _taskDao;
      }
    }
  }
}
