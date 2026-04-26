package com.offerbridge.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServiceOrder {
  private Long id;
  private String orderNo;
  private Long studentUserId;
  private Long orgId;
  private Long teamId;
  private Long assignedMemberId;
  private String teamNameSnapshot;
  private String orgNameSnapshot;
  private String serviceTitle;
  private String quoteDesc;
  private BigDecimal finalAmount;
  private String currency;
  private String orderStatus;
  private String paymentStatus;
  private LocalDateTime paidAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getOrderNo() { return orderNo; }
  public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
  public Long getStudentUserId() { return studentUserId; }
  public void setStudentUserId(Long studentUserId) { this.studentUserId = studentUserId; }
  public Long getOrgId() { return orgId; }
  public void setOrgId(Long orgId) { this.orgId = orgId; }
  public Long getTeamId() { return teamId; }
  public void setTeamId(Long teamId) { this.teamId = teamId; }
  public Long getAssignedMemberId() { return assignedMemberId; }
  public void setAssignedMemberId(Long assignedMemberId) { this.assignedMemberId = assignedMemberId; }
  public String getTeamNameSnapshot() { return teamNameSnapshot; }
  public void setTeamNameSnapshot(String teamNameSnapshot) { this.teamNameSnapshot = teamNameSnapshot; }
  public String getOrgNameSnapshot() { return orgNameSnapshot; }
  public void setOrgNameSnapshot(String orgNameSnapshot) { this.orgNameSnapshot = orgNameSnapshot; }
  public String getServiceTitle() { return serviceTitle; }
  public void setServiceTitle(String serviceTitle) { this.serviceTitle = serviceTitle; }
  public String getQuoteDesc() { return quoteDesc; }
  public void setQuoteDesc(String quoteDesc) { this.quoteDesc = quoteDesc; }
  public BigDecimal getFinalAmount() { return finalAmount; }
  public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public String getOrderStatus() { return orderStatus; }
  public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
  public String getPaymentStatus() { return paymentStatus; }
  public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
  public LocalDateTime getPaidAt() { return paidAt; }
  public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}
