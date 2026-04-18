package com.offerbridge.backend.entity;

import java.math.BigDecimal;

public class AgencyMemberMetrics {
  private Long id;
  private Long memberId;
  private Integer caseCount;
  private BigDecimal successRate;
  private BigDecimal avgRating;
  private BigDecimal responseEfficiencyScore;
  private String serviceTags;
  private String budgetTags;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getMemberId() { return memberId; }
  public void setMemberId(Long memberId) { this.memberId = memberId; }
  public Integer getCaseCount() { return caseCount; }
  public void setCaseCount(Integer caseCount) { this.caseCount = caseCount; }
  public BigDecimal getSuccessRate() { return successRate; }
  public void setSuccessRate(BigDecimal successRate) { this.successRate = successRate; }
  public BigDecimal getAvgRating() { return avgRating; }
  public void setAvgRating(BigDecimal avgRating) { this.avgRating = avgRating; }
  public BigDecimal getResponseEfficiencyScore() { return responseEfficiencyScore; }
  public void setResponseEfficiencyScore(BigDecimal responseEfficiencyScore) { this.responseEfficiencyScore = responseEfficiencyScore; }
  public String getServiceTags() { return serviceTags; }
  public void setServiceTags(String serviceTags) { this.serviceTags = serviceTags; }
  public String getBudgetTags() { return budgetTags; }
  public void setBudgetTags(String budgetTags) { this.budgetTags = budgetTags; }
}
