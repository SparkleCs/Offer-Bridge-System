package com.offerbridge.backend.entity;

import java.math.BigDecimal;

public class Program {
  private Long id;
  private Long schoolId;
  private String programName;
  private String collegeName;
  private String degreeType;
  private String subjectCategoryCode;
  private String subjectCategoryName;
  private String directionCode;
  private String directionName;
  private String studyMode;
  private Integer durationMonths;
  private BigDecimal tuitionAmount;
  private String tuitionCurrency;
  private String languageType;
  private BigDecimal languageMinScore;
  private BigDecimal gpaMinRecommend;
  private Boolean requiresGre;
  private Boolean requiresGmat;
  private String backgroundPreference;
  private String applicationRoundsOverview;
  private String suitableTags;
  private String intakeTerm;
  private String programSummary;

  private String schoolNameCn;
  private String schoolNameEn;
  private String countryCode;
  private String countryName;
  private Integer qsRank;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getSchoolId() { return schoolId; }
  public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
  public String getProgramName() { return programName; }
  public void setProgramName(String programName) { this.programName = programName; }
  public String getCollegeName() { return collegeName; }
  public void setCollegeName(String collegeName) { this.collegeName = collegeName; }
  public String getDegreeType() { return degreeType; }
  public void setDegreeType(String degreeType) { this.degreeType = degreeType; }
  public String getSubjectCategoryCode() { return subjectCategoryCode; }
  public void setSubjectCategoryCode(String subjectCategoryCode) { this.subjectCategoryCode = subjectCategoryCode; }
  public String getSubjectCategoryName() { return subjectCategoryName; }
  public void setSubjectCategoryName(String subjectCategoryName) { this.subjectCategoryName = subjectCategoryName; }
  public String getDirectionCode() { return directionCode; }
  public void setDirectionCode(String directionCode) { this.directionCode = directionCode; }
  public String getDirectionName() { return directionName; }
  public void setDirectionName(String directionName) { this.directionName = directionName; }
  public String getStudyMode() { return studyMode; }
  public void setStudyMode(String studyMode) { this.studyMode = studyMode; }
  public Integer getDurationMonths() { return durationMonths; }
  public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }
  public BigDecimal getTuitionAmount() { return tuitionAmount; }
  public void setTuitionAmount(BigDecimal tuitionAmount) { this.tuitionAmount = tuitionAmount; }
  public String getTuitionCurrency() { return tuitionCurrency; }
  public void setTuitionCurrency(String tuitionCurrency) { this.tuitionCurrency = tuitionCurrency; }
  public String getLanguageType() { return languageType; }
  public void setLanguageType(String languageType) { this.languageType = languageType; }
  public BigDecimal getLanguageMinScore() { return languageMinScore; }
  public void setLanguageMinScore(BigDecimal languageMinScore) { this.languageMinScore = languageMinScore; }
  public BigDecimal getGpaMinRecommend() { return gpaMinRecommend; }
  public void setGpaMinRecommend(BigDecimal gpaMinRecommend) { this.gpaMinRecommend = gpaMinRecommend; }
  public Boolean getRequiresGre() { return requiresGre; }
  public void setRequiresGre(Boolean requiresGre) { this.requiresGre = requiresGre; }
  public Boolean getRequiresGmat() { return requiresGmat; }
  public void setRequiresGmat(Boolean requiresGmat) { this.requiresGmat = requiresGmat; }
  public String getBackgroundPreference() { return backgroundPreference; }
  public void setBackgroundPreference(String backgroundPreference) { this.backgroundPreference = backgroundPreference; }
  public String getApplicationRoundsOverview() { return applicationRoundsOverview; }
  public void setApplicationRoundsOverview(String applicationRoundsOverview) { this.applicationRoundsOverview = applicationRoundsOverview; }
  public String getSuitableTags() { return suitableTags; }
  public void setSuitableTags(String suitableTags) { this.suitableTags = suitableTags; }
  public String getIntakeTerm() { return intakeTerm; }
  public void setIntakeTerm(String intakeTerm) { this.intakeTerm = intakeTerm; }
  public String getProgramSummary() { return programSummary; }
  public void setProgramSummary(String programSummary) { this.programSummary = programSummary; }
  public String getSchoolNameCn() { return schoolNameCn; }
  public void setSchoolNameCn(String schoolNameCn) { this.schoolNameCn = schoolNameCn; }
  public String getSchoolNameEn() { return schoolNameEn; }
  public void setSchoolNameEn(String schoolNameEn) { this.schoolNameEn = schoolNameEn; }
  public String getCountryCode() { return countryCode; }
  public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
  public String getCountryName() { return countryName; }
  public void setCountryName(String countryName) { this.countryName = countryName; }
  public Integer getQsRank() { return qsRank; }
  public void setQsRank(Integer qsRank) { this.qsRank = qsRank; }
}
