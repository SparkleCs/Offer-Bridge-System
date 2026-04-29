package com.offerbridge.backend.dto;

import java.time.LocalDateTime;
import java.time.Instant;
import java.math.BigDecimal;
import java.util.List;

public class MessageDtos {
  public static class SystemNotificationItem {
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String content;
    private String status;
    private String relatedType;
    private String relatedId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRelatedType() { return relatedType; }
    public void setRelatedType(String relatedType) { this.relatedType = relatedType; }
    public String getRelatedId() { return relatedId; }
    public void setRelatedId(String relatedId) { this.relatedId = relatedId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
  }

  public static class PagedResult<T> {
    private List<T> records;
    private long total;
    private int page;
    private int pageSize;
    private long unreadCount;

    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }
  }

  public static class MarkReadRequest {
    private Boolean markAll;
    private List<Long> ids;

    public Boolean getMarkAll() { return markAll; }
    public void setMarkAll(Boolean markAll) { this.markAll = markAll; }
    public List<Long> getIds() { return ids; }
    public void setIds(List<Long> ids) { this.ids = ids; }
  }

  public static class MarkReadResult {
    private long updatedCount;

    public long getUpdatedCount() { return updatedCount; }
    public void setUpdatedCount(long updatedCount) { this.updatedCount = updatedCount; }
  }

  public static class StartChatRequest {
    private Long teamId;
    private String greeting;

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public String getGreeting() { return greeting; }
    public void setGreeting(String greeting) { this.greeting = greeting; }
  }

  public static class AgentStartChatRequest {
    private Long studentUserId;
    private Long teamId;
    private String greeting;

    public Long getStudentUserId() { return studentUserId; }
    public void setStudentUserId(Long studentUserId) { this.studentUserId = studentUserId; }
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public String getGreeting() { return greeting; }
    public void setGreeting(String greeting) { this.greeting = greeting; }
  }

  public static class SendChatMessageRequest {
    private String content;
    private String contentType;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
  }

