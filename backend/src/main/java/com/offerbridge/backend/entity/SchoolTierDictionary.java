package com.offerbridge.backend.entity;

import java.math.BigDecimal;

public class SchoolTierDictionary {
  private Long id;
  private String schoolName;
  private String schoolAlias;
  private String tierCode;
  private BigDecimal tierScore;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getSchoolName() { return schoolName; }
  public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
  public String getSchoolAlias() { return schoolAlias; }
  public void setSchoolAlias(String schoolAlias) { this.schoolAlias = schoolAlias; }
  public String getTierCode() { return tierCode; }
  public void setTierCode(String tierCode) { this.tierCode = tierCode; }
  public BigDecimal getTierScore() { return tierScore; }
  public void setTierScore(BigDecimal tierScore) { this.tierScore = tierScore; }
}
