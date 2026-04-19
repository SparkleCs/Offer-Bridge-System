package com.offerbridge.backend.entity.forum;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "forum_reactions")
@CompoundIndexes({
  @CompoundIndex(name = "uniq_user_post_action", def = "{\"userId\":1,\"postId\":1,\"actionType\":1}", unique = true),
  @CompoundIndex(name = "idx_post_action", def = "{\"postId\":1,\"actionType\":1}")
})
public class ForumReactionDoc {
  @Id
  private String id;
  private Long userId;
  private String postId;
  private String actionType;
  private Instant createdAt;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getPostId() { return postId; }
  public void setPostId(String postId) { this.postId = postId; }
  public String getActionType() { return actionType; }
  public void setActionType(String actionType) { this.actionType = actionType; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
