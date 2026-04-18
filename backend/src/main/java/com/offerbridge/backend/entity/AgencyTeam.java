package com.offerbridge.backend.entity;

public class AgencyTeam {
  private Long id;
  private Long orgId;
  private String teamName;
  private String teamType;
  private String teamIntro;
  private String serviceCountryScope;
  private String serviceMajorScope;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getOrgId() { return orgId; }
  public void setOrgId(Long orgId) { this.orgId = orgId; }
  public String getTeamName() { return teamName; }
  public void setTeamName(String teamName) { this.teamName = teamName; }
  public String getTeamType() { return teamType; }
  public void setTeamType(String teamType) { this.teamType = teamType; }
  public String getTeamIntro() { return teamIntro; }
  public void setTeamIntro(String teamIntro) { this.teamIntro = teamIntro; }
  public String getServiceCountryScope() { return serviceCountryScope; }
  public void setServiceCountryScope(String serviceCountryScope) { this.serviceCountryScope = serviceCountryScope; }
  public String getServiceMajorScope() { return serviceMajorScope; }
  public void setServiceMajorScope(String serviceMajorScope) { this.serviceMajorScope = serviceMajorScope; }
}