  public static class ChatConversationItem {
    private String conversationId;
    private Long studentUserId;
    private Long agentUserId;
    private Long agentMemberId;
    private Long teamId;
    private Long orgId;
    private String teamName;
    private String orgName;
    private String agentName;
    private String agentAvatarUrl;
    private String agentJobTitle;
    private String studentName;
    private String studentSchoolName;
    private String studentMajor;
    private String studentEducationLevel;
    private String studentTargetMajorText;
    private String peerName;
    private String peerSubtitle;
    private String peerAvatarUrl;
    private String lastMessage;
    private String lastSenderRole;
    private int studentMessageCount;
    private int agentMessageCount;
    private boolean viewerStarred;
    private boolean resumeAccessGranted;
    private boolean phoneExchangeGranted;
    private boolean wechatExchangeGranted;
    private String relatedOrderStatus;
    private Long relatedOrderId;
    private int unreadCount;
    private Instant createdAt;
    private Instant updatedAt;

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    public Long getStudentUserId() { return studentUserId; }
    public void setStudentUserId(Long studentUserId) { this.studentUserId = studentUserId; }
    public Long getAgentUserId() { return agentUserId; }
    public void setAgentUserId(Long agentUserId) { this.agentUserId = agentUserId; }
    public Long getAgentMemberId() { return agentMemberId; }
    public void setAgentMemberId(Long agentMemberId) { this.agentMemberId = agentMemberId; }
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public String getAgentAvatarUrl() { return agentAvatarUrl; }
    public void setAgentAvatarUrl(String agentAvatarUrl) { this.agentAvatarUrl = agentAvatarUrl; }
    public String getAgentJobTitle() { return agentJobTitle; }
    public void setAgentJobTitle(String agentJobTitle) { this.agentJobTitle = agentJobTitle; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentSchoolName() { return studentSchoolName; }
    public void setStudentSchoolName(String studentSchoolName) { this.studentSchoolName = studentSchoolName; }
    public String getStudentMajor() { return studentMajor; }
    public void setStudentMajor(String studentMajor) { this.studentMajor = studentMajor; }
    public String getStudentEducationLevel() { return studentEducationLevel; }
    public void setStudentEducationLevel(String studentEducationLevel) { this.studentEducationLevel = studentEducationLevel; }
    public String getStudentTargetMajorText() { return studentTargetMajorText; }
    public void setStudentTargetMajorText(String studentTargetMajorText) { this.studentTargetMajorText = studentTargetMajorText; }
    public String getPeerName() { return peerName; }
    public void setPeerName(String peerName) { this.peerName = peerName; }
    public String getPeerSubtitle() { return peerSubtitle; }
    public void setPeerSubtitle(String peerSubtitle) { this.peerSubtitle = peerSubtitle; }
    public String getPeerAvatarUrl() { return peerAvatarUrl; }
    public void setPeerAvatarUrl(String peerAvatarUrl) { this.peerAvatarUrl = peerAvatarUrl; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public String getLastSenderRole() { return lastSenderRole; }
    public void setLastSenderRole(String lastSenderRole) { this.lastSenderRole = lastSenderRole; }
    public int getStudentMessageCount() { return studentMessageCount; }
    public void setStudentMessageCount(int studentMessageCount) { this.studentMessageCount = studentMessageCount; }
    public int getAgentMessageCount() { return agentMessageCount; }
    public void setAgentMessageCount(int agentMessageCount) { this.agentMessageCount = agentMessageCount; }
    public boolean isViewerStarred() { return viewerStarred; }
    public void setViewerStarred(boolean viewerStarred) { this.viewerStarred = viewerStarred; }
    public boolean isResumeAccessGranted() { return resumeAccessGranted; }
    public void setResumeAccessGranted(boolean resumeAccessGranted) { this.resumeAccessGranted = resumeAccessGranted; }
    public boolean isPhoneExchangeGranted() { return phoneExchangeGranted; }
    public void setPhoneExchangeGranted(boolean phoneExchangeGranted) { this.phoneExchangeGranted = phoneExchangeGranted; }
    public boolean isWechatExchangeGranted() { return wechatExchangeGranted; }
    public void setWechatExchangeGranted(boolean wechatExchangeGranted) { this.wechatExchangeGranted = wechatExchangeGranted; }
    public String getRelatedOrderStatus() { return relatedOrderStatus; }
    public void setRelatedOrderStatus(String relatedOrderStatus) { this.relatedOrderStatus = relatedOrderStatus; }
    public Long getRelatedOrderId() { return relatedOrderId; }
    public void setRelatedOrderId(Long relatedOrderId) { this.relatedOrderId = relatedOrderId; }
    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
  }

  public static class ChatMessageItem {
    private String id;
    private String conversationId;
    private Long senderUserId;
    private Long receiverUserId;
    private String senderRole;
    private String contentType;
    private String content;
    private String status;
    private boolean mine;
    private Instant createdAt;
    private Instant readAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    public Long getSenderUserId() { return senderUserId; }
    public void setSenderUserId(Long senderUserId) { this.senderUserId = senderUserId; }
    public Long getReceiverUserId() { return receiverUserId; }
    public void setReceiverUserId(Long receiverUserId) { this.receiverUserId = receiverUserId; }
    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isMine() { return mine; }
    public void setMine(boolean mine) { this.mine = mine; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }
  }

  public static class ChatStartResult {
    private ChatConversationItem conversation;
    private ChatMessageItem firstMessage;

    public ChatConversationItem getConversation() { return conversation; }
    public void setConversation(ChatConversationItem conversation) { this.conversation = conversation; }
    public ChatMessageItem getFirstMessage() { return firstMessage; }
    public void setFirstMessage(ChatMessageItem firstMessage) { this.firstMessage = firstMessage; }
  }

  public static class ChatUnreadSummary {
    private long unreadCount;

    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }
  }

  public static class ChatActionRequest {
    private String actionType;

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
  }

  public static class ChatActionRespondRequest {
    private Boolean approved;

    public Boolean getApproved() { return approved; }
    public void setApproved(Boolean approved) { this.approved = approved; }
  }

  public static class ContactExchangeView {
    private String contactType;
    private String ownContact;
    private String peerContact;

    public String getContactType() { return contactType; }
    public void setContactType(String contactType) { this.contactType = contactType; }
    public String getOwnContact() { return ownContact; }
    public void setOwnContact(String ownContact) { this.ownContact = ownContact; }
    public String getPeerContact() { return peerContact; }
    public void setPeerContact(String peerContact) { this.peerContact = peerContact; }
  }

  public static class StudentAcademicResumeView {
    private Long studentUserId;
    private String displayName;
    private String educationLevel;
    private String schoolName;
    private String major;
    private BigDecimal gpaValue;
    private String gpaScale;
    private Integer rankValue;
    private List<LanguageScoreItem> languageScores;
    private String targetMajorText;
    private String intakeTerm;
    private List<TargetCountryItem> targetCountries;
    private ExchangeExperienceItem exchangeExperience;
    private List<ResearchItem> researchExperiences;
    private List<CompetitionItem> competitionExperiences;
    private List<WorkItem> workExperiences;

