package com.offerbridge.backend.entity;

public class StudentWorkExperience {
  private Long id;
  private Long userId;
  private String companyName;
  private String companyTier;
  private String positionName;
  private String relevanceLevel;
  private String titleLevel;
  private String startDate;
  private String endDate;
  private Integer durationMonths;
  private String keywords;
  private String contentSummary;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getCompanyName() { return companyName; }
  public void setCompanyName(String companyName) { this.companyName = companyName; }
  public String getCompanyTier() { return companyTier; }
  public void setCompanyTier(String companyTier) { this.companyTier = companyTier; }
  public String getPositionName() { return positionName; }
  public void setPositionName(String positionName) { this.positionName = positionName; }
  public String getRelevanceLevel() { return relevanceLevel; }
  public void setRelevanceLevel(String relevanceLevel) { this.relevanceLevel = relevanceLevel; }
  public String getTitleLevel() { return titleLevel; }
  public void setTitleLevel(String titleLevel) { this.titleLevel = titleLevel; }
  public String getStartDate() { return startDate; }
  public void setStartDate(String startDate) { this.startDate = startDate; }
  public String getEndDate() { return endDate; }
  public void setEndDate(String endDate) { this.endDate = endDate; }
  public Integer getDurationMonths() { return durationMonths; }
  public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }
  public String getKeywords() { return keywords; }
  public void setKeywords(String keywords) { this.keywords = keywords; }
  public String getContentSummary() { return contentSummary; }
  public void setContentSummary(String contentSummary) { this.contentSummary = contentSummary; }
}
