package com.offerbridge.backend.entity;

import java.time.LocalDateTime;

public class VerificationRecord {
  private Long id;
  private Long userId;
  private String verifyType;
  private String status;
  private String payloadJson;
  private String rejectReason;
  private LocalDateTime submittedAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getVerifyType() { return verifyType; }
  public void setVerifyType(String verifyType) { this.verifyType = verifyType; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getPayloadJson() { return payloadJson; }
  public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
  public String getRejectReason() { return rejectReason; }
  public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
  public LocalDateTime getSubmittedAt() { return submittedAt; }
  public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
