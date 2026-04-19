package com.offerbridge.backend.entity.forum;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "forum_notifications")
@CompoundIndexes({
  @CompoundIndex(name = "idx_receiver_read_created", def = "{\"receiverUserId\":1,\"read\":1,\"createdAt\":-1}")
})
public class ForumNotificationDoc {
  @Id
  private String id;
  private Long receiverUserId;
  private Long actorUserId;
  private String postId;
  private String type;
  private boolean read;
  private Instant createdAt;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
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
