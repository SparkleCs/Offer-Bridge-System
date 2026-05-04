package com.offerbridge.backend.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.offerbridge.backend.dto.AiDtos;
import com.offerbridge.backend.entity.AiAnalysisReport;
import com.offerbridge.backend.entity.AiProgramRecommendation;
import com.offerbridge.backend.entity.Program;
import com.offerbridge.backend.entity.StudentBackgroundScore;
import com.offerbridge.backend.entity.StudentLanguageScore;
import com.offerbridge.backend.entity.StudentProfile;
import com.offerbridge.backend.entity.StudentTargetCountry;
import com.offerbridge.backend.exception.BizException;
import com.offerbridge.backend.mapper.AiAnalysisReportMapper;
import com.offerbridge.backend.mapper.AiProgramRecommendationMapper;
import com.offerbridge.backend.mapper.ProgramMapper;
import com.offerbridge.backend.mapper.StudentLanguageScoreMapper;
import com.offerbridge.backend.mapper.StudentProgramMatchResultMapper;
import com.offerbridge.backend.mapper.StudentProfileMapper;
import com.offerbridge.backend.mapper.StudentTargetCountryMapper;
import com.offerbridge.backend.service.AiService;
import com.offerbridge.backend.service.StudentBackgroundScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AiServiceImpl implements AiService {
  private static final String REPORT_TYPE_RECOMMENDATION = "PROGRAM_RECOMMENDATION";
  private static final String REPORT_TYPE_PROGRAM_ANALYSIS = "PROGRAM_ANALYSIS";
  private static final String RISK_WARNING = "结果仅供申请规划参考，最终录取以学校审核为准。";
  private static final String AI_UNAVAILABLE_MESSAGE = "AI预测暂不可用，请稍后重试";

  private final StudentProfileMapper studentProfileMapper;
  private final StudentLanguageScoreMapper studentLanguageScoreMapper;
  private final StudentTargetCountryMapper studentTargetCountryMapper;
  private final ProgramMapper programMapper;
  private final StudentProgramMatchResultMapper studentProgramMatchResultMapper;
  private final StudentBackgroundScoreService studentBackgroundScoreService;
  private final AiAnalysisReportMapper aiAnalysisReportMapper;
  private final AiProgramRecommendationMapper aiProgramRecommendationMapper;
  private final AiClient aiClient;
  private final ObjectMapper objectMapper;

  public AiServiceImpl(StudentProfileMapper studentProfileMapper,
                       StudentLanguageScoreMapper studentLanguageScoreMapper,
                       StudentTargetCountryMapper studentTargetCountryMapper,
                       ProgramMapper programMapper,
                       StudentProgramMatchResultMapper studentProgramMatchResultMapper,
                       StudentBackgroundScoreService studentBackgroundScoreService,
                       AiAnalysisReportMapper aiAnalysisReportMapper,
                       AiProgramRecommendationMapper aiProgramRecommendationMapper,
                       AiClient aiClient,
                       ObjectMapper objectMapper) {
    this.studentProfileMapper = studentProfileMapper;
    this.studentLanguageScoreMapper = studentLanguageScoreMapper;
    this.studentTargetCountryMapper = studentTargetCountryMapper;
    this.programMapper = programMapper;
    this.studentProgramMatchResultMapper = studentProgramMatchResultMapper;
    this.studentBackgroundScoreService = studentBackgroundScoreService;
    this.aiAnalysisReportMapper = aiAnalysisReportMapper;
    this.aiProgramRecommendationMapper = aiProgramRecommendationMapper;
    this.aiClient = aiClient;
    this.objectMapper = objectMapper;
  }

  @Override
  @Transactional
  public AiDtos.AiReportView generateRecommendations(Long userId) {
    StudentInputs inputs = loadAndValidateInputs(userId);
    List<ScoredProgram> scoredPrograms = programMapper.listPrograms(null, null, null, null, null).stream()
      .map(program -> scoreProgram(userId, inputs, program))
      .sorted(Comparator.comparing((ScoredProgram item) -> item.candidate.ruleMatchScore).reversed())
      .limit(30)
      .toList();
    return buildCallAndPersist(userId, REPORT_TYPE_RECOMMENDATION, inputs, scoredPrograms, false);
  }

  @Override
  public AiDtos.AiReportView getLatestReport(Long userId) {
    AiAnalysisReport report = aiAnalysisReportMapper.findLatestByUserId(userId, REPORT_TYPE_RECOMMENDATION);
    if (report == null || report.getResultJson() == null) return null;
    try {
      AiDtos.AiReportView view = objectMapper.readValue(report.getResultJson(), AiDtos.AiReportView.class);
      view.reportId = report.getId();
      view.status = report.getStatus();
      view.modelProvider = report.getModelProvider();
      view.modelVersion = report.getModelVersion();
      return view;
    } catch (Exception ex) {
      return null;
    }
  }

  @Override
  @Transactional
  public AiDtos.AiReportView analyzeProgram(Long userId, Long programId) {
    StudentInputs inputs = loadAndValidateInputs(userId);
    Program program = programMapper.findById(programId);
    if (program == null) {
      throw new BizException("BIZ_NOT_FOUND", "项目不存在");
    }
    return buildCallAndPersist(userId, REPORT_TYPE_PROGRAM_ANALYSIS, inputs, List.of(scoreProgram(userId, inputs, program)), true);
  }

  private AiDtos.AiReportView buildCallAndPersist(Long userId,
                                                  String reportType,
                                                  StudentInputs inputs,
                                                  List<ScoredProgram> scoredPrograms,
                                                  boolean singleProgram) {
    AiDtos.AiRecommendationRequest request = new AiDtos.AiRecommendationRequest();
    request.studentProfile = buildStudentSnapshot(inputs);
    request.programs = scoredPrograms.stream().map(item -> item.candidate).toList();

    AiDtos.AiRecommendationResponse response = singleProgram
      ? aiClient.programAnalysis(request)
      : aiClient.recommendations(request);
    if (response == null || response.recommendations == null || response.recommendations.isEmpty()) {
      throw new BizException("BIZ_AI_UNAVAILABLE", AI_UNAVAILABLE_MESSAGE);
    }

    AiDtos.AiReportView view = toReportView(response, scoredPrograms);

    view.status = safeDefault(response.status, "SUCCESS");
    view.generatedAt = view.generatedAt == null ? LocalDateTime.now().toString() : view.generatedAt;
    if (view.riskWarnings == null || view.riskWarnings.isEmpty()) {
      view.riskWarnings = List.of(RISK_WARNING);
    }

    AiAnalysisReport report = new AiAnalysisReport();
    report.setUserId(userId);
    report.setReportType(reportType);
    report.setInputSnapshotJson(toJson(request));
    report.setResultJson(toJson(view));
    report.setModelProvider(safeDefault(view.modelProvider, "fastapi"));
    report.setModelVersion(safeDefault(view.modelVersion, "admission-lr-v1"));
    report.setStatus(view.status);
    aiAnalysisReportMapper.insertOne(report);

    view.reportId = report.getId();
    for (AiDtos.AiProgramRecommendationItem item : view.recommendations) {
      AiProgramRecommendation row = new AiProgramRecommendation();
      row.setReportId(report.getId());
      row.setProgramId(item.programId);
      row.setMatchScore(item.mlScore == null ? safeInt(item.ruleMatchScore) : item.mlScore);
      row.setMatchTier(safeDefault(item.matchTier, "匹配"));
      row.setProbabilityEstimate(item.admissionProbabilityEstimate == null ? BigDecimal.ZERO : item.admissionProbabilityEstimate);
      row.setConfidenceLevel(item.confidenceLevel);
      row.setReasonTagsJson(toJson(item.reasonTags == null ? List.of() : item.reasonTags));
      row.setAiSummary(item.aiSummary);
      aiProgramRecommendationMapper.insertOne(row);
    }

    return view;
  }

  private StudentInputs loadAndValidateInputs(Long userId) {
    StudentProfile profile = studentProfileMapper.findByUserId(userId);
    List<StudentLanguageScore> languages = studentLanguageScoreMapper.listByUserId(userId);
    List<StudentTargetCountry> countries = studentTargetCountryMapper.listByUserId(userId);
    List<String> missing = new ArrayList<>();
    if (profile == null) {
      missing.add("学生画像");
    } else {
      if (profile.getGpaValue() == null || profile.getGpaScale() == null) missing.add("GPA");
      if (profile.getTargetMajorText() == null || profile.getTargetMajorText().isBlank()) missing.add("意向专业");
    }
    if (languages == null || languages.isEmpty()) missing.add("语言成绩");
    if (countries == null || countries.isEmpty()) missing.add("目标国家");
    if (!missing.isEmpty()) {
      throw new BizException("BIZ_BAD_REQUEST", "学生画像不完整，请先补充：" + String.join("、", missing));
    }
    StudentBackgroundScore backgroundScore = studentBackgroundScoreService.getOrRefreshScore(userId);
    return new StudentInputs(profile, languages, countries, backgroundScore);
  }

  private ScoredProgram scoreProgram(Long userId, StudentInputs inputs, Program program) {
    int score = 45;
    List<String> reasons = new ArrayList<>();

    if (inputs.countries.stream().anyMatch(it -> safeEquals(it.getCountryName(), program.getCountryName()))) {
      score += 10;
      reasons.add("命中目标国家");
    } else {
      reasons.add("国家偏好不完全命中");
    }

    if (containsIgnoreCase(inputs.profile.getTargetMajorText(), program.getDirectionName())) {
      score += 15;
      reasons.add("命中目标专业方向");
    } else {
      reasons.add("专业方向建议进一步确认");
    }

    if (inputs.profile.getGpaValue() != null && program.getGpaMinRecommend() != null) {
      BigDecimal diff = normalizedGpa(inputs.profile).subtract(program.getGpaMinRecommend());
      if (diff.compareTo(new BigDecimal("0.30")) >= 0) {
        score += 14;
        reasons.add("GPA竞争力较强");
      } else if (diff.compareTo(BigDecimal.ZERO) >= 0) {
        score += 10;
        reasons.add("GPA达到建议线");
      } else if (diff.compareTo(new BigDecimal("-0.30")) >= 0) {
        score += 6;
        reasons.add("GPA略低于建议线");
      } else {
        reasons.add("GPA需重点提升");
      }
    } else {
      reasons.add("缺少完整GPA信息");
    }

    if (program.getLanguageType() != null && program.getLanguageMinScore() != null) {
      StudentLanguageScore bestScore = inputs.languages.stream()
        .filter(it -> safeEquals(it.getLanguageType(), program.getLanguageType()))
        .findFirst()
        .orElse(null);

      if (bestScore == null || bestScore.getScore() == null) {
        reasons.add("缺少对应语言成绩");
      } else {
        BigDecimal diff = bestScore.getScore().subtract(program.getLanguageMinScore());
        if (diff.compareTo(new BigDecimal("0.50")) >= 0) {
          score += 10;
          reasons.add("语言成绩有优势");
        } else if (diff.compareTo(BigDecimal.ZERO) >= 0) {
          score += 8;
          reasons.add("语言成绩达标");
        } else if (diff.compareTo(new BigDecimal("-0.50")) >= 0) {
          score += 4;
          reasons.add("语言成绩接近门槛");
        } else {
          reasons.add("语言成绩需提升");
        }
      }
    }

    if (inputs.profile.getBudgetMax() != null && program.getTuitionAmount() != null) {
      if (inputs.profile.getBudgetMax().compareTo(program.getTuitionAmount()) >= 0) {
        score += 6;
        reasons.add("预算覆盖学费");
      } else {
        score -= 4;
        reasons.add("预算可能不足");
      }
    }

    if (Boolean.TRUE.equals(program.getRequiresGre()) || Boolean.TRUE.equals(program.getRequiresGmat())) {
      score -= 2;
      reasons.add("标准化考试要求较高");
    }

    score = Math.max(0, Math.min(100, score));
    String tier = score >= 80 ? "保底" : score >= 65 ? "匹配" : "冲刺";
    studentProgramMatchResultMapper.upsert(userId, program.getId(), score, tier, toJson(reasons));

    AiDtos.AiProgramCandidate candidate = new AiDtos.AiProgramCandidate();
    candidate.programId = program.getId();
    candidate.schoolName = displaySchoolName(program);
    candidate.programName = program.getProgramName();
    candidate.countryName = program.getCountryName();
    candidate.directionName = program.getDirectionName();
    candidate.qsRank = program.getQsRank();
    candidate.gpaMinRecommend = program.getGpaMinRecommend();
    candidate.languageType = program.getLanguageType();
    candidate.languageMinScore = program.getLanguageMinScore();
    candidate.tuitionAmount = program.getTuitionAmount();
    candidate.backgroundPreference = program.getBackgroundPreference();
    candidate.ruleMatchScore = score;
    candidate.ruleMatchTier = tier;
    candidate.ruleReasonTags = reasons;
    return new ScoredProgram(program, candidate);
  }

  private AiDtos.AiReportView toReportView(AiDtos.AiRecommendationResponse response, List<ScoredProgram> scoredPrograms) {
    Map<Long, ScoredProgram> byProgramId = scoredPrograms.stream()
      .collect(Collectors.toMap(item -> item.program.getId(), item -> item, (left, right) -> left));

    AiDtos.AiReportView view = new AiDtos.AiReportView();
    view.status = safeDefault(response.status, "SUCCESS");
    view.overallSummary = safeDefault(response.overallSummary, "已生成 AI 择校报告。");
    view.generatedAt = response.generatedAt;
    view.modelProvider = safeDefault(response.modelProvider, "fastapi");
    view.modelVersion = safeDefault(response.modelVersion, "hybrid-mvp-v1");
    view.gapAnalysis = response.gapAnalysis == null ? new ArrayList<>() : response.gapAnalysis;
    view.improvementSuggestions = response.improvementSuggestions == null ? new ArrayList<>() : response.improvementSuggestions;
    view.riskWarnings = response.riskWarnings == null ? new ArrayList<>() : response.riskWarnings;

    Set<Long> knownProgramIds = byProgramId.keySet();
    view.recommendations = response.recommendations.stream()
      .filter(item -> item.programId != null && knownProgramIds.contains(item.programId))
      .map(item -> enrichRecommendation(item, byProgramId.get(item.programId)))
      .sorted(Comparator.comparing((AiDtos.AiProgramRecommendationItem item) -> item.admissionProbabilityEstimate == null ? BigDecimal.ZERO : item.admissionProbabilityEstimate).reversed())
      .toList();
    if (view.recommendations.isEmpty()) {
      throw new BizException("BIZ_AI_UNAVAILABLE", AI_UNAVAILABLE_MESSAGE);
    }
    return view;
  }

  private AiDtos.AiProgramRecommendationItem enrichRecommendation(AiDtos.AiProgramRecommendationItem item, ScoredProgram scored) {
    item.schoolId = scored.program.getSchoolId();
    item.schoolName = safeDefault(item.schoolName, displaySchoolName(scored.program));
    item.programName = safeDefault(item.programName, scored.program.getProgramName());
    item.countryName = safeDefault(item.countryName, scored.program.getCountryName());
    item.directionName = safeDefault(item.directionName, scored.program.getDirectionName());
    item.qsRank = item.qsRank == null ? scored.program.getQsRank() : item.qsRank;
    item.ruleMatchScore = item.ruleMatchScore == null ? scored.candidate.ruleMatchScore : item.ruleMatchScore;
    item.mlScore = item.mlScore == null ? item.ruleMatchScore : item.mlScore;
    item.admissionProbabilityEstimate = item.admissionProbabilityEstimate == null
      ? BigDecimal.valueOf(safeInt(item.mlScore)).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
      : item.admissionProbabilityEstimate;
    item.matchTier = safeDefault(item.matchTier, probabilityTier(item.admissionProbabilityEstimate));
    item.confidenceLevel = safeDefault(item.confidenceLevel, "medium");
    item.reasonTags = item.reasonTags == null || item.reasonTags.isEmpty() ? scored.candidate.ruleReasonTags : item.reasonTags;
    item.aiSummary = safeDefault(item.aiSummary, "该项目可作为当前申请组合中的候选项目。");
    return item;
  }

  private Map<String, Object> buildStudentSnapshot(StudentInputs inputs) {
    Map<String, Object> profile = new LinkedHashMap<>();
    profile.put("educationLevel", inputs.profile.getEducationLevel());
    profile.put("schoolName", inputs.profile.getSchoolName());
    profile.put("undergraduateSchoolTier", inputs.profile.getUndergraduateSchoolTier());
    profile.put("major", inputs.profile.getMajor());
    profile.put("targetMajorText", inputs.profile.getTargetMajorText());
    profile.put("gpaValue", inputs.profile.getGpaValue());
    profile.put("gpaScale", inputs.profile.getGpaScale());
    profile.put("rankValue", inputs.profile.getRankValue());
    profile.put("budgetCurrency", inputs.profile.getBudgetCurrency());
    profile.put("budgetMax", inputs.profile.getBudgetMax());
    profile.put("targetCountries", inputs.countries.stream().map(StudentTargetCountry::getCountryName).toList());
    profile.put("languageScores", inputs.languages.stream().map(item -> Map.of(
      "languageType", item.getLanguageType(),
      "score", item.getScore()
    )).toList());
    if (inputs.backgroundScore != null) {
      profile.put("backgroundScore", Map.of(
        "overallAcademicScore", inputs.backgroundScore.getOverallAcademicScore(),
        "gpaScore", inputs.backgroundScore.getGpaScore(),
        "languageScore", inputs.backgroundScore.getLanguageScore(),
        "publicationScore", inputs.backgroundScore.getPublicationScore(),
        "researchScore", inputs.backgroundScore.getResearchScore(),
        "internshipScore", inputs.backgroundScore.getInternshipScore(),
        "exchangeScore", inputs.backgroundScore.getExchangeScore(),
        "competitionScore", inputs.backgroundScore.getCompetitionScore(),
        "undergraduateSchoolScore", inputs.backgroundScore.getUndergraduateSchoolScore()
      ));
    }
    return profile;
  }

  private String probabilityTier(BigDecimal probability) {
    if (probability == null) return "匹配";
    if (probability.compareTo(new BigDecimal("0.75")) >= 0) return "保底";
    if (probability.compareTo(new BigDecimal("0.45")) >= 0) return "匹配";
    return "冲刺";
  }

  private AiDtos.GapAnalysisItem gap(String dimension, String current, String target, String priority) {
    AiDtos.GapAnalysisItem item = new AiDtos.GapAnalysisItem();
    item.dimension = dimension;
    item.current = current;
    item.target = target;
    item.priority = priority;
    return item;
  }

  private AiDtos.ImprovementSuggestionItem suggestion(String priority, String action, String reason) {
    AiDtos.ImprovementSuggestionItem item = new AiDtos.ImprovementSuggestionItem();
    item.priority = priority;
    item.action = action;
    item.reason = reason;
    return item;
  }

  private BigDecimal normalizedGpa(StudentProfile profile) {
    if (!Objects.equals(profile.getGpaScale(), "PERCENTAGE")) return profile.getGpaValue();
    return profile.getGpaValue().divide(new BigDecimal("25"), 2, RoundingMode.HALF_UP);
  }

  private String displaySchoolName(Program program) {
    return safeDefault(program.getSchoolNameCn(), program.getSchoolNameEn());
  }

  private String safeDefault(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }

  private int safeInt(Integer value) {
    return value == null ? 0 : value;
  }

  private boolean safeEquals(String left, String right) {
    return left != null && right != null && left.equalsIgnoreCase(right);
  }

  private boolean containsIgnoreCase(String src, String part) {
    if (src == null || part == null) return false;
    return src.toLowerCase().contains(part.toLowerCase());
  }

  private String toJson(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (Exception ex) {
      return "{}";
    }
  }

  @SuppressWarnings("unused")
  private List<String> parseStringList(String json) {
    try {
      return objectMapper.readValue(json, new TypeReference<>() {});
    } catch (Exception ex) {
      return List.of();
    }
  }

  private record StudentInputs(StudentProfile profile,
                               List<StudentLanguageScore> languages,
                               List<StudentTargetCountry> countries,
                               StudentBackgroundScore backgroundScore) {
  }

  private record ScoredProgram(Program program, AiDtos.AiProgramCandidate candidate) {
  }
}
