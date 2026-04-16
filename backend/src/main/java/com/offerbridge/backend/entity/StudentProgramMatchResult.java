package com.offerbridge.backend.entity;

public class StudentProgramMatchResult {
  private Long id;
  private Long userId;
  private Long programId;
  private Integer matchScore;
  private String matchTier;
  private String reasonTagsJson;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public Long getProgramId() { return programId; }
  public void setProgramId(Long programId) { this.programId = programId; }
  public Integer getMatchScore() { return matchScore; }
  public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }
  public String getMatchTier() { return matchTier; }
  public void setMatchTier(String matchTier) { this.matchTier = matchTier; }
  public String getReasonTagsJson() { return reasonTagsJson; }
  public void setReasonTagsJson(String reasonTagsJson) { this.reasonTagsJson = reasonTagsJson; }
}
