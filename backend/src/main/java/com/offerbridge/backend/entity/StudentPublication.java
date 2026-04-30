package com.offerbridge.backend.entity;

public class StudentPublication {
  private Long id;
  private Long researchId;
  private String title;
  private String authorRole;
  private String authorOrder;
  private String journalName;
  private String publicationLevel;
  private String journalPartition;
  private Integer publishedYear;
  private String indexedInfo;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getResearchId() { return researchId; }
  public void setResearchId(Long researchId) { this.researchId = researchId; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getAuthorRole() { return authorRole; }
  public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }
  public String getAuthorOrder() { return authorOrder; }
  public void setAuthorOrder(String authorOrder) { this.authorOrder = authorOrder; }
  public String getJournalName() { return journalName; }
  public void setJournalName(String journalName) { this.journalName = journalName; }
  public String getPublicationLevel() { return publicationLevel; }
  public void setPublicationLevel(String publicationLevel) { this.publicationLevel = publicationLevel; }
  public String getJournalPartition() { return journalPartition; }
  public void setJournalPartition(String journalPartition) { this.journalPartition = journalPartition; }
  public Integer getPublishedYear() { return publishedYear; }
  public void setPublishedYear(Integer publishedYear) { this.publishedYear = publishedYear; }
  public String getIndexedInfo() { return indexedInfo; }
  public void setIndexedInfo(String indexedInfo) { this.indexedInfo = indexedInfo; }
}
