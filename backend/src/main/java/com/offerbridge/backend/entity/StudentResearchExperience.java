package com.offerbridge.backend.entity;

public class StudentResearchExperience {
  private Long id;
  private Long userId;
  private String projectName;
  private String startDate;
  private String endDate;
  private String contentSummary;
  private Boolean hasPublication;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getProjectName() { return projectName; }
  public void setProjectName(String projectName) { this.projectName = projectName; }
  public String getStartDate() { return startDate; }
  public void setStartDate(String startDate) { this.startDate = startDate; }
  public String getEndDate() { return endDate; }
  public void setEndDate(String endDate) { this.endDate = endDate; }
  public String getContentSummary() { return contentSummary; }
  public void setContentSummary(String contentSummary) { this.contentSummary = contentSummary; }
  public Boolean getHasPublication() { return hasPublication; }
  public void setHasPublication(Boolean hasPublication) { this.hasPublication = hasPublication; }
}
