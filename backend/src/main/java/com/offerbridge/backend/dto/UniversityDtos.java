package com.offerbridge.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class UniversityDtos {
  public static class FilterOption {
    private String code;
    private String name;
    private String parentCode;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getParentCode() { return parentCode; }
    public void setParentCode(String parentCode) { this.parentCode = parentCode; }
  }

  public static class MetaView {
    private List<FilterOption> countries;
    private List<FilterOption> subjectCategories;
    private List<FilterOption> directions;
    private List<FilterOption> applicationStatuses;

    public List<FilterOption> getCountries() { return countries; }
    public void setCountries(List<FilterOption> countries) { this.countries = countries; }
    public List<FilterOption> getSubjectCategories() { return subjectCategories; }
    public void setSubjectCategories(List<FilterOption> subjectCategories) { this.subjectCategories = subjectCategories; }
    public List<FilterOption> getDirections() { return directions; }
    public void setDirections(List<FilterOption> directions) { this.directions = directions; }
    public List<FilterOption> getApplicationStatuses() { return applicationStatuses; }
    public void setApplicationStatuses(List<FilterOption> applicationStatuses) { this.applicationStatuses = applicationStatuses; }
  }

  public static class SchoolListItem {
    private Long id;
    private String schoolNameCn;
    private String schoolNameEn;
    private String countryCode;
    private String countryName;
    private String cityName;
    private Integer qsRank;
    private Integer usnewsRank;
    private Integer usnewsRankingYear;
    private Integer displayRank;
    private String displayRankingSource;
    private String advantageSubjects;
    private BigDecimal tuitionMin;
    private BigDecimal tuitionMax;
    private String tuitionCurrency;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSchoolNameCn() { return schoolNameCn; }
    public void setSchoolNameCn(String schoolNameCn) { this.schoolNameCn = schoolNameCn; }
    public String getSchoolNameEn() { return schoolNameEn; }
    public void setSchoolNameEn(String schoolNameEn) { this.schoolNameEn = schoolNameEn; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public Integer getQsRank() { return qsRank; }
    public void setQsRank(Integer qsRank) { this.qsRank = qsRank; }
    public Integer getUsnewsRank() { return usnewsRank; }
    public void setUsnewsRank(Integer usnewsRank) { this.usnewsRank = usnewsRank; }
    public Integer getUsnewsRankingYear() { return usnewsRankingYear; }
    public void setUsnewsRankingYear(Integer usnewsRankingYear) { this.usnewsRankingYear = usnewsRankingYear; }
    public Integer getDisplayRank() { return displayRank; }
    public void setDisplayRank(Integer displayRank) { this.displayRank = displayRank; }
    public String getDisplayRankingSource() { return displayRankingSource; }
    public void setDisplayRankingSource(String displayRankingSource) { this.displayRankingSource = displayRankingSource; }
    public String getAdvantageSubjects() { return advantageSubjects; }
    public void setAdvantageSubjects(String advantageSubjects) { this.advantageSubjects = advantageSubjects; }
    public BigDecimal getTuitionMin() { return tuitionMin; }
    public void setTuitionMin(BigDecimal tuitionMin) { this.tuitionMin = tuitionMin; }
    public BigDecimal getTuitionMax() { return tuitionMax; }
    public void setTuitionMax(BigDecimal tuitionMax) { this.tuitionMax = tuitionMax; }
    public String getTuitionCurrency() { return tuitionCurrency; }
    public void setTuitionCurrency(String tuitionCurrency) { this.tuitionCurrency = tuitionCurrency; }
  }

  public static class SchoolDetailView {
    private Long id;
    private String schoolNameCn;
    private String schoolNameEn;
    private String countryCode;
    private String countryName;
    private String cityName;
    private Integer qsRank;
    private Integer rankingYear;
    private Integer usnewsRank;
    private Integer usnewsRankingYear;
    private Integer displayRank;
    private String displayRankingSource;
    private String schoolSummary;
    private BigDecimal tuitionMin;
    private BigDecimal tuitionMax;
    private String tuitionCurrency;
    private Integer durationMinMonths;
    private Integer durationMaxMonths;
    private String languageRequirementRange;
    private String advantageSubjects;
    private List<ProgramListItem> representativePrograms;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSchoolNameCn() { return schoolNameCn; }
    public void setSchoolNameCn(String schoolNameCn) { this.schoolNameCn = schoolNameCn; }
    public String getSchoolNameEn() { return schoolNameEn; }
    public void setSchoolNameEn(String schoolNameEn) { this.schoolNameEn = schoolNameEn; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public Integer getQsRank() { return qsRank; }
    public void setQsRank(Integer qsRank) { this.qsRank = qsRank; }
    public Integer getRankingYear() { return rankingYear; }
    public void setRankingYear(Integer rankingYear) { this.rankingYear = rankingYear; }
    public Integer getUsnewsRank() { return usnewsRank; }
    public void setUsnewsRank(Integer usnewsRank) { this.usnewsRank = usnewsRank; }
    public Integer getUsnewsRankingYear() { return usnewsRankingYear; }
    public void setUsnewsRankingYear(Integer usnewsRankingYear) { this.usnewsRankingYear = usnewsRankingYear; }
    public Integer getDisplayRank() { return displayRank; }
    public void setDisplayRank(Integer displayRank) { this.displayRank = displayRank; }
    public String getDisplayRankingSource() { return displayRankingSource; }
    public void setDisplayRankingSource(String displayRankingSource) { this.displayRankingSource = displayRankingSource; }
    public String getSchoolSummary() { return schoolSummary; }
    public void setSchoolSummary(String schoolSummary) { this.schoolSummary = schoolSummary; }
    public BigDecimal getTuitionMin() { return tuitionMin; }
    public void setTuitionMin(BigDecimal tuitionMin) { this.tuitionMin = tuitionMin; }
    public BigDecimal getTuitionMax() { return tuitionMax; }
    public void setTuitionMax(BigDecimal tuitionMax) { this.tuitionMax = tuitionMax; }
    public String getTuitionCurrency() { return tuitionCurrency; }
    public void setTuitionCurrency(String tuitionCurrency) { this.tuitionCurrency = tuitionCurrency; }
    public Integer getDurationMinMonths() { return durationMinMonths; }
    public void setDurationMinMonths(Integer durationMinMonths) { this.durationMinMonths = durationMinMonths; }
    public Integer getDurationMaxMonths() { return durationMaxMonths; }
    public void setDurationMaxMonths(Integer durationMaxMonths) { this.durationMaxMonths = durationMaxMonths; }
    public String getLanguageRequirementRange() { return languageRequirementRange; }
    public void setLanguageRequirementRange(String languageRequirementRange) { this.languageRequirementRange = languageRequirementRange; }
    public String getAdvantageSubjects() { return advantageSubjects; }
    public void setAdvantageSubjects(String advantageSubjects) { this.advantageSubjects = advantageSubjects; }
    public List<ProgramListItem> getRepresentativePrograms() { return representativePrograms; }
    public void setRepresentativePrograms(List<ProgramListItem> representativePrograms) { this.representativePrograms = representativePrograms; }
  }

  public static class ProgramListItem {
    private Long id;
    private Long schoolId;
    private String schoolNameCn;
    private String schoolNameEn;
    private Integer qsRank;
    private Integer usnewsRank;
    private Integer primaryRank;
    private String rankingSource;
    private String countryCode;
    private String countryName;
    private String programName;
    private String collegeName;
    private String subjectCategoryCode;
    private String subjectCategoryName;
    private String directionCode;
    private String directionName;
    private String studyMode;
    private Integer durationMonths;
    private BigDecimal tuitionAmount;
    private String tuitionCurrency;
    private String languageType;
    private BigDecimal languageMinScore;
    private BigDecimal gpaMinRecommend;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    public String getSchoolNameCn() { return schoolNameCn; }
    public void setSchoolNameCn(String schoolNameCn) { this.schoolNameCn = schoolNameCn; }
    public String getSchoolNameEn() { return schoolNameEn; }
    public void setSchoolNameEn(String schoolNameEn) { this.schoolNameEn = schoolNameEn; }
    public Integer getQsRank() { return qsRank; }
    public void setQsRank(Integer qsRank) { this.qsRank = qsRank; }
    public Integer getUsnewsRank() { return usnewsRank; }
    public void setUsnewsRank(Integer usnewsRank) { this.usnewsRank = usnewsRank; }
    public Integer getPrimaryRank() { return primaryRank; }
    public void setPrimaryRank(Integer primaryRank) { this.primaryRank = primaryRank; }
    public String getRankingSource() { return rankingSource; }
    public void setRankingSource(String rankingSource) { this.rankingSource = rankingSource; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }
    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }
    public String getSubjectCategoryCode() { return subjectCategoryCode; }
    public void setSubjectCategoryCode(String subjectCategoryCode) { this.subjectCategoryCode = subjectCategoryCode; }
    public String getSubjectCategoryName() { return subjectCategoryName; }
    public void setSubjectCategoryName(String subjectCategoryName) { this.subjectCategoryName = subjectCategoryName; }
    public String getDirectionCode() { return directionCode; }
    public void setDirectionCode(String directionCode) { this.directionCode = directionCode; }
    public String getDirectionName() { return directionName; }
    public void setDirectionName(String directionName) { this.directionName = directionName; }
    public String getStudyMode() { return studyMode; }
    public void setStudyMode(String studyMode) { this.studyMode = studyMode; }
    public Integer getDurationMonths() { return durationMonths; }
    public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }
    public BigDecimal getTuitionAmount() { return tuitionAmount; }
    public void setTuitionAmount(BigDecimal tuitionAmount) { this.tuitionAmount = tuitionAmount; }
    public String getTuitionCurrency() { return tuitionCurrency; }
    public void setTuitionCurrency(String tuitionCurrency) { this.tuitionCurrency = tuitionCurrency; }
    public String getLanguageType() { return languageType; }
    public void setLanguageType(String languageType) { this.languageType = languageType; }
    public BigDecimal getLanguageMinScore() { return languageMinScore; }
    public void setLanguageMinScore(BigDecimal languageMinScore) { this.languageMinScore = languageMinScore; }
    public BigDecimal getGpaMinRecommend() { return gpaMinRecommend; }
    public void setGpaMinRecommend(BigDecimal gpaMinRecommend) { this.gpaMinRecommend = gpaMinRecommend; }
  }

  public static class ProgramDetailView {
    private ProgramListItem basic;
    private String degreeType;
    private Boolean requiresGre;
    private Boolean requiresGmat;
    private String backgroundPreference;
    private String applicationRoundsOverview;
    private String suitableTags;
    private String intakeTerm;
    private String programSummary;
    private ProgramMatchResult matchResult;

    public ProgramListItem getBasic() { return basic; }
    public void setBasic(ProgramListItem basic) { this.basic = basic; }
    public String getDegreeType() { return degreeType; }
    public void setDegreeType(String degreeType) { this.degreeType = degreeType; }
    public Boolean getRequiresGre() { return requiresGre; }
    public void setRequiresGre(Boolean requiresGre) { this.requiresGre = requiresGre; }
    public Boolean getRequiresGmat() { return requiresGmat; }
    public void setRequiresGmat(Boolean requiresGmat) { this.requiresGmat = requiresGmat; }
    public String getBackgroundPreference() { return backgroundPreference; }
    public void setBackgroundPreference(String backgroundPreference) { this.backgroundPreference = backgroundPreference; }
    public String getApplicationRoundsOverview() { return applicationRoundsOverview; }
    public void setApplicationRoundsOverview(String applicationRoundsOverview) { this.applicationRoundsOverview = applicationRoundsOverview; }
    public String getSuitableTags() { return suitableTags; }
    public void setSuitableTags(String suitableTags) { this.suitableTags = suitableTags; }
    public String getIntakeTerm() { return intakeTerm; }
    public void setIntakeTerm(String intakeTerm) { this.intakeTerm = intakeTerm; }
    public String getProgramSummary() { return programSummary; }
    public void setProgramSummary(String programSummary) { this.programSummary = programSummary; }
    public ProgramMatchResult getMatchResult() { return matchResult; }
    public void setMatchResult(ProgramMatchResult matchResult) { this.matchResult = matchResult; }
  }

  public static class ProgramMatchResult {
    private Integer matchScore;
    private String matchTier;
    private List<String> reasonTags;

    public Integer getMatchScore() { return matchScore; }
    public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }
    public String getMatchTier() { return matchTier; }
    public void setMatchTier(String matchTier) { this.matchTier = matchTier; }
    public List<String> getReasonTags() { return reasonTags; }
    public void setReasonTags(List<String> reasonTags) { this.reasonTags = reasonTags; }
  }

  public static class ApplicationListItem {
    private Long id;
    private Long programId;
    private String statusCode;
    private String noteText;
    private ProgramListItem program;
    private ProgramMatchResult matchResult;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProgramId() { return programId; }
    public void setProgramId(Long programId) { this.programId = programId; }
    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }
    public ProgramListItem getProgram() { return program; }
    public void setProgram(ProgramListItem program) { this.program = program; }
    public ProgramMatchResult getMatchResult() { return matchResult; }
    public void setMatchResult(ProgramMatchResult matchResult) { this.matchResult = matchResult; }
  }

  public static class ApplicationListView {
    private List<ApplicationListItem> items;

    public List<ApplicationListItem> getItems() { return items; }
    public void setItems(List<ApplicationListItem> items) { this.items = items; }
  }

  public static class AddApplicationRequest {
    @NotNull
    private Long programId;
    private String noteText;

    public Long getProgramId() { return programId; }
    public void setProgramId(Long programId) { this.programId = programId; }
    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }
  }

  public static class UpdateApplicationStatusRequest {
    @NotBlank
    private String statusCode;

    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
  }

}
