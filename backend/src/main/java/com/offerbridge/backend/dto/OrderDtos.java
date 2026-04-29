package com.offerbridge.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderDtos {
  public static class CreateOrderRequest {
    @NotNull private Long teamId;
    private String remark;
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
  }

  public static class QuoteOrderRequest {
    @NotNull @DecimalMin(value = "0.01") private BigDecimal finalAmount;
    @NotBlank private String serviceTitle;
    @NotBlank private String quoteDesc;
    private Long assignedMemberId;
    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
    public String getServiceTitle() { return serviceTitle; }
    public void setServiceTitle(String serviceTitle) { this.serviceTitle = serviceTitle; }
    public String getQuoteDesc() { return quoteDesc; }
    public void setQuoteDesc(String quoteDesc) { this.quoteDesc = quoteDesc; }
    public Long getAssignedMemberId() { return assignedMemberId; }
    public void setAssignedMemberId(Long assignedMemberId) { this.assignedMemberId = assignedMemberId; }
  }

  public static class SubmitStageRequest {
    @NotBlank private String deliverableText;
    private String deliverableUrl;
    private List<StageAttachmentRequest> attachments = new ArrayList<>();
    public String getDeliverableText() { return deliverableText; }
    public void setDeliverableText(String deliverableText) { this.deliverableText = deliverableText; }
    public String getDeliverableUrl() { return deliverableUrl; }
    public void setDeliverableUrl(String deliverableUrl) { this.deliverableUrl = deliverableUrl; }
    public List<StageAttachmentRequest> getAttachments() { return attachments; }
    public void setAttachments(List<StageAttachmentRequest> attachments) { this.attachments = attachments; }
  }

  public static class StageAttachmentRequest {
    @NotBlank private String fileName;
    @NotBlank private String fileUrl;
    @NotBlank private String contentType;
    private String mimeType;
    private Long sizeBytes;
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }
  }

  public static class RejectStageRequest {
    @NotBlank private String feedback;
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
  }

  public static class CreateTodoRequest {
    @NotBlank private String title;
    private String description;
    private Long stageId;
    @NotBlank private String ownerRole;
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    public String getOwnerRole() { return ownerRole; }
    public void setOwnerRole(String ownerRole) { this.ownerRole = ownerRole; }
  }

  public static class RefundRequest {
    @NotBlank private String reason;
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
  }

  public static class PayResult {
    private String paymentNo;
    private String paymentUrl;
    private String paymentFormHtml;
    private String channel;
    private String message;
    public String getPaymentNo() { return paymentNo; }
    public void setPaymentNo(String paymentNo) { this.paymentNo = paymentNo; }
    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
    public String getPaymentFormHtml() { return paymentFormHtml; }
    public void setPaymentFormHtml(String paymentFormHtml) { this.paymentFormHtml = paymentFormHtml; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
  }

  public static class OrderSummary {
    private Long id;
    private String orderNo;
    private Long teamId;
    private String teamNameSnapshot;
    private String orgNameSnapshot;
    private String serviceTitle;
    private String quoteDesc;
    private BigDecimal finalAmount;
    private String currency;
    private String orderStatus;
    private String paymentStatus;
    private String createdAt;
    private String updatedAt;
    private Long caseId;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
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
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }
  }

  public static class AgentOrderSummary extends OrderSummary {
    private Long studentUserId;
    private String studentPhone;
    private String studentName;
    public Long getStudentUserId() { return studentUserId; }
    public void setStudentUserId(Long studentUserId) { this.studentUserId = studentUserId; }
    public String getStudentPhone() { return studentPhone; }
    public void setStudentPhone(String studentPhone) { this.studentPhone = studentPhone; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
  }

  public static class StageItem {
    private Long id;
    private Long caseId;
    private String stageKey;
    private String stageName;
    private Integer stageOrder;
    private String status;
    private String deliverableText;
    private String deliverableUrl;
    private List<StageAttachment> attachments = List.of();
    private String studentFeedback;
    private String submittedAt;
    private String completedAt;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }
    public String getStageKey() { return stageKey; }
    public void setStageKey(String stageKey) { this.stageKey = stageKey; }
    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }
    public Integer getStageOrder() { return stageOrder; }
    public void setStageOrder(Integer stageOrder) { this.stageOrder = stageOrder; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDeliverableText() { return deliverableText; }
    public void setDeliverableText(String deliverableText) { this.deliverableText = deliverableText; }
    public String getDeliverableUrl() { return deliverableUrl; }
    public void setDeliverableUrl(String deliverableUrl) { this.deliverableUrl = deliverableUrl; }
    public List<StageAttachment> getAttachments() { return attachments; }
    public void setAttachments(List<StageAttachment> attachments) { this.attachments = attachments; }
    public String getStudentFeedback() { return studentFeedback; }
    public void setStudentFeedback(String studentFeedback) { this.studentFeedback = studentFeedback; }
    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
  }

  public static class StageAttachment {
    private Long id;
    private Long stageId;
    private String fileName;
    private String fileUrl;
    private String contentType;
    private String mimeType;
    private Long sizeBytes;
    private String uploadedAt;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }
    public String getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(String uploadedAt) { this.uploadedAt = uploadedAt; }
  }

  public static class TodoItem {
    private Long id;
    private Long caseId;
    private Long stageId;
    private String title;
    private String description;
    private String ownerRole;
    private String status;
    private String completedAt;
    private String confirmedAt;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }
    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOwnerRole() { return ownerRole; }
    public void setOwnerRole(String ownerRole) { this.ownerRole = ownerRole; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
    public String getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(String confirmedAt) { this.confirmedAt = confirmedAt; }
  }

  public static class OrderDetail {
    private OrderSummary order;
    private List<StageItem> stages;
    private List<TodoItem> todos;
    public OrderSummary getOrder() { return order; }
    public void setOrder(OrderSummary order) { this.order = order; }
    public List<StageItem> getStages() { return stages; }
    public void setStages(List<StageItem> stages) { this.stages = stages; }
    public List<TodoItem> getTodos() { return todos; }
    public void setTodos(List<TodoItem> todos) { this.todos = todos; }
  }
}
