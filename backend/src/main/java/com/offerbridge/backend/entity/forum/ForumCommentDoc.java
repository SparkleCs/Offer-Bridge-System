package com.offerbridge.backend.entity.forum;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "forum_comments")
@CompoundIndexes({
  @CompoundIndex(name = "idx_post_status_created", def = "{\"postId\":1,\"status\":1,\"createdAt\":1}")
})
public class ForumCommentDoc {
  @Id
  private String id;
  private String postId;
  private Long authorUserId;
  private String content;
  private String status;
  private Instant createdAt;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
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
