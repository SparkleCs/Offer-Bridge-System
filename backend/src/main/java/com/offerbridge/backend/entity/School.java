package com.offerbridge.backend.entity;

import java.math.BigDecimal;

public class School {
  private Long id;
  private String schoolNameCn;
  private String schoolNameEn;
  private String countryCode;
  private String countryName;
  private String cityName;
  private Integer qsRank;
  private Integer rankingYear;
  private Integer usnewsRank;
  private Integer usnewsRankingYear;
  private String schoolSummary;
  private BigDecimal tuitionMin;
  private BigDecimal tuitionMax;
  private String tuitionCurrency;
  private Integer durationMinMonths;
  private Integer durationMaxMonths;
  private String languageRequirementRange;
  private String advantageSubjects;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getSchoolNameCn() { return schoolNameCn; }
  public void setSchoolNameCn(String schoolNameCn) { this.schoolNameCn = schoolNameCn; }
  public String getSchoolNameEn() { return schoolNameEn; }
  public void setSchoolNameEn(String schoolNameEn) { this.schoolNameEn = schoolNameEn; }
  public String getCountryCode() { return countryCode; }
  public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
  public String getCountryName() { return countryName; }
  public void setCountryName(String countryName) { this.countryName = countryName; }
  public String getCityName() { return cityName; }
  public void setCityName(String cityName) { this.cityName = cityName; }
  public Integer getQsRank() { return qsRank; }
  public void setQsRank(Integer qsRank) { this.qsRank = qsRank; }
  public Integer getRankingYear() { return rankingYear; }
  public void setRankingYear(Integer rankingYear) { this.rankingYear = rankingYear; }
  public Integer getUsnewsRank() { return usnewsRank; }
  public void setUsnewsRank(Integer usnewsRank) { this.usnewsRank = usnewsRank; }
  public Integer getUsnewsRankingYear() { return usnewsRankingYear; }
  public void setUsnewsRankingYear(Integer usnewsRankingYear) { this.usnewsRankingYear = usnewsRankingYear; }
  public String getSchoolSummary() { return schoolSummary; }
  public void setSchoolSummary(String schoolSummary) { this.schoolSummary = schoolSummary; }
  public BigDecimal getTuitionMin() { return tuitionMin; }
  public void setTuitionMin(BigDecimal tuitionMin) { this.tuitionMin = tuitionMin; }
  public BigDecimal getTuitionMax() { return tuitionMax; }
  public void setTuitionMax(BigDecimal tuitionMax) { this.tuitionMax = tuitionMax; }
  public String getTuitionCurrency() { return tuitionCurrency; }
  public void setTuitionCurrency(String tuitionCurrency) { this.tuitionCurrency = tuitionCurrency; }
  public Integer getDurationMinMonths() { return durationMinMonths; }
  public void setDurationMinMonths(Integer durationMinMonths) { this.durationMinMonths = durationMinMonths; }
  public Integer getDurationMaxMonths() { return durationMaxMonths; }
  public void setDurationMaxMonths(Integer durationMaxMonths) { this.durationMaxMonths = durationMaxMonths; }
  public String getLanguageRequirementRange() { return languageRequirementRange; }
  public void setLanguageRequirementRange(String languageRequirementRange) { this.languageRequirementRange = languageRequirementRange; }
  public String getAdvantageSubjects() { return advantageSubjects; }
  public void setAdvantageSubjects(String advantageSubjects) { this.advantageSubjects = advantageSubjects; }
}
