package com.offerbridge.backend.entity;

public class StudentCompetitionExperience {
  private Long id;
  private Long userId;
  private String competitionName;
  private String competitionLevel;
  private String award;
  private String roleDesc;
  private String eventDate;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getCompetitionName() { return competitionName; }
  public void setCompetitionName(String competitionName) { this.competitionName = competitionName; }
  public String getCompetitionLevel() { return competitionLevel; }
  public void setCompetitionLevel(String competitionLevel) { this.competitionLevel = competitionLevel; }
  public String getAward() { return award; }
  public void setAward(String award) { this.award = award; }
  public String getRoleDesc() { return roleDesc; }
  public void setRoleDesc(String roleDesc) { this.roleDesc = roleDesc; }
  public String getEventDate() { return eventDate; }
  public void setEventDate(String eventDate) { this.eventDate = eventDate; }
}
