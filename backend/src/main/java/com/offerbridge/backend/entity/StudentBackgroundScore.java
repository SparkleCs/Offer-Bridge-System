package com.offerbridge.backend.entity;

import java.math.BigDecimal;

public class StudentBackgroundScore {
  private Long id;
  private Long userId;
  private BigDecimal gpaScore;
  private BigDecimal languageScore;
  private BigDecimal publicationScore;
  private BigDecimal researchScore;
  private BigDecimal internshipScore;
  private BigDecimal exchangeScore;
  private BigDecimal competitionScore;
  private BigDecimal undergraduateSchoolScore;
  private BigDecimal overallAcademicScore;
  private String scoreVersion;
  private String scoreDetailJson;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public BigDecimal getGpaScore() { return gpaScore; }
  public void setGpaScore(BigDecimal gpaScore) { this.gpaScore = gpaScore; }
  public BigDecimal getLanguageScore() { return languageScore; }
  public void setLanguageScore(BigDecimal languageScore) { this.languageScore = languageScore; }
  public BigDecimal getPublicationScore() { return publicationScore; }
  public void setPublicationScore(BigDecimal publicationScore) { this.publicationScore = publicationScore; }
  public BigDecimal getResearchScore() { return researchScore; }
  public void setResearchScore(BigDecimal researchScore) { this.researchScore = researchScore; }
  public BigDecimal getInternshipScore() { return internshipScore; }
  public void setInternshipScore(BigDecimal internshipScore) { this.internshipScore = internshipScore; }
  public BigDecimal getExchangeScore() { return exchangeScore; }
  public void setExchangeScore(BigDecimal exchangeScore) { this.exchangeScore = exchangeScore; }
  public BigDecimal getCompetitionScore() { return competitionScore; }
  public void setCompetitionScore(BigDecimal competitionScore) { this.competitionScore = competitionScore; }
  public BigDecimal getUndergraduateSchoolScore() { return undergraduateSchoolScore; }
  public void setUndergraduateSchoolScore(BigDecimal undergraduateSchoolScore) { this.undergraduateSchoolScore = undergraduateSchoolScore; }
  public BigDecimal getOverallAcademicScore() { return overallAcademicScore; }
  public void setOverallAcademicScore(BigDecimal overallAcademicScore) { this.overallAcademicScore = overallAcademicScore; }
  public String getScoreVersion() { return scoreVersion; }
  public void setScoreVersion(String scoreVersion) { this.scoreVersion = scoreVersion; }
  public String getScoreDetailJson() { return scoreDetailJson; }
  public void setScoreDetailJson(String scoreDetailJson) { this.scoreDetailJson = scoreDetailJson; }
}
