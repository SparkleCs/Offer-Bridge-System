package com.offerbridge.backend.entity;

public class MajorDirection {
  private Long id;
  private String subjectCategoryCode;
  private String directionCode;
  private String directionName;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getSubjectCategoryCode() { return subjectCategoryCode; }
  public void setSubjectCategoryCode(String subjectCategoryCode) { this.subjectCategoryCode = subjectCategoryCode; }
  public String getDirectionCode() { return directionCode; }
  public void setDirectionCode(String directionCode) { this.directionCode = directionCode; }
  public String getDirectionName() { return directionName; }
  public void setDirectionName(String directionName) { this.directionName = directionName; }
}
