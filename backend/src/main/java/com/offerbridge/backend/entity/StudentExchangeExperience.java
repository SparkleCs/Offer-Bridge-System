package com.offerbridge.backend.entity;

import java.math.BigDecimal;

public class StudentExchangeExperience {
  private Long id;
  private Long userId;
  private String countryName;
  private String universityName;
  private BigDecimal gpaValue;
  private String majorCourses;
  private String startDate;
  private String endDate;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getCountryName() { return countryName; }
  public void setCountryName(String countryName) { this.countryName = countryName; }
  public String getUniversityName() { return universityName; }
  public void setUniversityName(String universityName) { this.universityName = universityName; }
  public BigDecimal getGpaValue() { return gpaValue; }
  public void setGpaValue(BigDecimal gpaValue) { this.gpaValue = gpaValue; }
  public String getMajorCourses() { return majorCourses; }
  public void setMajorCourses(String majorCourses) { this.majorCourses = majorCourses; }
  public String getStartDate() { return startDate; }
  public void setStartDate(String startDate) { this.startDate = startDate; }
  public String getEndDate() { return endDate; }
  public void setEndDate(String endDate) { this.endDate = endDate; }
}
