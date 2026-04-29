package com.offerbridge.backend.entity;

import java.math.BigDecimal;

public class StudentProfile {
  private Long id;
  private Long userId;
  private String name;
  private String email;
  private String wechatId;
  private String educationLevel;
  private String schoolName;
  private String major;
  private BigDecimal gpaValue;
  private String gpaScale;
  private Integer rankValue;
  private String targetMajorText;
  private String intakeTerm;
  private String budgetCurrency;
  private BigDecimal budgetMin;
  private BigDecimal budgetMax;
  private String budgetNote;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getWechatId() { return wechatId; }
  public void setWechatId(String wechatId) { this.wechatId = wechatId; }
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
  public Integer getRankValue() { return rankValue; }
  public void setRankValue(Integer rankValue) { this.rankValue = rankValue; }
  public String getTargetMajorText() { return targetMajorText; }
  public void setTargetMajorText(String targetMajorText) { this.targetMajorText = targetMajorText; }
  public String getIntakeTerm() { return intakeTerm; }
  public void setIntakeTerm(String intakeTerm) { this.intakeTerm = intakeTerm; }
  public String getBudgetCurrency() { return budgetCurrency; }
  public void setBudgetCurrency(String budgetCurrency) { this.budgetCurrency = budgetCurrency; }
  public BigDecimal getBudgetMin() { return budgetMin; }
  public void setBudgetMin(BigDecimal budgetMin) { this.budgetMin = budgetMin; }
  public BigDecimal getBudgetMax() { return budgetMax; }
  public void setBudgetMax(BigDecimal budgetMax) { this.budgetMax = budgetMax; }
  public String getBudgetNote() { return budgetNote; }
  public void setBudgetNote(String budgetNote) { this.budgetNote = budgetNote; }
}
