package com.example.coursework_1786.Models;

public class Review {
  private int reviewId;
  private int userId;
  private int courseId;
  private int rating; // e.g. 1-5 stars
  private String comment;
  private String reviewDate;

  // Constructor
  public Review(int reviewId, int userId, int courseId, int rating, String comment, String reviewDate) {
    this.reviewId = reviewId;
    this.userId = userId;
    this.courseId = courseId;
    this.rating = rating;
    this.comment = comment;
    this.reviewDate = reviewDate;
  }

  // Getters và Setters
  public int getReviewId() {
    return reviewId;
  }

  public void setReviewId(int reviewId) {
    this.reviewId = reviewId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getCourseId() {
    return courseId;
  }

  public void setCourseId(int courseId) {
    this.courseId = courseId;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getReviewDate() {
    return reviewDate;
  }

  public void setReviewDate(String reviewDate) {
    this.reviewDate = reviewDate;
  }
}

