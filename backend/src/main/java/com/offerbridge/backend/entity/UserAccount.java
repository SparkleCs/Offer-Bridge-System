package com.offerbridge.backend.entity;

import java.time.LocalDateTime;

public class UserAccount {
  private Long id;
  private String phone;
  private String role;
  private String status;
  private LocalDateTime lastLoginAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public LocalDateTime getLastLoginAt() { return lastLoginAt; }
  public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}
