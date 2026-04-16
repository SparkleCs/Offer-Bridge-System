package com.offerbridge.backend.entity;

public class StudentApplicationProgramRow extends Program {
  private Long applicationId;
  private String statusCode;
  private String noteText;

  public Long getApplicationId() { return applicationId; }
  public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
  public String getStatusCode() { return statusCode; }
  public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
  public String getNoteText() { return noteText; }
  public void setNoteText(String noteText) { this.noteText = noteText; }
}
