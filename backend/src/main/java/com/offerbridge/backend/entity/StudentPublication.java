package com.offerbridge.backend.entity;

public class StudentPublication {
  private Long id;
  private Long researchId;
  private String title;
  private String authorRole;
  private String journalName;
  private Integer publishedYear;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getResearchId() { return researchId; }
  public void setResearchId(Long researchId) { this.researchId = researchId; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getAuthorRole() { return authorRole; }
  public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }
  public String getJournalName() { return journalName; }
  public void setJournalName(String journalName) { this.journalName = journalName; }
  public Integer getPublishedYear() { return publishedYear; }
  public void setPublishedYear(Integer publishedYear) { this.publishedYear = publishedYear; }
}
