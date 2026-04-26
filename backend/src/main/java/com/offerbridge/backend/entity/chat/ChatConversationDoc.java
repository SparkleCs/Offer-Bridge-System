package com.offerbridge.backend.entity.chat;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "chat_conversations")
@CompoundIndexes({
  @CompoundIndex(name = "uk_student_team_agent", def = "{'studentUserId': 1, 'teamId': 1, 'agentMemberId': 1}", unique = true),
  @CompoundIndex(name = "idx_student_updated", def = "{'studentUserId': 1, 'updatedAt': -1}"),
  @CompoundIndex(name = "idx_agent_updated", def = "{'agentUserId': 1, 'updatedAt': -1}")
})
public class ChatConversationDoc {
  @Id
  private String id;
  @Indexed
  private Long studentUserId;
  @Indexed
  private Long agentUserId;
  private Long agentMemberId;
  private Long teamId;
  private Long orgId;
  private String teamName;
  private String orgName;
  private String agentName;
  private String agentAvatarUrl;
  private String agentJobTitle;
  private String lastMessage;
  private Integer unreadByStudent;
  private Integer unreadByAgent;
  private Instant createdAt;
  private Instant updatedAt;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
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
  public String getLastMessage() { return lastMessage; }
  public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
  public Integer getUnreadByStudent() { return unreadByStudent; }
  public void setUnreadByStudent(Integer unreadByStudent) { this.unreadByStudent = unreadByStudent; }
  public Integer getUnreadByAgent() { return unreadByAgent; }
  public void setUnreadByAgent(Integer unreadByAgent) { this.unreadByAgent = unreadByAgent; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
