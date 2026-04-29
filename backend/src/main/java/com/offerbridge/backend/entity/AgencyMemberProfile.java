package com.offerbridge.backend.entity;

public class AgencyMemberProfile {
  private Long id;
  private Long userId;
  private Long orgId;
  private String displayName;
  private String avatarUrl;
  private String realName;
  private String wechatId;
  private String jobTitle;
  private String educationLevel;
  private String graduatedSchool;
  private String major;
  private Integer yearsOfExperience;
  private String specialCountries;
  private String specialDirections;
  private String bio;
  private String serviceStyleTags;
  private String publicStatus;
  private String verifiedBadgeStatus;
  private String profileAuditStatus;
  private String status;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public Long getOrgId() { return orgId; }
  public void setOrgId(Long orgId) { this.orgId = orgId; }
  public String getDisplayName() { return displayName; }
  public void setDisplayName(String displayName) { this.displayName = displayName; }
  public String getAvatarUrl() { return avatarUrl; }
  public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
  public String getRealName() { return realName; }
  public void setRealName(String realName) { this.realName = realName; }
  public String getWechatId() { return wechatId; }
  public void setWechatId(String wechatId) { this.wechatId = wechatId; }
  public String getJobTitle() { return jobTitle; }
  public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
  public String getEducationLevel() { return educationLevel; }
  public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }
  public String getGraduatedSchool() { return graduatedSchool; }
  public void setGraduatedSchool(String graduatedSchool) { this.graduatedSchool = graduatedSchool; }
  public String getMajor() { return major; }
  public void setMajor(String major) { this.major = major; }
  public Integer getYearsOfExperience() { return yearsOfExperience; }
  public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
  public String getSpecialCountries() { return specialCountries; }
  public void setSpecialCountries(String specialCountries) { this.specialCountries = specialCountries; }
  public String getSpecialDirections() { return specialDirections; }
  public void setSpecialDirections(String specialDirections) { this.specialDirections = specialDirections; }
  public String getBio() { return bio; }
  public void setBio(String bio) { this.bio = bio; }
  public String getServiceStyleTags() { return serviceStyleTags; }
  public void setServiceStyleTags(String serviceStyleTags) { this.serviceStyleTags = serviceStyleTags; }
  public String getPublicStatus() { return publicStatus; }
  public void setPublicStatus(String publicStatus) { this.publicStatus = publicStatus; }
  public String getVerifiedBadgeStatus() { return verifiedBadgeStatus; }
  public void setVerifiedBadgeStatus(String verifiedBadgeStatus) { this.verifiedBadgeStatus = verifiedBadgeStatus; }
  public String getProfileAuditStatus() { return profileAuditStatus; }
  public void setProfileAuditStatus(String profileAuditStatus) { this.profileAuditStatus = profileAuditStatus; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
}
