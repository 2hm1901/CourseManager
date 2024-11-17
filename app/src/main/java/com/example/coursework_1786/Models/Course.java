package com.example.coursework_1786.Models;

public class Course {
  private int courseId;
  private String title;
  private String description;
  private double price;
  private int duration;
  private String type;
  private String thumbnailUrl;
  private String creationDate;

  // Constructor
  public Course(int courseId, String title, String description, double price, int duration, String type, String thumbnailUrl, String creationDate) {
    this.courseId = courseId;
    this.title = title;
    this.description = description;
    this.price = price;
    this.duration = duration;
    this.type = type;
    this.thumbnailUrl = thumbnailUrl;
    this.creationDate = creationDate;
  }
  public Course() {
  }

  // Getters và Setters
  public int getCourseId() {
    return courseId;
  }

  public void setCourseId(int courseId) {
    this.courseId = courseId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }
}

