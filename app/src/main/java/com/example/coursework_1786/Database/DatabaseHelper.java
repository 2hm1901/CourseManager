package com.example.coursework_1786.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.coursework_1786.Models.Course;
import com.example.coursework_1786.Models.Enrollment;
import com.example.coursework_1786.Models.Lesson;
import com.example.coursework_1786.Models.Review;
import com.example.coursework_1786.Models.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "YogaApp.db";
  private static final int DATABASE_VERSION = 3;

  // Table Names
  private static final String TABLE_USERS = "Users";
  private static final String TABLE_COURSES = "Courses";
  private static final String TABLE_LESSONS = "Lessons";
  private static final String TABLE_ENROLLMENTS = "Enrollments";
  private static final String TABLE_REVIEWS = "Reviews";

  // Common column names
  private static final String KEY_ID = "id";

  // Users Table - column names
  private static final String KEY_USER_ID = "userId";
  private static final String KEY_USERNAME = "username";
  private static final String KEY_EMAIL = "email";
  private static final String KEY_PASSWORD = "password";
  private static final String KEY_FULL_NAME = "fullName";
  private static final String KEY_PHONE = "phoneNumber";
  private static final String KEY_ROLE = "role";
  private static final String KEY_REGISTRATION_DATE = "registrationDate";

  // Courses Table - column names
  private static final String KEY_COURSE_ID = "courseId";
  private static final String KEY_TITLE = "title";
  private static final String KEY_DESCRIPTION = "description";
  private static final String KEY_PRICE = "price";
  private static final String KEY_DURATION = "duration";
  private static final String KEY_TYPE = "type";
  private static final String KEY_THUMBNAIL = "thumbnailUrl";
  private static final String KEY_CREATION_DATE = "creationDate";

  // Lessons Table - column names
  private static final String KEY_LESSON_ID = "lessonId";
  private static final String KEY_CONTENT = "content";
  private static final String KEY_IMAGE_URL = "videoUrl";


  // Enrollments Table - column names
  private static final String KEY_ENROLLMENT_ID = "enrollmentId";
  private static final String KEY_ENROLLMENT_DATE = "enrollmentDate";


  // Reviews Table - column names
  private static final String KEY_REVIEW_ID = "reviewId";
  private static final String KEY_RATING = "rating";
  private static final String KEY_COMMENT = "comment";
  private static final String KEY_REVIEW_DATE = "reviewDate";

  public DatabaseHelper(@Nullable Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
  // Table Create Statements
  // Users table create statement
  private static final String CREATE_TABLE_USERS = "CREATE TABLE "
          + TABLE_USERS + "(" + KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                              + KEY_USERNAME + " TEXT,"
                              + KEY_EMAIL + " TEXT,"
                              + KEY_PASSWORD + " TEXT,"
                              + KEY_FULL_NAME + " TEXT,"
                              + KEY_PHONE + " TEXT,"
                              + KEY_ROLE + " TEXT,"
                              + KEY_REGISTRATION_DATE + " TEXT" + ")";

  // Courses table create statement
  private static final String CREATE_TABLE_COURSES = "CREATE TABLE "
          + TABLE_COURSES + "(" + KEY_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                + KEY_TITLE + " TEXT,"
                                + KEY_DESCRIPTION + " TEXT,"
                                + KEY_PRICE + " REAL,"
                                + KEY_DURATION + " INTEGER,"
                                + KEY_TYPE + " TEXT,"
                                + KEY_THUMBNAIL + " TEXT,"
                                + KEY_CREATION_DATE + " TEXT" + ")";

  // Lessons table create statement
  private static final String CREATE_TABLE_LESSONS = "CREATE TABLE "
          + TABLE_LESSONS + "(" + KEY_LESSON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
          + KEY_COURSE_ID + " INTEGER,"
          + KEY_USER_ID + " INTEGER, "
          + KEY_TITLE + " TEXT,"
          + KEY_CONTENT + " TEXT,"
          + KEY_IMAGE_URL + " TEXT,"
          + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + "),"
          + "FOREIGN KEY(" + KEY_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + KEY_COURSE_ID + ")" + ")";

  // Enrollments table create statement
  private static final String CREATE_TABLE_ENROLLMENTS = "CREATE TABLE "
          + TABLE_ENROLLMENTS + "(" + KEY_ENROLLMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
          + KEY_USER_ID + " INTEGER,"
          + KEY_COURSE_ID + " INTEGER,"
          + KEY_ENROLLMENT_DATE + " TEXT,"
          + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + "),"
          + "FOREIGN KEY(" + KEY_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + KEY_COURSE_ID + ")" + ")";


  // Reviews table create statement
  private static final String CREATE_TABLE_REVIEWS = "CREATE TABLE "
          + TABLE_REVIEWS + "(" + KEY_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
          + KEY_USER_ID + " INTEGER,"
          + KEY_COURSE_ID + " INTEGER,"
          + KEY_RATING + " INTEGER,"
          + KEY_COMMENT + " TEXT,"
          + KEY_REVIEW_DATE + " TEXT,"
          + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + "),"
          + "FOREIGN KEY(" + KEY_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + KEY_COURSE_ID + ")" + ")";



  @Override
  public void onCreate(SQLiteDatabase db) {
    // Creating tables
    db.execSQL(CREATE_TABLE_USERS);
    db.execSQL(CREATE_TABLE_COURSES);
    db.execSQL(CREATE_TABLE_LESSONS);
    db.execSQL(CREATE_TABLE_ENROLLMENTS);
    db.execSQL(CREATE_TABLE_REVIEWS);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Drop older tables if existed
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSONS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENROLLMENTS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);

    // Create new tables
    onCreate(db);
  }
  public SQLiteDatabase openWritableDatabase() {
    return this.getWritableDatabase();
  }
  // -------------------------------------
  // Users CRUD Methods
  // -------------------------------------

  public long insertUser(String fullName, String email, String password, String phone, String role) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(KEY_FULL_NAME, fullName);
    values.put(KEY_EMAIL, email);
    values.put(KEY_PASSWORD, password);
    values.put(KEY_PHONE, phone);
    values.put(KEY_ROLE, role);

    long id = db.insert(TABLE_USERS, null, values);
    db.close();
    return id;
  }

  public User getUser(int userId) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.query(TABLE_USERS, null, KEY_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
    if (cursor != null)
      cursor.moveToFirst();

    @SuppressLint("Range") User user = new User(
            cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)),
            cursor.getString(cursor.getColumnIndex(KEY_EMAIL)),
            cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)),
            cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME)),
            cursor.getString(cursor.getColumnIndex(KEY_PHONE)),
            cursor.getString(cursor.getColumnIndex(KEY_ROLE)),
            cursor.getString(cursor.getColumnIndex(KEY_REGISTRATION_DATE))
    );

    cursor.close();
    return user;
  }

  public List<User> getAllUsers() {
    List<User> userList = new ArrayList<>();
    String selectQuery = "SELECT * FROM " + TABLE_USERS;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);

    if (cursor.moveToFirst()) {
      do {
        @SuppressLint("Range") User user = new User(
                cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_EMAIL)),
                cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)),
                cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME)),
                cursor.getString(cursor.getColumnIndex(KEY_PHONE)),
                cursor.getString(cursor.getColumnIndex(KEY_ROLE)),
                cursor.getString(cursor.getColumnIndex(KEY_REGISTRATION_DATE))
        );
        userList.add(user);
      } while (cursor.moveToNext());
    }

    cursor.close();
    return userList;
  }

  public int updateUser(User user) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(KEY_EMAIL, user.getEmail());
    values.put(KEY_PASSWORD, user.getPassword());
    values.put(KEY_FULL_NAME, user.getFullName());
    values.put(KEY_PHONE, user.getPhoneNumber());
    values.put(KEY_ROLE, user.getRole());
    values.put(KEY_REGISTRATION_DATE, user.getRegistrationDate());

    return db.update(TABLE_USERS, values, KEY_USER_ID + "=?", new String[]{String.valueOf(user.getUserId())});
  }

  public void deleteUser(int userId) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(TABLE_USERS, KEY_USER_ID + "=?", new String[]{String.valueOf(userId)});
    db.close();
  }

  // -------------------------------------
  // Courses CRUD Methods
  // -------------------------------------

  public long insertCourse(String title, String description, double price, int duration, String type, String thumbnail, String creationDate) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(KEY_TITLE, title);
    values.put(KEY_DESCRIPTION, description);
    values.put(KEY_PRICE, price);
    values.put(KEY_DURATION, duration);
    values.put(KEY_TYPE, type);
    values.put(KEY_THUMBNAIL, thumbnail);
    values.put(KEY_CREATION_DATE, creationDate);

    long id = db.insert(TABLE_COURSES, null, values);
    db.close();
    return id;
  }

  public Course getCourse(int courseId) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.query(TABLE_COURSES, null, KEY_COURSE_ID + "=?", new String[]{String.valueOf(courseId)}, null, null, null);
    if (cursor != null)
      cursor.moveToFirst();

    @SuppressLint("Range") Course course = new Course(
            cursor.getInt(cursor.getColumnIndex(KEY_COURSE_ID)),
            cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
            cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
            cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)),
            cursor.getInt(cursor.getColumnIndex(KEY_DURATION)),
            cursor.getString(cursor.getColumnIndex(KEY_TYPE)),
            cursor.getString(cursor.getColumnIndex(KEY_THUMBNAIL)),
            cursor.getString(cursor.getColumnIndex(KEY_CREATION_DATE))
    );

    cursor.close();
    return course;
  }

  public List<Course> getAllCourses() {
    List<Course> courseList = new ArrayList<>();
    String selectQuery = "SELECT * FROM " + TABLE_COURSES;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);

    if (cursor.moveToFirst()) {
      do {
        @SuppressLint("Range") Course course = new Course(
                cursor.getInt(cursor.getColumnIndex(KEY_COURSE_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)),
                cursor.getInt(cursor.getColumnIndex(KEY_DURATION)),
                cursor.getString(cursor.getColumnIndex(KEY_TYPE)),
                cursor.getString(cursor.getColumnIndex(KEY_THUMBNAIL)),
                cursor.getString(cursor.getColumnIndex(KEY_CREATION_DATE))
        );
        courseList.add(course);
      } while (cursor.moveToNext());
    }

    cursor.close();
    return courseList;
  }

  // Cập nhật một khóa học
  public int updateCourse(Course course) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(KEY_TITLE, course.getTitle());
    values.put(KEY_DESCRIPTION, course.getDescription());
    values.put(KEY_PRICE, course.getPrice());
    values.put(KEY_DURATION, course.getDuration());
    values.put(KEY_TYPE, course.getType());
    values.put(KEY_THUMBNAIL, course.getThumbnailUrl());
    values.put(KEY_CREATION_DATE, course.getCreationDate());

    return db.update(TABLE_COURSES, values, KEY_COURSE_ID + "=?", new String[]{String.valueOf(course.getCourseId())});
  }

  // Xóa một khóa học
  public void deleteCourse(int courseId) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(TABLE_COURSES, KEY_COURSE_ID + "=?", new String[]{String.valueOf(courseId)});
  }

  // -------------------------------------
