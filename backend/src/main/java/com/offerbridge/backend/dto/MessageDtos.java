package com.offerbridge.backend.dto;

import java.time.LocalDateTime;
import java.time.Instant;
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

  public static class StartChatRequest {
    private Long teamId;
    private String greeting;

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public String getGreeting() { return greeting; }
    public void setGreeting(String greeting) { this.greeting = greeting; }
  }

  public static class SendChatMessageRequest {
    private String content;
    private String contentType;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
  }

  public static class ChatConversationItem {
    private String conversationId;
    private Long studentUserId;
    private Long agentUserId;
    private Long agentMemberId;
    private Long teamId;
    private Long orgId;
    private String teamName;
    private String orgName;
    private String agentName;
    private String agentAvatarUrl;
    private String agentJobTitle;
    private String studentName;
    private String studentSchoolName;
    private String studentMajor;
    private String studentEducationLevel;
    private String studentTargetMajorText;
    private String peerName;
    private String peerSubtitle;
    private String peerAvatarUrl;
    private String lastMessage;
    private int unreadCount;
    private Instant createdAt;
    private Instant updatedAt;

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    public Long getStudentUserId() { return studentUserId; }
    public void setStudentUserId(Long studentUserId) { this.studentUserId = studentUserId; }
    public Long getAgentUserId() { return agentUserId; }
    public void setAgentUserId(Long agentUserId) { this.agentUserId = agentUserId; }
    public Long getAgentMemberId() { return agentMemberId; }
    public void setAgentMemberId(Long agentMemberId) { this.agentMemberId = agentMemberId; }
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public String getAgentAvatarUrl() { return agentAvatarUrl; }
    public void setAgentAvatarUrl(String agentAvatarUrl) { this.agentAvatarUrl = agentAvatarUrl; }
    public String getAgentJobTitle() { return agentJobTitle; }
    public void setAgentJobTitle(String agentJobTitle) { this.agentJobTitle = agentJobTitle; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentSchoolName() { return studentSchoolName; }
    public void setStudentSchoolName(String studentSchoolName) { this.studentSchoolName = studentSchoolName; }
    public String getStudentMajor() { return studentMajor; }
    public void setStudentMajor(String studentMajor) { this.studentMajor = studentMajor; }
    public String getStudentEducationLevel() { return studentEducationLevel; }
    public void setStudentEducationLevel(String studentEducationLevel) { this.studentEducationLevel = studentEducationLevel; }
    public String getStudentTargetMajorText() { return studentTargetMajorText; }
    public void setStudentTargetMajorText(String studentTargetMajorText) { this.studentTargetMajorText = studentTargetMajorText; }
    public String getPeerName() { return peerName; }
    public void setPeerName(String peerName) { this.peerName = peerName; }
    public String getPeerSubtitle() { return peerSubtitle; }
    public void setPeerSubtitle(String peerSubtitle) { this.peerSubtitle = peerSubtitle; }
    public String getPeerAvatarUrl() { return peerAvatarUrl; }
    public void setPeerAvatarUrl(String peerAvatarUrl) { this.peerAvatarUrl = peerAvatarUrl; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
  }

  public static class ChatMessageItem {
    private String id;
    private String conversationId;
    private Long senderUserId;
    private Long receiverUserId;
    private String senderRole;
    private String contentType;
    private String content;
    private String status;
    private boolean mine;
    private Instant createdAt;
    private Instant readAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    public Long getSenderUserId() { return senderUserId; }
    public void setSenderUserId(Long senderUserId) { this.senderUserId = senderUserId; }
    public Long getReceiverUserId() { return receiverUserId; }
    public void setReceiverUserId(Long receiverUserId) { this.receiverUserId = receiverUserId; }
    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isMine() { return mine; }
    public void setMine(boolean mine) { this.mine = mine; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }
  }

  public static class ChatStartResult {
    private ChatConversationItem conversation;
    private ChatMessageItem firstMessage;

    public ChatConversationItem getConversation() { return conversation; }
    public void setConversation(ChatConversationItem conversation) { this.conversation = conversation; }
    public ChatMessageItem getFirstMessage() { return firstMessage; }
    public void setFirstMessage(ChatMessageItem firstMessage) { this.firstMessage = firstMessage; }
  }

  public static class ChatUnreadSummary {
    private long unreadCount;

    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }
  }
}
