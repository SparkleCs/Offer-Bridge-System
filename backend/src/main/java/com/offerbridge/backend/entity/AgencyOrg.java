package com.offerbridge.backend.entity;

public class AgencyOrg {
  private Long id;
  private Long adminUserId;
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
  public Long getAdminUserId() { return adminUserId; }
  public void setAdminUserId(Long adminUserId) { this.adminUserId = adminUserId; }
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
