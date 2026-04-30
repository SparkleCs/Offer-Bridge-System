package com.offerbridge.backend.entity;

public class StudentResearchExperience {
  private Long id;
  private Long userId;
  private String projectName;
  private String roleName;
  private String roleLevel;
  private String relevanceLevel;
  private String startDate;
  private String endDate;
  private Integer durationMonths;
  private String contentSummary;
  private Boolean hasPublication;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getProjectName() { return projectName; }
  public void setProjectName(String projectName) { this.projectName = projectName; }
  public String getRoleName() { return roleName; }
  public void setRoleName(String roleName) { this.roleName = roleName; }
  public String getRoleLevel() { return roleLevel; }
  public void setRoleLevel(String roleLevel) { this.roleLevel = roleLevel; }
  public String getRelevanceLevel() { return relevanceLevel; }
  public void setRelevanceLevel(String relevanceLevel) { this.relevanceLevel = relevanceLevel; }
  public String getStartDate() { return startDate; }
  public void setStartDate(String startDate) { this.startDate = startDate; }
  public String getEndDate() { return endDate; }
  public void setEndDate(String endDate) { this.endDate = endDate; }
  public Integer getDurationMonths() { return durationMonths; }
  public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }
  public String getContentSummary() { return contentSummary; }
  public void setContentSummary(String contentSummary) { this.contentSummary = contentSummary; }
  public Boolean getHasPublication() { return hasPublication; }
  public void setHasPublication(Boolean hasPublication) { this.hasPublication = hasPublication; }
}
