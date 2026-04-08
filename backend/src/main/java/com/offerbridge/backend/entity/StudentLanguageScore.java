package com.offerbridge.backend.entity;

import java.math.BigDecimal;

public class StudentLanguageScore {
  private Long id;
  private Long userId;
  private String languageType;
  private BigDecimal score;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getLanguageType() { return languageType; }
  public void setLanguageType(String languageType) { this.languageType = languageType; }
  public BigDecimal getScore() { return score; }
  public void setScore(BigDecimal score) { this.score = score; }
}
