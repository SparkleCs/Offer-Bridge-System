package com.offerbridge.backend.entity;

public class StudentVerificationMaterial {
  private Long id;
  private Long userId;
  private String realName;
  private String idNoMasked;
  private String idCardImageUrl;
  private String studentCardImageUrl;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getRealName() { return realName; }
  public void setRealName(String realName) { this.realName = realName; }
  public String getIdNoMasked() { return idNoMasked; }
  public void setIdNoMasked(String idNoMasked) { this.idNoMasked = idNoMasked; }
  public String getIdCardImageUrl() { return idCardImageUrl; }
  public void setIdCardImageUrl(String idCardImageUrl) { this.idCardImageUrl = idCardImageUrl; }
  public String getStudentCardImageUrl() { return studentCardImageUrl; }
  public void setStudentCardImageUrl(String studentCardImageUrl) { this.studentCardImageUrl = studentCardImageUrl; }
}
