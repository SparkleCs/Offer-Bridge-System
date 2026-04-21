package com.offerbridge.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AgencyTeam {
  private Long id;
  private Long orgId;
  private String teamName;
  private String teamType;
  private String teamIntro;
  private String serviceCountryScope;
  private String serviceMajorScope;
  private BigDecimal priceMin;
  private BigDecimal priceMax;
  private String publishStatus;
  private LocalDateTime publishedAt;
  private Long publishedBy;

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
  public BigDecimal getPriceMin() { return priceMin; }
  public void setPriceMin(BigDecimal priceMin) { this.priceMin = priceMin; }
  public BigDecimal getPriceMax() { return priceMax; }
  public void setPriceMax(BigDecimal priceMax) { this.priceMax = priceMax; }
  public String getPublishStatus() { return publishStatus; }
  public void setPublishStatus(String publishStatus) { this.publishStatus = publishStatus; }
  public LocalDateTime getPublishedAt() { return publishedAt; }
  public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
  public Long getPublishedBy() { return publishedBy; }
  public void setPublishedBy(Long publishedBy) { this.publishedBy = publishedBy; }
}
