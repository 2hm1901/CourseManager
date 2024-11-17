package com.example.coursework_1786.Models;

public class Lesson {
  private int lessonId;
  private int courseId;
  private int userId;
  private String title;
  private String content;
  private String imageUrl;

  // Constructor


  public Lesson(int lessonId, int courseId, int userId, String title, String content, String imageUrl) {
    this.lessonId = lessonId;
    this.courseId = courseId;
    this.userId = userId;
    this.title = title;
    this.content = content;
    this.imageUrl = imageUrl;
  }
  public Lesson(){};

  // Getters và Setters
  public int getLessonId() {
    return lessonId;
  }

  public void setLessonId(int lessonId) {
    this.lessonId = lessonId;
  }

  public int getCourseId() {
    return courseId;
  }

  public void setCourseId(int courseId) {
    this.courseId = courseId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}

