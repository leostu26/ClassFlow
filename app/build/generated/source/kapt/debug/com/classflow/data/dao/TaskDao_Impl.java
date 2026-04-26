package com.classflow.data.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.classflow.data.model.Priority;
import com.classflow.data.model.Task;
import com.classflow.data.model.TaskType;
import com.classflow.data.model.TaskWithCourseInfo;
import com.classflow.data.model.TaskWithCourseName;
import com.classflow.util.Converters;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Long;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class TaskDao_Impl implements TaskDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Task> __insertAdapterOfTask;

  private final Converters __converters = new Converters();

  private final EntityDeleteOrUpdateAdapter<Task> __deleteAdapterOfTask;

  private final EntityDeleteOrUpdateAdapter<Task> __updateAdapterOfTask;

  public TaskDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfTask = new EntityInsertAdapter<Task>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `tasks` (`id`,`courseId`,`title`,`description`,`dueDate`,`isCompleted`,`priority`,`type`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Task entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCourseId());
        if (entity.getTitle() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getTitle());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getDescription());
        }
        statement.bindLong(5, entity.getDueDate());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final String _tmp_1 = __converters.fromPriority(entity.getPriority());
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, _tmp_1);
        }
        final String _tmp_2 = __converters.fromTaskType(entity.getType());
        if (_tmp_2 == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, _tmp_2);
        }
      }
    };
    this.__deleteAdapterOfTask = new EntityDeleteOrUpdateAdapter<Task>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `tasks` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Task entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTask = new EntityDeleteOrUpdateAdapter<Task>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `tasks` SET `id` = ?,`courseId` = ?,`title` = ?,`description` = ?,`dueDate` = ?,`isCompleted` = ?,`priority` = ?,`type` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Task entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCourseId());
        if (entity.getTitle() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getTitle());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getDescription());
        }
        statement.bindLong(5, entity.getDueDate());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final String _tmp_1 = __converters.fromPriority(entity.getPriority());
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, _tmp_1);
        }
        final String _tmp_2 = __converters.fromTaskType(entity.getType());
        if (_tmp_2 == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, _tmp_2);
        }
        statement.bindLong(9, entity.getId());
      }
    };
  }

  @Override
  public Object insertTask(final Task task, final Continuation<? super Long> $completion) {
    if (task == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfTask.insertAndReturnId(_connection, task);
    }, $completion);
  }

  @Override
  public Object insertTasks(final List<Task> tasks, final Continuation<? super Unit> $completion) {
    if (tasks == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfTask.insert(_connection, tasks);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object deleteTask(final Task task, final Continuation<? super Unit> $completion) {
    if (task == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfTask.handle(_connection, task);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object updateTask(final Task task, final Continuation<? super Unit> $completion) {
    if (task == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfTask.handle(_connection, task);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public LiveData<List<Task>> getTasksForCourse(final long courseId) {
    final String _sql = "SELECT * FROM tasks WHERE courseId = ? ORDER BY dueDate ASC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"tasks"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, courseId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCourseId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "courseId");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfIsCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isCompleted");
        final int _columnIndexOfPriority = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "priority");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final List<Task> _result = new ArrayList<Task>();
        while (_stmt.step()) {
          final Task _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpCourseId;
          _tmpCourseId = _stmt.getLong(_columnIndexOfCourseId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpDueDate;
          _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate);
          final boolean _tmpIsCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsCompleted));
          _tmpIsCompleted = _tmp != 0;
          final Priority _tmpPriority;
          final String _tmp_1;
          if (_stmt.isNull(_columnIndexOfPriority)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfPriority);
          }
          _tmpPriority = __converters.toPriority(_tmp_1);
          final TaskType _tmpType;
          final String _tmp_2;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = __converters.toTaskType(_tmp_2);
          _item = new Task(_tmpId,_tmpCourseId,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpIsCompleted,_tmpPriority,_tmpType);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<Task>> getAllTasks() {
    final String _sql = "SELECT * FROM tasks ORDER BY dueDate ASC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"tasks"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCourseId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "courseId");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfIsCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isCompleted");
        final int _columnIndexOfPriority = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "priority");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final List<Task> _result = new ArrayList<Task>();
        while (_stmt.step()) {
          final Task _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpCourseId;
          _tmpCourseId = _stmt.getLong(_columnIndexOfCourseId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpDueDate;
          _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate);
          final boolean _tmpIsCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsCompleted));
          _tmpIsCompleted = _tmp != 0;
          final Priority _tmpPriority;
          final String _tmp_1;
          if (_stmt.isNull(_columnIndexOfPriority)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfPriority);
          }
          _tmpPriority = __converters.toPriority(_tmp_1);
          final TaskType _tmpType;
          final String _tmp_2;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = __converters.toTaskType(_tmp_2);
          _item = new Task(_tmpId,_tmpCourseId,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpIsCompleted,_tmpPriority,_tmpType);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<Task>> getUpcomingTasks() {
    final String _sql = "SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC LIMIT 5";
    return __db.getInvalidationTracker().createLiveData(new String[] {"tasks"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCourseId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "courseId");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfIsCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isCompleted");
        final int _columnIndexOfPriority = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "priority");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final List<Task> _result = new ArrayList<Task>();
        while (_stmt.step()) {
          final Task _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpCourseId;
          _tmpCourseId = _stmt.getLong(_columnIndexOfCourseId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpDueDate;
          _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate);
          final boolean _tmpIsCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsCompleted));
          _tmpIsCompleted = _tmp != 0;
          final Priority _tmpPriority;
          final String _tmp_1;
          if (_stmt.isNull(_columnIndexOfPriority)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfPriority);
          }
          _tmpPriority = __converters.toPriority(_tmp_1);
          final TaskType _tmpType;
          final String _tmp_2;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = __converters.toTaskType(_tmp_2);
          _item = new Task(_tmpId,_tmpCourseId,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpIsCompleted,_tmpPriority,_tmpType);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<Task>> getTasksDueSoon(final long start, final long end) {
    final String _sql = "SELECT * FROM tasks WHERE dueDate BETWEEN ? AND ? AND isCompleted = 0 ORDER BY dueDate ASC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"tasks"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, start);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, end);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCourseId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "courseId");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfIsCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isCompleted");
        final int _columnIndexOfPriority = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "priority");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final List<Task> _result = new ArrayList<Task>();
        while (_stmt.step()) {
          final Task _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpCourseId;
          _tmpCourseId = _stmt.getLong(_columnIndexOfCourseId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpDueDate;
          _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate);
          final boolean _tmpIsCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsCompleted));
          _tmpIsCompleted = _tmp != 0;
          final Priority _tmpPriority;
          final String _tmp_1;
          if (_stmt.isNull(_columnIndexOfPriority)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfPriority);
          }
          _tmpPriority = __converters.toPriority(_tmp_1);
          final TaskType _tmpType;
          final String _tmp_2;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = __converters.toTaskType(_tmp_2);
          _item = new Task(_tmpId,_tmpCourseId,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpIsCompleted,_tmpPriority,_tmpType);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<TaskWithCourseName>> getTasksWithCourseNameDueSoon(final long start,
      final long end) {
    final String _sql = "\n"
            + "        SELECT t.id as taskId, t.courseId, t.title, t.description, t.dueDate,\n"
            + "               t.isCompleted, t.priority, t.type, c.name as courseName, c.color as courseColor\n"
            + "        FROM tasks t\n"
            + "        INNER JOIN courses c ON t.courseId = c.id\n"
            + "        WHERE t.dueDate BETWEEN ? AND ? AND t.isCompleted = 0\n"
            + "        ORDER BY t.dueDate ASC\n"
            + "    ";
    return __db.getInvalidationTracker().createLiveData(new String[] {"tasks",
        "courses"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, start);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, end);
        final int _columnIndexOfTaskId = 0;
        final int _columnIndexOfCourseId = 1;
        final int _columnIndexOfTitle = 2;
        final int _columnIndexOfDescription = 3;
        final int _columnIndexOfDueDate = 4;
        final int _columnIndexOfIsCompleted = 5;
        final int _columnIndexOfPriority = 6;
        final int _columnIndexOfType = 7;
        final int _columnIndexOfCourseName = 8;
        final int _columnIndexOfCourseColor = 9;
        final List<TaskWithCourseName> _result = new ArrayList<TaskWithCourseName>();
        while (_stmt.step()) {
          final TaskWithCourseName _item;
          final long _tmpTaskId;
          _tmpTaskId = _stmt.getLong(_columnIndexOfTaskId);
          final long _tmpCourseId;
          _tmpCourseId = _stmt.getLong(_columnIndexOfCourseId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpDueDate;
          _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate);
          final boolean _tmpIsCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsCompleted));
          _tmpIsCompleted = _tmp != 0;
          final Priority _tmpPriority;
          final String _tmp_1;
          if (_stmt.isNull(_columnIndexOfPriority)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfPriority);
          }
          _tmpPriority = __converters.toPriority(_tmp_1);
          final TaskType _tmpType;
          final String _tmp_2;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = __converters.toTaskType(_tmp_2);
          final String _tmpCourseName;
          if (_stmt.isNull(_columnIndexOfCourseName)) {
            _tmpCourseName = null;
          } else {
            _tmpCourseName = _stmt.getText(_columnIndexOfCourseName);
          }
          final String _tmpCourseColor;
          if (_stmt.isNull(_columnIndexOfCourseColor)) {
            _tmpCourseColor = null;
          } else {
            _tmpCourseColor = _stmt.getText(_columnIndexOfCourseColor);
          }
          _item = new TaskWithCourseName(_tmpTaskId,_tmpCourseId,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpIsCompleted,_tmpPriority,_tmpType,_tmpCourseName,_tmpCourseColor);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<TaskWithCourseName>> getTasksWithCourseNameFuture(final long afterDate) {
    final String _sql = "\n"
            + "        SELECT t.id as taskId, t.courseId, t.title, t.description, t.dueDate,\n"
            + "               t.isCompleted, t.priority, t.type, c.name as courseName, c.color as courseColor\n"
            + "        FROM tasks t\n"
            + "        INNER JOIN courses c ON t.courseId = c.id\n"
            + "        WHERE t.dueDate > ? AND t.isCompleted = 0\n"
            + "        ORDER BY t.dueDate ASC\n"
            + "    ";
    return __db.getInvalidationTracker().createLiveData(new String[] {"tasks",
        "courses"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, afterDate);
        final int _columnIndexOfTaskId = 0;
        final int _columnIndexOfCourseId = 1;
        final int _columnIndexOfTitle = 2;
        final int _columnIndexOfDescription = 3;
        final int _columnIndexOfDueDate = 4;
        final int _columnIndexOfIsCompleted = 5;
        final int _columnIndexOfPriority = 6;
        final int _columnIndexOfType = 7;
        final int _columnIndexOfCourseName = 8;
        final int _columnIndexOfCourseColor = 9;
        final List<TaskWithCourseName> _result = new ArrayList<TaskWithCourseName>();
        while (_stmt.step()) {
          final TaskWithCourseName _item;
          final long _tmpTaskId;
          _tmpTaskId = _stmt.getLong(_columnIndexOfTaskId);
          final long _tmpCourseId;
          _tmpCourseId = _stmt.getLong(_columnIndexOfCourseId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpDueDate;
          _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate);
          final boolean _tmpIsCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsCompleted));
          _tmpIsCompleted = _tmp != 0;
          final Priority _tmpPriority;
          final String _tmp_1;
          if (_stmt.isNull(_columnIndexOfPriority)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfPriority);
          }
          _tmpPriority = __converters.toPriority(_tmp_1);
          final TaskType _tmpType;
          final String _tmp_2;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = __converters.toTaskType(_tmp_2);
          final String _tmpCourseName;
          if (_stmt.isNull(_columnIndexOfCourseName)) {
            _tmpCourseName = null;
          } else {
            _tmpCourseName = _stmt.getText(_columnIndexOfCourseName);
          }
          final String _tmpCourseColor;
          if (_stmt.isNull(_columnIndexOfCourseColor)) {
            _tmpCourseColor = null;
          } else {
            _tmpCourseColor = _stmt.getText(_columnIndexOfCourseColor);
          }
          _item = new TaskWithCourseName(_tmpTaskId,_tmpCourseId,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpIsCompleted,_tmpPriority,_tmpType,_tmpCourseName,_tmpCourseColor);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<TaskWithCourseInfo>> getAllTasksWithCourseInfo() {
    final String _sql = "\n"
            + "        SELECT t.id as taskId, t.courseId, t.title, t.description, t.dueDate,\n"
            + "               t.isCompleted, t.priority, t.type, c.name as courseName, c.color as courseColor\n"
            + "        FROM tasks t\n"
            + "        INNER JOIN courses c ON t.courseId = c.id\n"
            + "        WHERE t.dueDate > 0\n"
            + "        ORDER BY c.name ASC, t.dueDate ASC\n"
            + "    ";
    return __db.getInvalidationTracker().createLiveData(new String[] {"tasks",
        "courses"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfTaskId = 0;
        final int _columnIndexOfCourseId = 1;
        final int _columnIndexOfTitle = 2;
        final int _columnIndexOfDescription = 3;
        final int _columnIndexOfDueDate = 4;
        final int _columnIndexOfIsCompleted = 5;
        final int _columnIndexOfPriority = 6;
        final int _columnIndexOfType = 7;
        final int _columnIndexOfCourseName = 8;
        final int _columnIndexOfCourseColor = 9;
        final List<TaskWithCourseInfo> _result = new ArrayList<TaskWithCourseInfo>();
        while (_stmt.step()) {
          final TaskWithCourseInfo _item;
          final long _tmpTaskId;
          _tmpTaskId = _stmt.getLong(_columnIndexOfTaskId);
          final long _tmpCourseId;
          _tmpCourseId = _stmt.getLong(_columnIndexOfCourseId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpDueDate;
          _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate);
          final boolean _tmpIsCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsCompleted));
          _tmpIsCompleted = _tmp != 0;
          final Priority _tmpPriority;
          final String _tmp_1;
          if (_stmt.isNull(_columnIndexOfPriority)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfPriority);
          }
          _tmpPriority = __converters.toPriority(_tmp_1);
          final TaskType _tmpType;
          final String _tmp_2;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = __converters.toTaskType(_tmp_2);
          final String _tmpCourseName;
          if (_stmt.isNull(_columnIndexOfCourseName)) {
            _tmpCourseName = null;
          } else {
            _tmpCourseName = _stmt.getText(_columnIndexOfCourseName);
          }
          final String _tmpCourseColor;
          if (_stmt.isNull(_columnIndexOfCourseColor)) {
            _tmpCourseColor = null;
          } else {
            _tmpCourseColor = _stmt.getText(_columnIndexOfCourseColor);
          }
          _item = new TaskWithCourseInfo(_tmpTaskId,_tmpCourseId,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpIsCompleted,_tmpPriority,_tmpType,_tmpCourseName,_tmpCourseColor);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<TaskWithCourseInfo>> getAllTasksWithCourseInfoForSearch() {
    final String _sql = "\n"
            + "        SELECT t.id as taskId, t.courseId, t.title, t.description, t.dueDate,\n"
            + "               t.isCompleted, t.priority, t.type, c.name as courseName, c.color as courseColor\n"
            + "        FROM tasks t\n"
            + "        INNER JOIN courses c ON t.courseId = c.id\n"
            + "        ORDER BY c.name ASC, t.dueDate ASC\n"
            + "    ";
    return __db.getInvalidationTracker().createLiveData(new String[] {"tasks",
        "courses"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfTaskId = 0;
        final int _columnIndexOfCourseId = 1;
        final int _columnIndexOfTitle = 2;
        final int _columnIndexOfDescription = 3;
        final int _columnIndexOfDueDate = 4;
        final int _columnIndexOfIsCompleted = 5;
        final int _columnIndexOfPriority = 6;
        final int _columnIndexOfType = 7;
        final int _columnIndexOfCourseName = 8;
        final int _columnIndexOfCourseColor = 9;
        final List<TaskWithCourseInfo> _result = new ArrayList<TaskWithCourseInfo>();
        while (_stmt.step()) {
          final TaskWithCourseInfo _item;
          final long _tmpTaskId;
          _tmpTaskId = _stmt.getLong(_columnIndexOfTaskId);
          final long _tmpCourseId;
          _tmpCourseId = _stmt.getLong(_columnIndexOfCourseId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpDueDate;
          _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate);
          final boolean _tmpIsCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsCompleted));
          _tmpIsCompleted = _tmp != 0;
          final Priority _tmpPriority;
          final String _tmp_1;
          if (_stmt.isNull(_columnIndexOfPriority)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfPriority);
          }
          _tmpPriority = __converters.toPriority(_tmp_1);
          final TaskType _tmpType;
          final String _tmp_2;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = __converters.toTaskType(_tmp_2);
          final String _tmpCourseName;
          if (_stmt.isNull(_columnIndexOfCourseName)) {
            _tmpCourseName = null;
          } else {
            _tmpCourseName = _stmt.getText(_columnIndexOfCourseName);
          }
          final String _tmpCourseColor;
          if (_stmt.isNull(_columnIndexOfCourseColor)) {
            _tmpCourseColor = null;
          } else {
            _tmpCourseColor = _stmt.getText(_columnIndexOfCourseColor);
          }
          _item = new TaskWithCourseInfo(_tmpTaskId,_tmpCourseId,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpIsCompleted,_tmpPriority,_tmpType,_tmpCourseName,_tmpCourseColor);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<Integer> getPendingTaskCount(final long courseId) {
    final String _sql = "SELECT COUNT(*) FROM tasks WHERE courseId = ? AND isCompleted = 0";
    return __db.getInvalidationTracker().createLiveData(new String[] {"tasks"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, courseId);
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<Integer> getTotalTaskCount(final long courseId) {
    final String _sql = "SELECT COUNT(*) FROM tasks WHERE courseId = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"tasks"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, courseId);
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<Integer> getTotalPendingCount() {
    final String _sql = "SELECT COUNT(*) FROM tasks WHERE isCompleted = 0";
    return __db.getInvalidationTracker().createLiveData(new String[] {"tasks"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getTaskById(final long taskId, final Continuation<? super Task> $completion) {
    final String _sql = "SELECT * FROM tasks WHERE id = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, taskId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCourseId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "courseId");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfIsCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isCompleted");
        final int _columnIndexOfPriority = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "priority");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final Task _result;
        if (_stmt.step()) {
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpCourseId;
          _tmpCourseId = _stmt.getLong(_columnIndexOfCourseId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpDueDate;
          _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate);
          final boolean _tmpIsCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsCompleted));
          _tmpIsCompleted = _tmp != 0;
          final Priority _tmpPriority;
          final String _tmp_1;
          if (_stmt.isNull(_columnIndexOfPriority)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfPriority);
          }
          _tmpPriority = __converters.toPriority(_tmp_1);
          final TaskType _tmpType;
          final String _tmp_2;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = __converters.toTaskType(_tmp_2);
          _result = new Task(_tmpId,_tmpCourseId,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpIsCompleted,_tmpPriority,_tmpType);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getTasksForCourseOnce(final long courseId,
      final Continuation<? super List<Task>> $completion) {
    final String _sql = "SELECT * FROM tasks WHERE courseId = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, courseId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCourseId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "courseId");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfIsCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isCompleted");
        final int _columnIndexOfPriority = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "priority");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final List<Task> _result = new ArrayList<Task>();
        while (_stmt.step()) {
          final Task _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpCourseId;
          _tmpCourseId = _stmt.getLong(_columnIndexOfCourseId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpDueDate;
          _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate);
          final boolean _tmpIsCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsCompleted));
          _tmpIsCompleted = _tmp != 0;
          final Priority _tmpPriority;
          final String _tmp_1;
          if (_stmt.isNull(_columnIndexOfPriority)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfPriority);
          }
          _tmpPriority = __converters.toPriority(_tmp_1);
          final TaskType _tmpType;
          final String _tmp_2;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = __converters.toTaskType(_tmp_2);
          _item = new Task(_tmpId,_tmpCourseId,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpIsCompleted,_tmpPriority,_tmpType);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getAllTasksOnce(final Continuation<? super List<Task>> $completion) {
    final String _sql = "SELECT * FROM tasks ORDER BY courseId ASC, dueDate ASC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCourseId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "courseId");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfIsCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isCompleted");
        final int _columnIndexOfPriority = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "priority");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final List<Task> _result = new ArrayList<Task>();
        while (_stmt.step()) {
          final Task _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpCourseId;
          _tmpCourseId = _stmt.getLong(_columnIndexOfCourseId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpDueDate;
          _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate);
          final boolean _tmpIsCompleted;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsCompleted));
          _tmpIsCompleted = _tmp != 0;
          final Priority _tmpPriority;
          final String _tmp_1;
          if (_stmt.isNull(_columnIndexOfPriority)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfPriority);
          }
          _tmpPriority = __converters.toPriority(_tmp_1);
          final TaskType _tmpType;
          final String _tmp_2;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = __converters.toTaskType(_tmp_2);
          _item = new Task(_tmpId,_tmpCourseId,_tmpTitle,_tmpDescription,_tmpDueDate,_tmpIsCompleted,_tmpPriority,_tmpType);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object setTaskCompleted(final long taskId, final boolean completed,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE tasks SET isCompleted = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        final int _tmp = completed ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, taskId);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteAllTasks(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM tasks";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
