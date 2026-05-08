package com.offerbridge.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UsSchoolDifficultyProfile {
  private Long id;
  private Long schoolId;
  private BigDecimal schoolSelectivityScore;
  private BigDecimal admissionBarScore;
  private BigDecimal schoolHeatScore;
  private String difficultyVersion;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getSchoolId() { return schoolId; }
  public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
  public BigDecimal getSchoolSelectivityScore() { return schoolSelectivityScore; }
  public void setSchoolSelectivityScore(BigDecimal schoolSelectivityScore) { this.schoolSelectivityScore = schoolSelectivityScore; }
  public BigDecimal getAdmissionBarScore() { return admissionBarScore; }
  public void setAdmissionBarScore(BigDecimal admissionBarScore) { this.admissionBarScore = admissionBarScore; }
  public BigDecimal getSchoolHeatScore() { return schoolHeatScore; }
  public void setSchoolHeatScore(BigDecimal schoolHeatScore) { this.schoolHeatScore = schoolHeatScore; }
  public String getDifficultyVersion() { return difficultyVersion; }
  public void setDifficultyVersion(String difficultyVersion) { this.difficultyVersion = difficultyVersion; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