    public Long getStudentUserId() { return studentUserId; }
    public void setStudentUserId(Long studentUserId) { this.studentUserId = studentUserId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
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
    public List<LanguageScoreItem> getLanguageScores() { return languageScores; }
    public void setLanguageScores(List<LanguageScoreItem> languageScores) { this.languageScores = languageScores; }
    public String getTargetMajorText() { return targetMajorText; }
    public void setTargetMajorText(String targetMajorText) { this.targetMajorText = targetMajorText; }
    public String getIntakeTerm() { return intakeTerm; }
    public void setIntakeTerm(String intakeTerm) { this.intakeTerm = intakeTerm; }
    public List<TargetCountryItem> getTargetCountries() { return targetCountries; }
    public void setTargetCountries(List<TargetCountryItem> targetCountries) { this.targetCountries = targetCountries; }
    public ExchangeExperienceItem getExchangeExperience() { return exchangeExperience; }
    public void setExchangeExperience(ExchangeExperienceItem exchangeExperience) { this.exchangeExperience = exchangeExperience; }
    public List<ResearchItem> getResearchExperiences() { return researchExperiences; }
    public void setResearchExperiences(List<ResearchItem> researchExperiences) { this.researchExperiences = researchExperiences; }
    public List<CompetitionItem> getCompetitionExperiences() { return competitionExperiences; }
    public void setCompetitionExperiences(List<CompetitionItem> competitionExperiences) { this.competitionExperiences = competitionExperiences; }
    public List<WorkItem> getWorkExperiences() { return workExperiences; }
    public void setWorkExperiences(List<WorkItem> workExperiences) { this.workExperiences = workExperiences; }
  }

  public static class LanguageScoreItem {
    private String languageType;
    private BigDecimal score;

    public String getLanguageType() { return languageType; }
    public void setLanguageType(String languageType) { this.languageType = languageType; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
  }

  public static class TargetCountryItem {
    private String countryName;

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
  }

  public static class PublicationItem {
    private String title;
    private String authorRole;
    private String journalName;
    private Integer publishedYear;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthorRole() { return authorRole; }
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }
    public String getJournalName() { return journalName; }
    public void setJournalName(String journalName) { this.journalName = journalName; }
    public Integer getPublishedYear() { return publishedYear; }
    public void setPublishedYear(Integer publishedYear) { this.publishedYear = publishedYear; }
  }

  public static class ResearchItem {
    private Long id;
    private String projectName;
    private String startDate;
    private String endDate;
    private String contentSummary;
    private Boolean hasPublication;
    private List<PublicationItem> publications;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getContentSummary() { return contentSummary; }
    public void setContentSummary(String contentSummary) { this.contentSummary = contentSummary; }
    public Boolean getHasPublication() { return hasPublication; }
    public void setHasPublication(Boolean hasPublication) { this.hasPublication = hasPublication; }
    public List<PublicationItem> getPublications() { return publications; }
    public void setPublications(List<PublicationItem> publications) { this.publications = publications; }
  }

  public static class CompetitionItem {
    private Long id;
    private String competitionName;
    private String competitionLevel;
    private String award;
    private String roleDesc;
    private String eventDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCompetitionName() { return competitionName; }
    public void setCompetitionName(String competitionName) { this.competitionName = competitionName; }
    public String getCompetitionLevel() { return competitionLevel; }
    public void setCompetitionLevel(String competitionLevel) { this.competitionLevel = competitionLevel; }
    public String getAward() { return award; }
    public void setAward(String award) { this.award = award; }
    public String getRoleDesc() { return roleDesc; }
    public void setRoleDesc(String roleDesc) { this.roleDesc = roleDesc; }
    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
  }

  public static class WorkItem {
    private Long id;
    private String companyName;
    private String positionName;
    private String startDate;
    private String endDate;
    private String keywords;
    private String contentSummary;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }
    public String getContentSummary() { return contentSummary; }
    public void setContentSummary(String contentSummary) { this.contentSummary = contentSummary; }
  }

  public static class ExchangeExperienceItem {
    private String countryName;
    private String universityName;
    private BigDecimal gpaValue;
    private String majorCourses;
    private String startDate;
    private String endDate;

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    public String getUniversityName() { return universityName; }
    public void setUniversityName(String universityName) { this.universityName = universityName; }
    public BigDecimal getGpaValue() { return gpaValue; }
    public void setGpaValue(BigDecimal gpaValue) { this.gpaValue = gpaValue; }
    public String getMajorCourses() { return majorCourses; }
    public void setMajorCourses(String majorCourses) { this.majorCourses = majorCourses; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
  }
}
