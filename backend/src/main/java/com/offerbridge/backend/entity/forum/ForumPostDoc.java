package com.offerbridge.backend.entity.forum;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "forum_posts")
@CompoundIndexes({
  @CompoundIndex(name = "idx_channel_status_created", def = "{\"channel\":1,\"status\":1,\"createdAt\":-1}")
})
public class ForumPostDoc {
  @Id
  private String id;
  @Indexed
  private Long authorUserId;
  @Indexed
  private String channel;
  @TextIndexed(weight = 6)
  private String title;
  private String contentHtml;
  @TextIndexed(weight = 2)
  private String contentText;
  private List<String> tags;
  @Indexed
  private String status;
  private long likeCount;
  private long commentCount;
  private long favoriteCount;
  private long shareCount;
  @Indexed
  private Instant createdAt;
  private Instant updatedAt;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public Long getAuthorUserId() { return authorUserId; }
  public void setAuthorUserId(Long authorUserId) { this.authorUserId = authorUserId; }
  public String getChannel() { return channel; }
  public void setChannel(String channel) { this.channel = channel; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getContentHtml() { return contentHtml; }
  public void setContentHtml(String contentHtml) { this.contentHtml = contentHtml; }
  public String getContentText() { return contentText; }
  public void setContentText(String contentText) { this.contentText = contentText; }
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
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
