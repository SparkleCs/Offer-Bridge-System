package com.offerbridge.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AiSchoolRecommendation {
  private Long id;
  private Long reportId;
  private Long schoolId;
  private Integer matchScore;
  private String matchTier;
  private BigDecimal probabilityEstimate;
  private String confidenceLevel;
  private BigDecimal gpaScore;
  private BigDecimal languageScore;
  private BigDecimal softBackgroundScore;
  private BigDecimal schoolSelectivityScore;
  private BigDecimal admissionBarScore;
  private BigDecimal schoolHeatScore;
  private String reasonTagsJson;
  private String aiSummary;
  private LocalDateTime createdAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getReportId() { return reportId; }
  public void setReportId(Long reportId) { this.reportId = reportId; }
  public Long getSchoolId() { return schoolId; }
  public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
  public Integer getMatchScore() { return matchScore; }
  public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }
  public String getMatchTier() { return matchTier; }
  public void setMatchTier(String matchTier) { this.matchTier = matchTier; }
  public BigDecimal getProbabilityEstimate() { return probabilityEstimate; }
  public void setProbabilityEstimate(BigDecimal probabilityEstimate) { this.probabilityEstimate = probabilityEstimate; }
  public String getConfidenceLevel() { return confidenceLevel; }
  public void setConfidenceLevel(String confidenceLevel) { this.confidenceLevel = confidenceLevel; }
  public BigDecimal getGpaScore() { return gpaScore; }
  public void setGpaScore(BigDecimal gpaScore) { this.gpaScore = gpaScore; }
  public BigDecimal getLanguageScore() { return languageScore; }
  public void setLanguageScore(BigDecimal languageScore) { this.languageScore = languageScore; }
  public BigDecimal getSoftBackgroundScore() { return softBackgroundScore; }
  public void setSoftBackgroundScore(BigDecimal softBackgroundScore) { this.softBackgroundScore = softBackgroundScore; }
  public BigDecimal getSchoolSelectivityScore() { return schoolSelectivityScore; }
  public void setSchoolSelectivityScore(BigDecimal schoolSelectivityScore) { this.schoolSelectivityScore = schoolSelectivityScore; }
  public BigDecimal getAdmissionBarScore() { return admissionBarScore; }
  public void setAdmissionBarScore(BigDecimal admissionBarScore) { this.admissionBarScore = admissionBarScore; }
  public BigDecimal getSchoolHeatScore() { return schoolHeatScore; }
  public void setSchoolHeatScore(BigDecimal schoolHeatScore) { this.schoolHeatScore = schoolHeatScore; }
  public String getReasonTagsJson() { return reasonTagsJson; }
  public void setReasonTagsJson(String reasonTagsJson) { this.reasonTagsJson = reasonTagsJson; }
  public String getAiSummary() { return aiSummary; }
  public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
