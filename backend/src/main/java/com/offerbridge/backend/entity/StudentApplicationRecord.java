package com.offerbridge.backend.entity;

public class StudentApplicationRecord {
  private Long id;
  private Long userId;
  private Long programId;
  private String statusCode;
  private String noteText;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public Long getProgramId() { return programId; }
  public void setProgramId(Long programId) { this.programId = programId; }
  public String getStatusCode() { return statusCode; }
  public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
  public String getNoteText() { return noteText; }
  public void setNoteText(String noteText) { this.noteText = noteText; }
}
