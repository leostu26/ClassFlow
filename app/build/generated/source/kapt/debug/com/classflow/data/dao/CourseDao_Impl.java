package com.classflow.data.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.classflow.data.model.Course;
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
public final class CourseDao_Impl implements CourseDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Course> __insertAdapterOfCourse;

  private final EntityDeleteOrUpdateAdapter<Course> __deleteAdapterOfCourse;

  private final EntityDeleteOrUpdateAdapter<Course> __updateAdapterOfCourse;

  public CourseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfCourse = new EntityInsertAdapter<Course>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `courses` (`id`,`name`,`code`,`instructor`,`color`,`schedule`,`room`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Course entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getCode() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getCode());
        }
        if (entity.getInstructor() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getInstructor());
        }
        if (entity.getColor() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getColor());
        }
        if (entity.getSchedule() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getSchedule());
        }
        if (entity.getRoom() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getRoom());
        }
      }
    };
    this.__deleteAdapterOfCourse = new EntityDeleteOrUpdateAdapter<Course>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `courses` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Course entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfCourse = new EntityDeleteOrUpdateAdapter<Course>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `courses` SET `id` = ?,`name` = ?,`code` = ?,`instructor` = ?,`color` = ?,`schedule` = ?,`room` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Course entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getCode() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getCode());
        }
        if (entity.getInstructor() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getInstructor());
        }
        if (entity.getColor() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getColor());
        }
        if (entity.getSchedule() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getSchedule());
        }
        if (entity.getRoom() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getRoom());
        }
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insertCourse(final Course course, final Continuation<? super Long> $completion) {
    if (course == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfCourse.insertAndReturnId(_connection, course);
    }, $completion);
  }

  @Override
  public Object deleteCourse(final Course course, final Continuation<? super Unit> $completion) {
    if (course == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfCourse.handle(_connection, course);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object updateCourse(final Course course, final Continuation<? super Unit> $completion) {
    if (course == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfCourse.handle(_connection, course);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public LiveData<List<Course>> getAllCourses() {
    final String _sql = "SELECT * FROM courses ORDER BY name ASC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"courses"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "code");
        final int _columnIndexOfInstructor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "instructor");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfSchedule = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "schedule");
        final int _columnIndexOfRoom = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "room");
        final List<Course> _result = new ArrayList<Course>();
        while (_stmt.step()) {
          final Course _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          final String _tmpCode;
          if (_stmt.isNull(_columnIndexOfCode)) {
            _tmpCode = null;
          } else {
            _tmpCode = _stmt.getText(_columnIndexOfCode);
          }
          final String _tmpInstructor;
          if (_stmt.isNull(_columnIndexOfInstructor)) {
            _tmpInstructor = null;
          } else {
            _tmpInstructor = _stmt.getText(_columnIndexOfInstructor);
          }
          final String _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor);
          }
          final String _tmpSchedule;
          if (_stmt.isNull(_columnIndexOfSchedule)) {
            _tmpSchedule = null;
          } else {
            _tmpSchedule = _stmt.getText(_columnIndexOfSchedule);
          }
          final String _tmpRoom;
          if (_stmt.isNull(_columnIndexOfRoom)) {
            _tmpRoom = null;
          } else {
            _tmpRoom = _stmt.getText(_columnIndexOfRoom);
          }
          _item = new Course(_tmpId,_tmpName,_tmpCode,_tmpInstructor,_tmpColor,_tmpSchedule,_tmpRoom);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getCourseById(final long courseId, final Continuation<? super Course> $completion) {
    final String _sql = "SELECT * FROM courses WHERE id = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, courseId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfCode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "code");
        final int _columnIndexOfInstructor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "instructor");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfSchedule = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "schedule");
        final int _columnIndexOfRoom = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "room");
        final Course _result;
        if (_stmt.step()) {
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          final String _tmpCode;
          if (_stmt.isNull(_columnIndexOfCode)) {
            _tmpCode = null;
          } else {
            _tmpCode = _stmt.getText(_columnIndexOfCode);
          }
          final String _tmpInstructor;
          if (_stmt.isNull(_columnIndexOfInstructor)) {
            _tmpInstructor = null;
          } else {
            _tmpInstructor = _stmt.getText(_columnIndexOfInstructor);
          }
          final String _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = _stmt.getText(_columnIndexOfColor);
          }
          final String _tmpSchedule;
          if (_stmt.isNull(_columnIndexOfSchedule)) {
            _tmpSchedule = null;
          } else {
            _tmpSchedule = _stmt.getText(_columnIndexOfSchedule);
          }
          final String _tmpRoom;
          if (_stmt.isNull(_columnIndexOfRoom)) {
            _tmpRoom = null;
          } else {
            _tmpRoom = _stmt.getText(_columnIndexOfRoom);
          }
          _result = new Course(_tmpId,_tmpName,_tmpCode,_tmpInstructor,_tmpColor,_tmpSchedule,_tmpRoom);
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
  public LiveData<Integer> getCourseCount() {
    final String _sql = "SELECT COUNT(*) FROM courses";
    return __db.getInvalidationTracker().createLiveData(new String[] {"courses"}, false, (_connection) -> {
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
