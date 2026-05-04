package com.offerbridge.backend.entity;

import java.time.LocalDateTime;

public class AiAnalysisReport {
  private Long id;
  private Long userId;
  private String reportType;
  private String inputSnapshotJson;
  private String resultJson;
  private String modelProvider;
  private String modelVersion;
  private String status;
  private LocalDateTime createdAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getReportType() { return reportType; }
  public void setReportType(String reportType) { this.reportType = reportType; }
  public String getInputSnapshotJson() { return inputSnapshotJson; }
  public void setInputSnapshotJson(String inputSnapshotJson) { this.inputSnapshotJson = inputSnapshotJson; }
  public String getResultJson() { return resultJson; }
  public void setResultJson(String resultJson) { this.resultJson = resultJson; }
  public String getModelProvider() { return modelProvider; }
  public void setModelProvider(String modelProvider) { this.modelProvider = modelProvider; }
  public String getModelVersion() { return modelVersion; }
  public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
