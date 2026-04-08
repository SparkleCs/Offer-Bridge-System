package com.offerbridge.backend.entity;

public class StudentWorkExperience {
  private Long id;
  private Long userId;
  private String companyName;
  private String positionName;
  private String startDate;
  private String endDate;
  private String keywords;
  private String contentSummary;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getCompanyName() { return companyName; }
  public void setCompanyName(String companyName) { this.companyName = companyName; }
  public String getPositionName() { return positionName; }
  public void setPositionName(String positionName) { this.positionName = positionName; }
  public String getStartDate() { return startDate; }
  public void setStartDate(String startDate) { this.startDate = startDate; }
  public String getEndDate() { return endDate; }
  public void setEndDate(String endDate) { this.endDate = endDate; }
  public String getKeywords() { return keywords; }
  public void setKeywords(String keywords) { this.keywords = keywords; }
  public String getContentSummary() { return contentSummary; }
  public void setContentSummary(String contentSummary) { this.contentSummary = contentSummary; }
}
