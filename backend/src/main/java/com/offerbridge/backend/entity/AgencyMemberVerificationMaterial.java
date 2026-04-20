package com.offerbridge.backend.entity;

public class AgencyMemberVerificationMaterial {
  private Long id;
  private Long userId;
  private String idCardImageUrl;
  private String employmentProofImageUrl;
  private String educationProofImageUrl;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getIdCardImageUrl() { return idCardImageUrl; }
  public void setIdCardImageUrl(String idCardImageUrl) { this.idCardImageUrl = idCardImageUrl; }
  public String getEmploymentProofImageUrl() { return employmentProofImageUrl; }
  public void setEmploymentProofImageUrl(String employmentProofImageUrl) { this.employmentProofImageUrl = employmentProofImageUrl; }
  public String getEducationProofImageUrl() { return educationProofImageUrl; }
  public void setEducationProofImageUrl(String educationProofImageUrl) { this.educationProofImageUrl = educationProofImageUrl; }
}

