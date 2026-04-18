package com.offerbridge.backend.entity;

import java.time.LocalDateTime;

public class AgencyInvitation {
  private Long id;
  private Long orgId;
  private Long teamId;
  private String email;
  private String inviteeName;
  private String roleHint;
  private String token;
  private String status;
  private LocalDateTime expiresAt;
  private Long createdBy;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getOrgId() { return orgId; }
  public void setOrgId(Long orgId) { this.orgId = orgId; }
  public Long getTeamId() { return teamId; }
  public void setTeamId(Long teamId) { this.teamId = teamId; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getInviteeName() { return inviteeName; }
  public void setInviteeName(String inviteeName) { this.inviteeName = inviteeName; }
  public String getRoleHint() { return roleHint; }
  public void setRoleHint(String roleHint) { this.roleHint = roleHint; }
  public String getToken() { return token; }
  public void setToken(String token) { this.token = token; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public LocalDateTime getExpiresAt() { return expiresAt; }
  public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
  public Long getCreatedBy() { return createdBy; }
  public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
}
