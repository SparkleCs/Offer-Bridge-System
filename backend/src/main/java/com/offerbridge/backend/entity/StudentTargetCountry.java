package com.offerbridge.backend.entity;

public class StudentTargetCountry {
  private Long id;
  private Long userId;
  private String countryName;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getCountryName() { return countryName; }
  public void setCountryName(String countryName) { this.countryName = countryName; }
}
