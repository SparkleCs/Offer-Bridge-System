package com.offerbridge.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MessageDtos {
  public static class SystemNotificationItem {
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

  public static class PagedResult<T> {
    private List<T> records;
    private long total;
    private int page;
    private int pageSize;
    private long unreadCount;

    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }
  }

  public static class MarkReadRequest {
    private Boolean markAll;
    private List<Long> ids;

    public Boolean getMarkAll() { return markAll; }
    public void setMarkAll(Boolean markAll) { this.markAll = markAll; }
    public List<Long> getIds() { return ids; }
    public void setIds(List<Long> ids) { this.ids = ids; }
  }

  public static class MarkReadResult {
    private long updatedCount;

    public long getUpdatedCount() { return updatedCount; }
    public void setUpdatedCount(long updatedCount) { this.updatedCount = updatedCount; }
  }
}

