package com.offerbridge.backend.entity;

import java.time.LocalDateTime;

public class SystemNotification {
  private Long id;
  private Long userId;
  private String type;
  private String title;
  private String content;
  private String status;
  private String relatedType;
  private String relatedId;
  private LocalDateTime createdAt;
  private LocalDateTime readAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getContent() { return content; }
  public void setContent(String content) { this.content = content; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getRelatedType() { return relatedType; }
  public void setRelatedType(String relatedType) { this.relatedType = relatedType; }
  public String getRelatedId() { return relatedId; }
  public void setRelatedId(String relatedId) { this.relatedId = relatedId; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getReadAt() { return readAt; }
  public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}

