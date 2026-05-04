package com.offerbridge.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.List;

public class AgencyDtos {
  public static class OrgProfileUpsertRequest {
    @NotBlank private String orgName;
    private String brandName;
    @NotBlank private String countryCode;
    @NotBlank private String provinceOrState;
    @NotBlank private String city;
    private String district;
    @NotBlank private String addressLine;
    private String logoUrl;
    private String coverImageUrl;
    private String officeEnvironmentImages;
    @NotBlank private String contactPhone;
    @Email private String contactEmail;
    private String websiteUrl;
    private String socialLinks;
    @Min(1900) @Max(2100) private Integer foundedYear;
    private String teamSizeRange;
    @NotBlank private String serviceMode;
    @NotBlank private String orgIntro;
    private String orgSlogan;

    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getProvinceOrState() { return provinceOrState; }
    public void setProvinceOrState(String provinceOrState) { this.provinceOrState = provinceOrState; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
    public String getOfficeEnvironmentImages() { return officeEnvironmentImages; }
    public void setOfficeEnvironmentImages(String officeEnvironmentImages) { this.officeEnvironmentImages = officeEnvironmentImages; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
    public String getSocialLinks() { return socialLinks; }
    public void setSocialLinks(String socialLinks) { this.socialLinks = socialLinks; }
    public Integer getFoundedYear() { return foundedYear; }
    public void setFoundedYear(Integer foundedYear) { this.foundedYear = foundedYear; }
    public String getTeamSizeRange() { return teamSizeRange; }
    public void setTeamSizeRange(String teamSizeRange) { this.teamSizeRange = teamSizeRange; }
    public String getServiceMode() { return serviceMode; }
    public void setServiceMode(String serviceMode) { this.serviceMode = serviceMode; }
    public String getOrgIntro() { return orgIntro; }
    public void setOrgIntro(String orgIntro) { this.orgIntro = orgIntro; }
    public String getOrgSlogan() { return orgSlogan; }
    public void setOrgSlogan(String orgSlogan) { this.orgSlogan = orgSlogan; }
  }

  public static class OrgProfileView {
    private Long id;
    private String orgName;
    private String brandName;
    private String countryCode;
    private String provinceOrState;
    private String city;
    private String district;
    private String addressLine;
    private String logoUrl;
    private String coverImageUrl;
    private String officeEnvironmentImages;
    private String contactPhone;
    private String contactEmail;
    private String websiteUrl;
    private String socialLinks;
    private Integer foundedYear;
    private String teamSizeRange;
    private String serviceMode;
    private String orgIntro;
    private String orgSlogan;
    private String verificationStatus;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getProvinceOrState() { return provinceOrState; }
    public void setProvinceOrState(String provinceOrState) { this.provinceOrState = provinceOrState; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
    public String getOfficeEnvironmentImages() { return officeEnvironmentImages; }
    public void setOfficeEnvironmentImages(String officeEnvironmentImages) { this.officeEnvironmentImages = officeEnvironmentImages; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
    public String getSocialLinks() { return socialLinks; }
    public void setSocialLinks(String socialLinks) { this.socialLinks = socialLinks; }
    public Integer getFoundedYear() { return foundedYear; }
    public void setFoundedYear(Integer foundedYear) { this.foundedYear = foundedYear; }
    public String getTeamSizeRange() { return teamSizeRange; }
    public void setTeamSizeRange(String teamSizeRange) { this.teamSizeRange = teamSizeRange; }
    public String getServiceMode() { return serviceMode; }
    public void setServiceMode(String serviceMode) { this.serviceMode = serviceMode; }
    public String getOrgIntro() { return orgIntro; }
    public void setOrgIntro(String orgIntro) { this.orgIntro = orgIntro; }
    public String getOrgSlogan() { return orgSlogan; }
    public void setOrgSlogan(String orgSlogan) { this.orgSlogan = orgSlogan; }
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
  }

  public static class TeamCreateRequest {
    @NotBlank private String teamName;
    private String teamType;
    private String teamIntro;
    @NotBlank private String serviceCountryScope;
    @NotBlank private String serviceMajorScope;

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getTeamType() { return teamType; }
    public void setTeamType(String teamType) { this.teamType = teamType; }
    public String getTeamIntro() { return teamIntro; }
    public void setTeamIntro(String teamIntro) { this.teamIntro = teamIntro; }
    public String getServiceCountryScope() { return serviceCountryScope; }
    public void setServiceCountryScope(String serviceCountryScope) { this.serviceCountryScope = serviceCountryScope; }
    public String getServiceMajorScope() { return serviceMajorScope; }
    public void setServiceMajorScope(String serviceMajorScope) { this.serviceMajorScope = serviceMajorScope; }
  }

  public static class TeamView {
    private Long id;
    private String teamName;
    private String teamType;
    private String teamIntro;
    private String serviceCountryScope;
    private String serviceMajorScope;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private String publishStatus;
    private String updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getTeamType() { return teamType; }
    public void setTeamType(String teamType) { this.teamType = teamType; }
    public String getTeamIntro() { return teamIntro; }
    public void setTeamIntro(String teamIntro) { this.teamIntro = teamIntro; }
    public String getServiceCountryScope() { return serviceCountryScope; }
    public void setServiceCountryScope(String serviceCountryScope) { this.serviceCountryScope = serviceCountryScope; }
    public String getServiceMajorScope() { return serviceMajorScope; }
    public void setServiceMajorScope(String serviceMajorScope) { this.serviceMajorScope = serviceMajorScope; }
    public BigDecimal getPriceMin() { return priceMin; }
    public void setPriceMin(BigDecimal priceMin) { this.priceMin = priceMin; }
    public BigDecimal getPriceMax() { return priceMax; }
    public void setPriceMax(BigDecimal priceMax) { this.priceMax = priceMax; }
    public String getPublishStatus() { return publishStatus; }
    public void setPublishStatus(String publishStatus) { this.publishStatus = publishStatus; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
  }

  public static class TeamProductUpsertRequest {
    @NotBlank private String teamName;
    private String teamType;
    private String teamIntro;
    @NotBlank private String serviceCountryScope;
    @NotBlank private String serviceMajorScope;
    @NotNull @DecimalMin(value = "0", inclusive = true) private BigDecimal priceMin;
    @NotNull @DecimalMin(value = "0", inclusive = true) private BigDecimal priceMax;
    private List<@NotNull Long> publisherMemberIds;

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getTeamType() { return teamType; }
    public void setTeamType(String teamType) { this.teamType = teamType; }
    public String getTeamIntro() { return teamIntro; }
    public void setTeamIntro(String teamIntro) { this.teamIntro = teamIntro; }
    public String getServiceCountryScope() { return serviceCountryScope; }
    public void setServiceCountryScope(String serviceCountryScope) { this.serviceCountryScope = serviceCountryScope; }
    public String getServiceMajorScope() { return serviceMajorScope; }
    public void setServiceMajorScope(String serviceMajorScope) { this.serviceMajorScope = serviceMajorScope; }
    public BigDecimal getPriceMin() { return priceMin; }
    public void setPriceMin(BigDecimal priceMin) { this.priceMin = priceMin; }
    public BigDecimal getPriceMax() { return priceMax; }
    public void setPriceMax(BigDecimal priceMax) { this.priceMax = priceMax; }
    public List<Long> getPublisherMemberIds() { return publisherMemberIds; }
    public void setPublisherMemberIds(List<Long> publisherMemberIds) { this.publisherMemberIds = publisherMemberIds; }
  }

  public static class TeamProductSummaryItem {
    private Long teamId;
    private String teamName;
    private String teamType;
    private String publishStatus;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private String updatedAt;

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getTeamType() { return teamType; }
    public void setTeamType(String teamType) { this.teamType = teamType; }
    public String getPublishStatus() { return publishStatus; }
    public void setPublishStatus(String publishStatus) { this.publishStatus = publishStatus; }
    public BigDecimal getPriceMin() { return priceMin; }
    public void setPriceMin(BigDecimal priceMin) { this.priceMin = priceMin; }
    public BigDecimal getPriceMax() { return priceMax; }
    public void setPriceMax(BigDecimal priceMax) { this.priceMax = priceMax; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
  }

  public static class TeamProductOrgMemberItem {
    private Long memberId;
    private String displayName;
    private String avatarUrl;
    private String jobTitle;
    private String roleCode;
    private String verifiedBadgeStatus;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public String getVerifiedBadgeStatus() { return verifiedBadgeStatus; }
    public void setVerifiedBadgeStatus(String verifiedBadgeStatus) { this.verifiedBadgeStatus = verifiedBadgeStatus; }
  }

  public static class TeamProductPublisherItem {
    private Long memberId;
    private String displayName;
    private String avatarUrl;
    private String jobTitle;
    private String roleCode;
    private String bio;
    private Integer yearsOfExperience;
    private String educationLevel;
    private String specialCountries;
    private String specialDirections;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }
    public String getSpecialCountries() { return specialCountries; }
    public void setSpecialCountries(String specialCountries) { this.specialCountries = specialCountries; }
    public String getSpecialDirections() { return specialDirections; }
    public void setSpecialDirections(String specialDirections) { this.specialDirections = specialDirections; }
  }

  public static class TeamProductDetailView {
    private Long teamId;
    private String teamName;
    private String teamType;
    private String teamIntro;
    private String serviceCountryScope;
    private String serviceMajorScope;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private String publishStatus;
    private String updatedAt;
    private List<TeamProductPublisherItem> publisherMembers;

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getTeamType() { return teamType; }
    public void setTeamType(String teamType) { this.teamType = teamType; }
    public String getTeamIntro() { return teamIntro; }
    public void setTeamIntro(String teamIntro) { this.teamIntro = teamIntro; }
    public String getServiceCountryScope() { return serviceCountryScope; }
    public void setServiceCountryScope(String serviceCountryScope) { this.serviceCountryScope = serviceCountryScope; }
    public String getServiceMajorScope() { return serviceMajorScope; }
    public void setServiceMajorScope(String serviceMajorScope) { this.serviceMajorScope = serviceMajorScope; }
    public BigDecimal getPriceMin() { return priceMin; }
    public void setPriceMin(BigDecimal priceMin) { this.priceMin = priceMin; }
    public BigDecimal getPriceMax() { return priceMax; }
    public void setPriceMax(BigDecimal priceMax) { this.priceMax = priceMax; }
    public String getPublishStatus() { return publishStatus; }
    public void setPublishStatus(String publishStatus) { this.publishStatus = publishStatus; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public List<TeamProductPublisherItem> getPublisherMembers() { return publisherMembers; }
    public void setPublisherMembers(List<TeamProductPublisherItem> publisherMembers) { this.publisherMembers = publisherMembers; }
  }

  public static class InvitationCreateRequest {
    @NotNull private Long teamId;
    @NotBlank @Email private String email;
    private String inviteeName;
    private String roleHint;

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getInviteeName() { return inviteeName; }
    public void setInviteeName(String inviteeName) { this.inviteeName = inviteeName; }
    public String getRoleHint() { return roleHint; }
    public void setRoleHint(String roleHint) { this.roleHint = roleHint; }
  }

  public static class InvitationView {
    private Long id;
    private String email;
    private String status;
    private String token;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
  }

  public static class MemberProfileUpdateRequest {
    @NotBlank private String displayName;
    private String wechatId;
    @NotBlank private String jobTitle;
    @NotBlank private String educationLevel;
    @NotBlank private String graduatedSchool;
    private String major;
    @NotNull @Min(0) @Max(60) private Integer yearsOfExperience;
    @NotBlank private String specialCountries;
    @NotBlank private String specialDirections;
    @NotBlank private String bio;
    private String serviceStyleTags;
    @NotBlank private String publicStatus;

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
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
  }

  public static class MemberSelfProfileView {
    private Long memberId;
    private Long orgId;
    private String displayName;
    private String avatarUrl;
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
    private String profileAuditStatus;
    private List<String> roleCodes;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
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
    public String getProfileAuditStatus() { return profileAuditStatus; }
    public void setProfileAuditStatus(String profileAuditStatus) { this.profileAuditStatus = profileAuditStatus; }
    public List<String> getRoleCodes() { return roleCodes; }
    public void setRoleCodes(List<String> roleCodes) { this.roleCodes = roleCodes; }
  }

  public static class MemberAvatarUpdateRequest {
    @NotBlank private String avatarUrl;

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
  }

  public static class MemberWechatUpdateRequest {
    @NotBlank private String wechatId;

    public String getWechatId() { return wechatId; }
    public void setWechatId(String wechatId) { this.wechatId = wechatId; }
  }

  public static class MemberVerificationSubmitRequest {
    @NotBlank private String idCardImageUrl;
    @NotBlank private String employmentProofImageUrl;
    @NotBlank private String educationProofImageUrl;

    public String getIdCardImageUrl() { return idCardImageUrl; }
    public void setIdCardImageUrl(String idCardImageUrl) { this.idCardImageUrl = idCardImageUrl; }
    public String getEmploymentProofImageUrl() { return employmentProofImageUrl; }
    public void setEmploymentProofImageUrl(String employmentProofImageUrl) { this.employmentProofImageUrl = employmentProofImageUrl; }
    public String getEducationProofImageUrl() { return educationProofImageUrl; }
    public void setEducationProofImageUrl(String educationProofImageUrl) { this.educationProofImageUrl = educationProofImageUrl; }
  }

  public static class MemberVerificationStatusView {
    private String status;
    private String rejectReason;
    private String payloadJson;
    private String submittedAt;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
  }

  public static class RoleItem {
    @NotBlank private String roleCode;
    @NotNull private Boolean isPrimary;

    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
  }

  public static class MemberRolesUpdateRequest {
    @NotEmpty @Valid private List<RoleItem> roles;

    public List<RoleItem> getRoles() { return roles; }
    public void setRoles(List<RoleItem> roles) { this.roles = roles; }
  }

  public static class MemberMetricsUpdateRequest {
    @NotNull @Min(0) private Integer caseCount;
    @NotNull @Min(0) private BigDecimal successRate;
    @NotNull @Min(0) private BigDecimal avgRating;
    @NotNull @Min(0) private BigDecimal responseEfficiencyScore;
    private String serviceTags;
    private String budgetTags;

    public Integer getCaseCount() { return caseCount; }
    public void setCaseCount(Integer caseCount) { this.caseCount = caseCount; }
    public BigDecimal getSuccessRate() { return successRate; }
    public void setSuccessRate(BigDecimal successRate) { this.successRate = successRate; }
    public BigDecimal getAvgRating() { return avgRating; }
    public void setAvgRating(BigDecimal avgRating) { this.avgRating = avgRating; }
    public BigDecimal getResponseEfficiencyScore() { return responseEfficiencyScore; }
    public void setResponseEfficiencyScore(BigDecimal responseEfficiencyScore) { this.responseEfficiencyScore = responseEfficiencyScore; }
    public String getServiceTags() { return serviceTags; }
    public void setServiceTags(String serviceTags) { this.serviceTags = serviceTags; }
    public String getBudgetTags() { return budgetTags; }
    public void setBudgetTags(String budgetTags) { this.budgetTags = budgetTags; }
  }

  public static class DiscoveryMemberItem {
    private Long memberId;
    private String displayName;
    private String jobTitle;
    private String primaryRole;
    private String orgName;
    private String teamName;
    private String city;
    private String bio;
    private String specialCountries;
    private String specialDirections;
    private Integer yearsOfExperience;
    private String educationLevel;
    private Integer caseCount;
    private BigDecimal successRate;
    private BigDecimal avgRating;
    private BigDecimal responseEfficiencyScore;
    private String serviceTags;
    private String budgetTags;
    private String verifiedBadgeStatus;
    private String logoUrl;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getPrimaryRole() { return primaryRole; }
    public void setPrimaryRole(String primaryRole) { this.primaryRole = primaryRole; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getSpecialCountries() { return specialCountries; }
    public void setSpecialCountries(String specialCountries) { this.specialCountries = specialCountries; }
    public String getSpecialDirections() { return specialDirections; }
    public void setSpecialDirections(String specialDirections) { this.specialDirections = specialDirections; }
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }
    public Integer getCaseCount() { return caseCount; }
    public void setCaseCount(Integer caseCount) { this.caseCount = caseCount; }
    public BigDecimal getSuccessRate() { return successRate; }
    public void setSuccessRate(BigDecimal successRate) { this.successRate = successRate; }
    public BigDecimal getAvgRating() { return avgRating; }
    public void setAvgRating(BigDecimal avgRating) { this.avgRating = avgRating; }
    public BigDecimal getResponseEfficiencyScore() { return responseEfficiencyScore; }
    public void setResponseEfficiencyScore(BigDecimal responseEfficiencyScore) { this.responseEfficiencyScore = responseEfficiencyScore; }
    public String getServiceTags() { return serviceTags; }
    public void setServiceTags(String serviceTags) { this.serviceTags = serviceTags; }
    public String getBudgetTags() { return budgetTags; }
    public void setBudgetTags(String budgetTags) { this.budgetTags = budgetTags; }
    public String getVerifiedBadgeStatus() { return verifiedBadgeStatus; }
    public void setVerifiedBadgeStatus(String verifiedBadgeStatus) { this.verifiedBadgeStatus = verifiedBadgeStatus; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
  }

  public static class DiscoveryMemberDetail {
    private DiscoveryMemberItem member;
    private OrgProfileView org;
    private List<String> roleCodes;

    public DiscoveryMemberItem getMember() { return member; }
    public void setMember(DiscoveryMemberItem member) { this.member = member; }
    public OrgProfileView getOrg() { return org; }
    public void setOrg(OrgProfileView org) { this.org = org; }
    public List<String> getRoleCodes() { return roleCodes; }
    public void setRoleCodes(List<String> roleCodes) { this.roleCodes = roleCodes; }
  }

  public static class DiscoveryTeamItem {
    private Long teamId;
    private String teamName;
    private String teamIntro;
    private String orgName;
    private String city;
    private String logoUrl;
    private String serviceCountryScope;
    private String serviceMajorScope;
    private Integer caseCount;
    private BigDecimal successRate;
    private BigDecimal avgRating;
    private BigDecimal responseEfficiencyScore;
    private String priceTextPlaceholder;
    private Boolean favorited;

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getTeamIntro() { return teamIntro; }
    public void setTeamIntro(String teamIntro) { this.teamIntro = teamIntro; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getServiceCountryScope() { return serviceCountryScope; }
    public void setServiceCountryScope(String serviceCountryScope) { this.serviceCountryScope = serviceCountryScope; }
    public String getServiceMajorScope() { return serviceMajorScope; }
    public void setServiceMajorScope(String serviceMajorScope) { this.serviceMajorScope = serviceMajorScope; }
    public Integer getCaseCount() { return caseCount; }
    public void setCaseCount(Integer caseCount) { this.caseCount = caseCount; }
    public BigDecimal getSuccessRate() { return successRate; }
    public void setSuccessRate(BigDecimal successRate) { this.successRate = successRate; }
    public BigDecimal getAvgRating() { return avgRating; }
    public void setAvgRating(BigDecimal avgRating) { this.avgRating = avgRating; }
    public BigDecimal getResponseEfficiencyScore() { return responseEfficiencyScore; }
    public void setResponseEfficiencyScore(BigDecimal responseEfficiencyScore) { this.responseEfficiencyScore = responseEfficiencyScore; }
    public String getPriceTextPlaceholder() { return priceTextPlaceholder; }
    public void setPriceTextPlaceholder(String priceTextPlaceholder) { this.priceTextPlaceholder = priceTextPlaceholder; }
    public Boolean getFavorited() { return favorited; }
    public void setFavorited(Boolean favorited) { this.favorited = favorited; }
  }

  public static class DiscoveryTeamMemberItem {
    private Long memberId;
    private String displayName;
    private String jobTitle;
    private String roleCode;
    private String bio;
    private String specialCountries;
    private String specialDirections;
    private Integer yearsOfExperience;
    private String educationLevel;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getSpecialCountries() { return specialCountries; }
    public void setSpecialCountries(String specialCountries) { this.specialCountries = specialCountries; }
    public String getSpecialDirections() { return specialDirections; }
    public void setSpecialDirections(String specialDirections) { this.specialDirections = specialDirections; }
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }
  }

  public static class DiscoveryTeamDetail {
    private Long teamId;
    private String teamName;
    private String teamType;
    private String teamIntro;
    private String orgName;
    private String city;
    private String serviceCountryScope;
    private String serviceMajorScope;
    private Integer caseCount;
    private BigDecimal successRate;
    private BigDecimal avgRating;
    private BigDecimal responseEfficiencyScore;
    private String priceTextPlaceholder;
    private String packagePlaceholderTitle;
    private String packagePlaceholderDesc;
    private OrgProfileView org;
    private List<DiscoveryTeamMemberItem> members;
    private Boolean favorited;

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getTeamType() { return teamType; }
    public void setTeamType(String teamType) { this.teamType = teamType; }
    public String getTeamIntro() { return teamIntro; }
    public void setTeamIntro(String teamIntro) { this.teamIntro = teamIntro; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getServiceCountryScope() { return serviceCountryScope; }
    public void setServiceCountryScope(String serviceCountryScope) { this.serviceCountryScope = serviceCountryScope; }
    public String getServiceMajorScope() { return serviceMajorScope; }
    public void setServiceMajorScope(String serviceMajorScope) { this.serviceMajorScope = serviceMajorScope; }
    public Integer getCaseCount() { return caseCount; }
    public void setCaseCount(Integer caseCount) { this.caseCount = caseCount; }
    public BigDecimal getSuccessRate() { return successRate; }
    public void setSuccessRate(BigDecimal successRate) { this.successRate = successRate; }
    public BigDecimal getAvgRating() { return avgRating; }
    public void setAvgRating(BigDecimal avgRating) { this.avgRating = avgRating; }
    public BigDecimal getResponseEfficiencyScore() { return responseEfficiencyScore; }
    public void setResponseEfficiencyScore(BigDecimal responseEfficiencyScore) { this.responseEfficiencyScore = responseEfficiencyScore; }
    public String getPriceTextPlaceholder() { return priceTextPlaceholder; }
    public void setPriceTextPlaceholder(String priceTextPlaceholder) { this.priceTextPlaceholder = priceTextPlaceholder; }
    public String getPackagePlaceholderTitle() { return packagePlaceholderTitle; }
    public void setPackagePlaceholderTitle(String packagePlaceholderTitle) { this.packagePlaceholderTitle = packagePlaceholderTitle; }
    public String getPackagePlaceholderDesc() { return packagePlaceholderDesc; }
    public void setPackagePlaceholderDesc(String packagePlaceholderDesc) { this.packagePlaceholderDesc = packagePlaceholderDesc; }
    public OrgProfileView getOrg() { return org; }
    public void setOrg(OrgProfileView org) { this.org = org; }
    public List<DiscoveryTeamMemberItem> getMembers() { return members; }
    public void setMembers(List<DiscoveryTeamMemberItem> members) { this.members = members; }
    public Boolean getFavorited() { return favorited; }
    public void setFavorited(Boolean favorited) { this.favorited = favorited; }
  }

  public static class OrgVerificationSubmitRequest {
    @NotBlank private String licenseNo;
    @NotBlank private String legalPersonName;
    @NotBlank private String licenseImageUrl;
    @NotBlank private String legalPersonIdImageUrl;
    @NotBlank private String corporateAccountName;
    @NotBlank private String corporateBankName;
    @NotBlank private String corporateBankAccountNo;
    private String corporateAccountProofImageUrl;
    private String officeEnvironmentImageUrls;
    @NotBlank private String adminRealNameImageUrl;
    @NotBlank private String adminEmploymentProofImageUrl;
    private String remark;

    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }
    public String getLegalPersonName() { return legalPersonName; }
    public void setLegalPersonName(String legalPersonName) { this.legalPersonName = legalPersonName; }
    public String getLicenseImageUrl() { return licenseImageUrl; }
    public void setLicenseImageUrl(String licenseImageUrl) { this.licenseImageUrl = licenseImageUrl; }
    public String getLegalPersonIdImageUrl() { return legalPersonIdImageUrl; }
    public void setLegalPersonIdImageUrl(String legalPersonIdImageUrl) { this.legalPersonIdImageUrl = legalPersonIdImageUrl; }
    public String getCorporateAccountName() { return corporateAccountName; }
    public void setCorporateAccountName(String corporateAccountName) { this.corporateAccountName = corporateAccountName; }
    public String getCorporateBankName() { return corporateBankName; }
    public void setCorporateBankName(String corporateBankName) { this.corporateBankName = corporateBankName; }
    public String getCorporateBankAccountNo() { return corporateBankAccountNo; }
    public void setCorporateBankAccountNo(String corporateBankAccountNo) { this.corporateBankAccountNo = corporateBankAccountNo; }
    public String getCorporateAccountProofImageUrl() { return corporateAccountProofImageUrl; }
    public void setCorporateAccountProofImageUrl(String corporateAccountProofImageUrl) { this.corporateAccountProofImageUrl = corporateAccountProofImageUrl; }
    public String getOfficeEnvironmentImageUrls() { return officeEnvironmentImageUrls; }
    public void setOfficeEnvironmentImageUrls(String officeEnvironmentImageUrls) { this.officeEnvironmentImageUrls = officeEnvironmentImageUrls; }
    public String getAdminRealNameImageUrl() { return adminRealNameImageUrl; }
    public void setAdminRealNameImageUrl(String adminRealNameImageUrl) { this.adminRealNameImageUrl = adminRealNameImageUrl; }
    public String getAdminEmploymentProofImageUrl() { return adminEmploymentProofImageUrl; }
    public void setAdminEmploymentProofImageUrl(String adminEmploymentProofImageUrl) { this.adminEmploymentProofImageUrl = adminEmploymentProofImageUrl; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
  }

  public static class OrgVerificationView {
    private String verificationStatus;
    private String recordStatus;
    private String payloadJson;
    private String rejectReason;
    private String submittedAt;

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    public String getRecordStatus() { return recordStatus; }
    public void setRecordStatus(String recordStatus) { this.recordStatus = recordStatus; }
    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
  }

  public static class MemberCreateRequest {
    @Pattern(regexp = "^1\\d{10}$")
    private String phone;
    @NotBlank private String displayName;
    @NotBlank private String jobTitle;
    private List<@NotBlank String> permissions;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
  }

  public static class MemberAdminItem {
    private Long memberId;
    private Long userId;
    private String phone;
    private String displayName;
    private String jobTitle;
    private String educationLevel;
    private String graduatedSchool;
    private Integer yearsOfExperience;
    private String publicStatus;
    private String verifiedBadgeStatus;
    private String profileAuditStatus;
    private String memberStatus;
    private String accountStatus;
    private List<String> roleCodes;
    private List<String> permissions;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }
    public String getGraduatedSchool() { return graduatedSchool; }
    public void setGraduatedSchool(String graduatedSchool) { this.graduatedSchool = graduatedSchool; }
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public String getPublicStatus() { return publicStatus; }
    public void setPublicStatus(String publicStatus) { this.publicStatus = publicStatus; }
    public String getVerifiedBadgeStatus() { return verifiedBadgeStatus; }
    public void setVerifiedBadgeStatus(String verifiedBadgeStatus) { this.verifiedBadgeStatus = verifiedBadgeStatus; }
    public String getProfileAuditStatus() { return profileAuditStatus; }
    public void setProfileAuditStatus(String profileAuditStatus) { this.profileAuditStatus = profileAuditStatus; }
    public String getMemberStatus() { return memberStatus; }
    public void setMemberStatus(String memberStatus) { this.memberStatus = memberStatus; }
    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }
    public List<String> getRoleCodes() { return roleCodes; }
    public void setRoleCodes(List<String> roleCodes) { this.roleCodes = roleCodes; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
  }

  public static class MemberStatusUpdateRequest {
    @Pattern(regexp = "^(ACTIVE|DISABLED)$")
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
  }

  public static class MemberPermissionsUpdateRequest {
    @NotNull private List<@NotBlank String> permissions;

    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
  }

  public static class PagedResult<T> {
    private List<T> records;
    private long total;
    private int page;
    private int pageSize;

    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
  }

  public static class AgentStudentSearchItem {
    private Long studentUserId;
    private String displayName;
    private String educationLevel;
    private String schoolName;
    private String major;
    private BigDecimal gpaValue;
    private String gpaScale;
    private String languageSummary;
    private String targetCountries;
    private String targetMajorText;
    private String intakeTerm;

    public Long getStudentUserId() { return studentUserId; }
    public void setStudentUserId(Long studentUserId) { this.studentUserId = studentUserId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public BigDecimal getGpaValue() { return gpaValue; }
    public void setGpaValue(BigDecimal gpaValue) { this.gpaValue = gpaValue; }
    public String getGpaScale() { return gpaScale; }
    public void setGpaScale(String gpaScale) { this.gpaScale = gpaScale; }
    public String getLanguageSummary() { return languageSummary; }
    public void setLanguageSummary(String languageSummary) { this.languageSummary = languageSummary; }
    public String getTargetCountries() { return targetCountries; }
    public void setTargetCountries(String targetCountries) { this.targetCountries = targetCountries; }
    public String getTargetMajorText() { return targetMajorText; }
    public void setTargetMajorText(String targetMajorText) { this.targetMajorText = targetMajorText; }
    public String getIntakeTerm() { return intakeTerm; }
    public void setIntakeTerm(String intakeTerm) { this.intakeTerm = intakeTerm; }
  }

  public static class MemberWorkbenchAccessView {
    private String orgVerificationStatus;
    private String memberVerificationStatus;
    private List<String> permissions;
    private boolean canChatStudent;
    private boolean canPublishPackage;
    private boolean canDoCoreActions;
    private String blockedReason;

    public String getOrgVerificationStatus() { return orgVerificationStatus; }
    public void setOrgVerificationStatus(String orgVerificationStatus) { this.orgVerificationStatus = orgVerificationStatus; }
    public String getMemberVerificationStatus() { return memberVerificationStatus; }
    public void setMemberVerificationStatus(String memberVerificationStatus) { this.memberVerificationStatus = memberVerificationStatus; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    public boolean isCanChatStudent() { return canChatStudent; }
    public void setCanChatStudent(boolean canChatStudent) { this.canChatStudent = canChatStudent; }
    public boolean isCanPublishPackage() { return canPublishPackage; }
    public void setCanPublishPackage(boolean canPublishPackage) { this.canPublishPackage = canPublishPackage; }
    public boolean isCanDoCoreActions() { return canDoCoreActions; }
    public void setCanDoCoreActions(boolean canDoCoreActions) { this.canDoCoreActions = canDoCoreActions; }
    public String getBlockedReason() { return blockedReason; }
    public void setBlockedReason(String blockedReason) { this.blockedReason = blockedReason; }
  }
}
