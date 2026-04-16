package com.offerbridge.backend.entity;

public class SubjectCategory {
  private Long id;
  private String categoryCode;
  private String categoryName;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getCategoryCode() { return categoryCode; }
  public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
  public String getCategoryName() { return categoryName; }
  public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
