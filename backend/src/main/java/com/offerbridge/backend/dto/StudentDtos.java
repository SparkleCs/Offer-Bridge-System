package com.offerbridge.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class StudentDtos {
  public static class LanguageScoreItem {
    @NotBlank
    private String languageType;
    @NotNull
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal score;

    public String getLanguageType() { return languageType; }
    public void setLanguageType(String languageType) { this.languageType = languageType; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
  }

  public static class ProfileBasicUpdateRequest {
    private String name;
    @Email
    private String email;
    private String wechatId;
    private String educationLevel;
    private String schoolName;
    private String major;
    private String targetMajorText;
    private String intakeTerm;
    private String budgetCurrency;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String budgetNote;
    @Valid
    private List<TargetCountryItem> targetCountries;

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
    public List<TargetCountryItem> getTargetCountries() { return targetCountries; }
    public void setTargetCountries(List<TargetCountryItem> targetCountries) { this.targetCountries = targetCountries; }
  }

  public static class ProfileAcademicUpdateRequest {
    @NotNull
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal gpaValue;
    @NotBlank
    private String gpaScale;
    @NotEmpty
    @Valid
    private List<LanguageScoreItem> languageScores;
    private Integer rankValue;

    public BigDecimal getGpaValue() { return gpaValue; }
    public void setGpaValue(BigDecimal gpaValue) { this.gpaValue = gpaValue; }
    public String getGpaScale() { return gpaScale; }
    public void setGpaScale(String gpaScale) { this.gpaScale = gpaScale; }
    public List<LanguageScoreItem> getLanguageScores() { return languageScores; }
    public void setLanguageScores(List<LanguageScoreItem> languageScores) { this.languageScores = languageScores; }
    public Integer getRankValue() { return rankValue; }
    public void setRankValue(Integer rankValue) { this.rankValue = rankValue; }
  }

  public static class WechatUpdateRequest {
    @NotBlank
    private String wechatId;

    public String getWechatId() { return wechatId; }
    public void setWechatId(String wechatId) { this.wechatId = wechatId; }
  }

  public static class ProfileView {
    private Long userId;
    private String phone;
    private String name;
    private String email;
    private String wechatId;
    private String educationLevel;
    private String schoolName;
    private String major;
    private BigDecimal gpaValue;
    private String gpaScale;
    private List<LanguageScoreItem> languageScores;
    private Integer rankValue;
    private String targetMajorText;
    private String intakeTerm;
    private String budgetCurrency;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String budgetNote;
    private List<TargetCountryItem> targetCountries;
    private boolean profileCompleted;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
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
    public List<LanguageScoreItem> getLanguageScores() { return languageScores; }
    public void setLanguageScores(List<LanguageScoreItem> languageScores) { this.languageScores = languageScores; }
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
    public List<TargetCountryItem> getTargetCountries() { return targetCountries; }
    public void setTargetCountries(List<TargetCountryItem> targetCountries) { this.targetCountries = targetCountries; }
    public boolean isProfileCompleted() { return profileCompleted; }
    public void setProfileCompleted(boolean profileCompleted) { this.profileCompleted = profileCompleted; }
  }

  public static class TargetCountryItem {
    @NotBlank
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
    @NotNull
    private Boolean hasPublication;
    @Valid
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

  public static class ResearchSaveRequest {
    @NotNull
    @Valid
    private List<ResearchItem> items;

    public List<ResearchItem> getItems() { return items; }
    public void setItems(List<ResearchItem> items) { this.items = items; }
  }

  public static class ResearchView {
    private List<ResearchItem> items;

    public List<ResearchItem> getItems() { return items; }
    public void setItems(List<ResearchItem> items) { this.items = items; }
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

  public static class CompetitionSaveRequest {
    @NotNull
    @Valid
    private List<CompetitionItem> items;

    public List<CompetitionItem> getItems() { return items; }
    public void setItems(List<CompetitionItem> items) { this.items = items; }
  }

  public static class CompetitionView {
    private List<CompetitionItem> items;

    public List<CompetitionItem> getItems() { return items; }
    public void setItems(List<CompetitionItem> items) { this.items = items; }
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

  public static class WorkSaveRequest {
    @NotNull
    @Valid
    private List<WorkItem> items;

    public List<WorkItem> getItems() { return items; }
    public void setItems(List<WorkItem> items) { this.items = items; }
  }

  public static class WorkView {
    private List<WorkItem> items;

    public List<WorkItem> getItems() { return items; }
    public void setItems(List<WorkItem> items) { this.items = items; }
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

  public static class ExchangeExperienceSaveRequest {
    @NotBlank
    private String countryName;
    @NotBlank
    private String universityName;
    @NotNull
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal gpaValue;
    @NotBlank
    private String majorCourses;
    @NotBlank
    private String startDate;
    @NotBlank
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

  public static class DebugSourceCheckView {
    private boolean enabled;
    private String databaseName;
    private String researchTable;
    private String competitionTable;
    private String workTable;
    private String exchangeTable;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getDatabaseName() { return databaseName; }
    public void setDatabaseName(String databaseName) { this.databaseName = databaseName; }
    public String getResearchTable() { return researchTable; }
    public void setResearchTable(String researchTable) { this.researchTable = researchTable; }
    public String getCompetitionTable() { return competitionTable; }
    public void setCompetitionTable(String competitionTable) { this.competitionTable = competitionTable; }
    public String getWorkTable() { return workTable; }
    public void setWorkTable(String workTable) { this.workTable = workTable; }
    public String getExchangeTable() { return exchangeTable; }
    public void setExchangeTable(String exchangeTable) { this.exchangeTable = exchangeTable; }
  }

  public static class VerificationSubmitRequest {
    @NotBlank
    private String realName;
    @NotBlank
    private String idNo;
    @NotBlank
    private String idCardImageUrl;
    @NotBlank
    private String studentCardImageUrl;

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getIdNo() { return idNo; }
    public void setIdNo(String idNo) { this.idNo = idNo; }
    public String getIdCardImageUrl() { return idCardImageUrl; }
    public void setIdCardImageUrl(String idCardImageUrl) { this.idCardImageUrl = idCardImageUrl; }
    public String getStudentCardImageUrl() { return studentCardImageUrl; }
    public void setStudentCardImageUrl(String studentCardImageUrl) { this.studentCardImageUrl = studentCardImageUrl; }
  }

  public static class VerificationStatusView {
    private String realNameStatus;
    private String educationStatus;
    private boolean verificationCompleted;

    public String getRealNameStatus() { return realNameStatus; }
    public void setRealNameStatus(String realNameStatus) { this.realNameStatus = realNameStatus; }
    public String getEducationStatus() { return educationStatus; }
    public void setEducationStatus(String educationStatus) { this.educationStatus = educationStatus; }
    public boolean isVerificationCompleted() { return verificationCompleted; }
    public void setVerificationCompleted(boolean verificationCompleted) { this.verificationCompleted = verificationCompleted; }
  }
}