// Lessons CRUD Methods
// -------------------------------------

  public long insertLesson(String courseId, String userId, String title, String content, String videoUrl) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(KEY_COURSE_ID, courseId);
    values.put(KEY_USER_ID, userId);
    values.put(KEY_TITLE, title);
    values.put(KEY_CONTENT, content);
    values.put(KEY_IMAGE_URL, videoUrl);

    long id = db.insert(TABLE_LESSONS, null, values);
    db.close();
    return id;
  }

  public Lesson getLesson(int lessonId) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.query(TABLE_LESSONS, null, KEY_LESSON_ID + "=?", new String[]{String.valueOf(lessonId)}, null, null, null);
    if (cursor != null)
      cursor.moveToFirst();

    @SuppressLint("Range") Lesson lesson = new Lesson(
            cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID)),
            cursor.getInt(cursor.getColumnIndex(KEY_COURSE_ID)),
            cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)),
            cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
            cursor.getString(cursor.getColumnIndex(KEY_CONTENT)),
            cursor.getString(cursor.getColumnIndex(KEY_IMAGE_URL))
    );

    cursor.close();
    return lesson;
  }

  public List<Lesson> getAllLessons() {
    List<Lesson> lessonList = new ArrayList<>();
    String selectQuery = "SELECT * FROM " + TABLE_LESSONS;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);

    if (cursor.moveToFirst()) {
      do {
        @SuppressLint("Range") Lesson lesson = new Lesson(
                cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID)),
                cursor.getInt(cursor.getColumnIndex(KEY_COURSE_ID)),
                cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                cursor.getString(cursor.getColumnIndex(KEY_CONTENT)),
                cursor.getString(cursor.getColumnIndex(KEY_IMAGE_URL))
        );
        lessonList.add(lesson);
      } while (cursor.moveToNext());
    }

    cursor.close();
    return lessonList;
  }

  public int updateLesson(Lesson lesson) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(KEY_COURSE_ID, lesson.getCourseId());
    values.put(KEY_USER_ID, lesson.getUserId());
    values.put(KEY_TITLE, lesson.getTitle());
    values.put(KEY_CONTENT, lesson.getContent());
    values.put(KEY_IMAGE_URL, lesson.getImageUrl());

    return db.update(TABLE_LESSONS, values, KEY_LESSON_ID + "=?", new String[]{String.valueOf(lesson.getLessonId())});
  }

  public void deleteLesson(int lessonId) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(TABLE_LESSONS, KEY_LESSON_ID + "=?", new String[]{String.valueOf(lessonId)});
    db.close();
  }

