package com.offerbridge.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AuthDtos {
  public static class SendSmsRequest {
    @Pattern(regexp = "^1\\d{10}$")
    private String phone;
    @NotBlank
    private String scene;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getScene() { return scene; }
    public void setScene(String scene) { this.scene = scene; }
  }

  public static class SmsLoginRequest {
    @Pattern(regexp = "^1\\d{10}$")
    private String phone;
    @Pattern(regexp = "^\\d{4,6}$")
    private String code;
    @Pattern(regexp = "^(STUDENT|AGENT_ORG)$")
    private String role;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
  }

  public static class RefreshRequest {
    @NotBlank
    private String refreshToken;

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
  }

  public static class AuthResult {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String role;
    private boolean profileCompleted;
    private boolean verificationCompleted;

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isProfileCompleted() { return profileCompleted; }
    public void setProfileCompleted(boolean profileCompleted) { this.profileCompleted = profileCompleted; }
    public boolean isVerificationCompleted() { return verificationCompleted; }
    public void setVerificationCompleted(boolean verificationCompleted) { this.verificationCompleted = verificationCompleted; }
  }
}
