package com.example.coursework_1786.Models;

public class User {
  private int userId;
  private String email;
  private String password;
  private String fullName;
  private String phoneNumber;
  private String role;
  private String registrationDate;

  public User(String fullName, String email, String password) {
    this.fullName = fullName;
    this.email = email;
    this.password = password;

  }

  // Constructor
  public User(int userId, String email, String password, String fullName, String phoneNumber, String role, String registrationDate) {
    this.userId = userId;
    this.email = email;
    this.password = password;
    this.fullName = fullName;
    this.phoneNumber = phoneNumber;
    this.role = role;
    this.registrationDate = registrationDate;
  }


  // Getters và Setters
  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getRegistrationDate() {
    return registrationDate;
  }

  public void setRegistrationDate(String registrationDate) {
    this.registrationDate = registrationDate;
  }
}

