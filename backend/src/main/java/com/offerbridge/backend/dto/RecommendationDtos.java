package com.offerbridge.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class RecommendationDtos {
  public static class StudentAgencyTeamRecommendationItem {
    private Long teamId;
    private String teamName;
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
    private Integer recommendScore;
    private String matchLevel;
    private List<String> matchReasons;
    private List<String> matchedTags;
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
    public Integer getRecommendScore() { return recommendScore; }
    public void setRecommendScore(Integer recommendScore) { this.recommendScore = recommendScore; }
    public String getMatchLevel() { return matchLevel; }
    public void setMatchLevel(String matchLevel) { this.matchLevel = matchLevel; }
    public List<String> getMatchReasons() { return matchReasons; }
    public void setMatchReasons(List<String> matchReasons) { this.matchReasons = matchReasons; }
    public List<String> getMatchedTags() { return matchedTags; }
    public void setMatchedTags(List<String> matchedTags) { this.matchedTags = matchedTags; }
    public Boolean getFavorited() { return favorited; }
    public void setFavorited(Boolean favorited) { this.favorited = favorited; }
  }

  public static class StudentAgencyTeamRecommendationCandidate extends StudentAgencyTeamRecommendationItem {
    private BigDecimal priceMin;
    private BigDecimal priceMax;

    public BigDecimal getPriceMin() { return priceMin; }
    public void setPriceMin(BigDecimal priceMin) { this.priceMin = priceMin; }
    public BigDecimal getPriceMax() { return priceMax; }
    public void setPriceMax(BigDecimal priceMax) { this.priceMax = priceMax; }
  }

  public static class AgencyTeamStudentRecommendationItem {
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
    private Integer recommendScore;
    private String matchLevel;
    private List<String> matchReasons;
    private List<String> matchedTags;

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
    public Integer getRecommendScore() { return recommendScore; }
    public void setRecommendScore(Integer recommendScore) { this.recommendScore = recommendScore; }
    public String getMatchLevel() { return matchLevel; }
    public void setMatchLevel(String matchLevel) { this.matchLevel = matchLevel; }
    public List<String> getMatchReasons() { return matchReasons; }
    public void setMatchReasons(List<String> matchReasons) { this.matchReasons = matchReasons; }
    public List<String> getMatchedTags() { return matchedTags; }
    public void setMatchedTags(List<String> matchedTags) { this.matchedTags = matchedTags; }
  }

  public static class AgencyTeamStudentRecommendationCandidate extends AgencyTeamStudentRecommendationItem {
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;

    public BigDecimal getBudgetMin() { return budgetMin; }
    public void setBudgetMin(BigDecimal budgetMin) { this.budgetMin = budgetMin; }
    public BigDecimal getBudgetMax() { return budgetMax; }
    public void setBudgetMax(BigDecimal budgetMax) { this.budgetMax = budgetMax; }
  }
}
