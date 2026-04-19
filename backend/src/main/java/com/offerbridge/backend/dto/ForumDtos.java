package com.offerbridge.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public class ForumDtos {
  public static class CreatePostRequest {
    @NotBlank
    private String channel;
    @NotBlank
    private String title;
    @NotBlank
    private String contentHtml;
    @NotEmpty
    private List<String> tags;

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContentHtml() { return contentHtml; }
    public void setContentHtml(String contentHtml) { this.contentHtml = contentHtml; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
  }

  public static class CreateCommentRequest {
    @NotBlank
    private String content;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
  }

  public static class MarkNotificationsReadRequest {
    private Boolean markAll;
    private List<String> notificationIds;

    public Boolean getMarkAll() { return markAll; }
    public void setMarkAll(Boolean markAll) { this.markAll = markAll; }
    public List<String> getNotificationIds() { return notificationIds; }
    public void setNotificationIds(List<String> notificationIds) { this.notificationIds = notificationIds; }
  }

  public static class PostItem {
    private String postId;
    private Long authorUserId;
    private String authorDisplayName;
    private String channel;
    private String title;
    private String contentHtml;
    private String summary;
    private List<String> tags;
    private String status;
    private long likeCount;
    private long commentCount;
    private long favoriteCount;
    private long shareCount;
    private boolean viewerLiked;
    private boolean viewerFavorited;
    private Instant createdAt;
    private Instant updatedAt;

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public Long getAuthorUserId() { return authorUserId; }
    public void setAuthorUserId(Long authorUserId) { this.authorUserId = authorUserId; }
    public String getAuthorDisplayName() { return authorDisplayName; }
    public void setAuthorDisplayName(String authorDisplayName) { this.authorDisplayName = authorDisplayName; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContentHtml() { return contentHtml; }
    public void setContentHtml(String contentHtml) { this.contentHtml = contentHtml; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getLikeCount() { return likeCount; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }
    public long getCommentCount() { return commentCount; }
    public void setCommentCount(long commentCount) { this.commentCount = commentCount; }
    public long getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(long favoriteCount) { this.favoriteCount = favoriteCount; }
    public long getShareCount() { return shareCount; }
    public void setShareCount(long shareCount) { this.shareCount = shareCount; }
    public boolean isViewerLiked() { return viewerLiked; }
    public void setViewerLiked(boolean viewerLiked) { this.viewerLiked = viewerLiked; }
    public boolean isViewerFavorited() { return viewerFavorited; }
    public void setViewerFavorited(boolean viewerFavorited) { this.viewerFavorited = viewerFavorited; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
  }

  public static class PostListView {
    private long total;
    private int page;
    private int pageSize;
    private List<PostItem> items;

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public List<PostItem> getItems() { return items; }
    public void setItems(List<PostItem> items) { this.items = items; }
  }

  public static class CommentItem {
    private String commentId;
    private String postId;
    private Long authorUserId;
    private String content;
    private String status;
    private Instant createdAt;

    public String getCommentId() { return commentId; }
    public void setCommentId(String commentId) { this.commentId = commentId; }
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public Long getAuthorUserId() { return authorUserId; }
    public void setAuthorUserId(Long authorUserId) { this.authorUserId = authorUserId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  }

  public static class CommentListView {
    private List<CommentItem> items;

    public List<CommentItem> getItems() { return items; }
    public void setItems(List<CommentItem> items) { this.items = items; }
  }

  public static class InteractionStateView {
    private boolean liked;
    private boolean favorited;
    private long likeCount;
    private long favoriteCount;

    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }
    public boolean isFavorited() { return favorited; }
    public void setFavorited(boolean favorited) { this.favorited = favorited; }
    public long getLikeCount() { return likeCount; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }
    public long getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(long favoriteCount) { this.favoriteCount = favoriteCount; }
  }

  public static class ShareView {
    private String shareUrl;
    private long shareCount;

    public String getShareUrl() { return shareUrl; }
    public void setShareUrl(String shareUrl) { this.shareUrl = shareUrl; }
    public long getShareCount() { return shareCount; }
    public void setShareCount(long shareCount) { this.shareCount = shareCount; }
  }

  public static class NotificationItem {
    private String notificationId;
    private Long receiverUserId;
    private Long actorUserId;
    private String postId;
    private String type;
    private boolean read;
    private Instant createdAt;

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public Long getReceiverUserId() { return receiverUserId; }
    public void setReceiverUserId(Long receiverUserId) { this.receiverUserId = receiverUserId; }
    public Long getActorUserId() { return actorUserId; }
    public void setActorUserId(Long actorUserId) { this.actorUserId = actorUserId; }
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  }

  public static class NotificationListView {
    private long unreadCount;
    private int page;
    private int pageSize;
    private List<NotificationItem> items;

    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public List<NotificationItem> getItems() { return items; }
    public void setItems(List<NotificationItem> items) { this.items = items; }
  }

  public static class NotificationReadResult {
    private long updatedCount;

    public long getUpdatedCount() { return updatedCount; }
    public void setUpdatedCount(long updatedCount) { this.updatedCount = updatedCount; }
  }

  public static class PostQuery {
    private String channel;
    private String keyword;
    @NotNull
    private Integer page;
    @NotNull
    private Integer pageSize;

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
  }
}