// -------------------------------------
// Enrollments CRUD Methods
// -------------------------------------

  public long insertEnrollment(int userId, int courseId, String enrollmentDate) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(KEY_USER_ID, userId);
    values.put(KEY_COURSE_ID, courseId);
    values.put(KEY_ENROLLMENT_DATE, enrollmentDate);

    long id = db.insert(TABLE_ENROLLMENTS, null, values);
    db.close();
    return id;
  }

  public List<Enrollment> getAllEnrollments() {
    List<Enrollment> enrollmentList = new ArrayList<>();
    String selectQuery = "SELECT * FROM " + TABLE_ENROLLMENTS;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);

    if (cursor.moveToFirst()) {
      do {
        @SuppressLint("Range") Enrollment enrollment = new Enrollment(
                cursor.getInt(cursor.getColumnIndex(KEY_ENROLLMENT_ID)),
                cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)),
                cursor.getInt(cursor.getColumnIndex(KEY_COURSE_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_ENROLLMENT_DATE))
        );
        enrollmentList.add(enrollment);
      } while (cursor.moveToNext());
    }

    cursor.close();
    return enrollmentList;
  }

  public void deleteEnrollment(int enrollmentId) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(TABLE_ENROLLMENTS, KEY_ENROLLMENT_ID + "=?", new String[]{String.valueOf(enrollmentId)});
    db.close();
  }

  // -------------------------------------
  // Reviews CRUD Methods
  // -------------------------------------

  public long insertReview(int userId, int courseId, int rating, String comment, String reviewDate) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(KEY_USER_ID, userId);
    values.put(KEY_COURSE_ID, courseId);
    values.put(KEY_RATING, rating);
    values.put(KEY_COMMENT, comment);
    values.put(KEY_REVIEW_DATE, reviewDate);

    long id = db.insert(TABLE_REVIEWS, null, values);
    db.close();
    return id;
  }

  public List<Review> getAllReviews() {
    List<Review> reviewList = new ArrayList<>();
    String selectQuery = "SELECT * FROM " + TABLE_REVIEWS;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);

    if (cursor.moveToFirst()) {
      do {
        @SuppressLint("Range") Review review = new Review(
                cursor.getInt(cursor.getColumnIndex(KEY_REVIEW_ID)),
                cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)),
                cursor.getInt(cursor.getColumnIndex(KEY_COURSE_ID)),
                cursor.getInt(cursor.getColumnIndex(KEY_RATING)),
                cursor.getString(cursor.getColumnIndex(KEY_COMMENT)),
                cursor.getString(cursor.getColumnIndex(KEY_REVIEW_DATE))
        );
        reviewList.add(review);
      } while (cursor.moveToNext());
    }

    cursor.close();
    return reviewList;
  }

  public void deleteReview(int reviewId) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(TABLE_REVIEWS, KEY_REVIEW_ID + "=?", new String[]{String.valueOf(reviewId)});
    db.close();
  }

}




