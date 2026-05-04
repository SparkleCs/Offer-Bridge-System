package com.offerbridge.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AiProgramRecommendation {
  private Long id;
  private Long reportId;
  private Long programId;
  private Integer matchScore;
  private String matchTier;
  private BigDecimal probabilityEstimate;
  private String confidenceLevel;
  private String reasonTagsJson;
  private String aiSummary;
  private LocalDateTime createdAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getReportId() { return reportId; }
  public void setReportId(Long reportId) { this.reportId = reportId; }
  public Long getProgramId() { return programId; }
  public void setProgramId(Long programId) { this.programId = programId; }
  public Integer getMatchScore() { return matchScore; }
  public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }
  public String getMatchTier() { return matchTier; }
  public void setMatchTier(String matchTier) { this.matchTier = matchTier; }
  public BigDecimal getProbabilityEstimate() { return probabilityEstimate; }
  public void setProbabilityEstimate(BigDecimal probabilityEstimate) { this.probabilityEstimate = probabilityEstimate; }
  public String getConfidenceLevel() { return confidenceLevel; }
  public void setConfidenceLevel(String confidenceLevel) { this.confidenceLevel = confidenceLevel; }
  public String getReasonTagsJson() { return reasonTagsJson; }
  public void setReasonTagsJson(String reasonTagsJson) { this.reasonTagsJson = reasonTagsJson; }
  public String getAiSummary() { return aiSummary; }
  public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
