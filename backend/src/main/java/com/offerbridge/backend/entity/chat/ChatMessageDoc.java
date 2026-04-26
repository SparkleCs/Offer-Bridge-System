package com.offerbridge.backend.entity.chat;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "chat_messages")
@CompoundIndexes({
  @CompoundIndex(name = "idx_conversation_created", def = "{'conversationId': 1, 'createdAt': -1}"),
  @CompoundIndex(name = "idx_receiver_status", def = "{'receiverUserId': 1, 'status': 1}")
})
public class ChatMessageDoc {
  @Id
  private String id;
  @Indexed
  private String conversationId;
  private Long senderUserId;
  private Long receiverUserId;
  private String senderRole;
  private String contentType;
  private String content;
  private String status;
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
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public Instant getReadAt() { return readAt; }
  public void setReadAt(Instant readAt) { this.readAt = readAt; }
}
