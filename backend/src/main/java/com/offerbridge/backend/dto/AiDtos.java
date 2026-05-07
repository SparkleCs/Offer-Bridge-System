package com.offerbridge.backend.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AiDtos {
  public static class AiReportView {
    public Long reportId;
    public String status;
    public String overallSummary;
    public String generatedAt;
    public String modelProvider;
    public String modelVersion;
    public List<AiProgramRecommendationItem> recommendations = new ArrayList<>();
    public List<GapAnalysisItem> gapAnalysis = new ArrayList<>();
    public List<ImprovementSuggestionItem> improvementSuggestions = new ArrayList<>();
    public List<String> riskWarnings = new ArrayList<>();
  }

  public static class AiProgramRecommendationItem {
    public Long programId;
    public Long schoolId;
    public String schoolName;
    public String programName;
    public String countryName;
    public String directionName;
    public Integer qsRank;
    public Integer usnewsRank;
    public String rankingSource;
    public Integer primaryRank;
    public Integer ruleMatchScore;
    public Integer mlScore;
    public BigDecimal admissionProbabilityEstimate;
    public String matchTier;
    public String confidenceLevel;
    public List<String> reasonTags = new ArrayList<>();
    public String aiSummary;
  }

  public static class GapAnalysisItem {
    public String dimension;
    public String current;
    public String target;
    public String priority;
  }

  public static class ImprovementSuggestionItem {
    public String priority;
    public String action;
    public String reason;
  }

  public static class AiRecommendationRequest {
    public Map<String, Object> studentProfile;
    public List<AiProgramCandidate> programs = new ArrayList<>();
  }

  public static class AiProgramCandidate {
    public Long programId;
    public String schoolName;
    public String programName;
    public String countryName;
    public String directionName;
    public Integer qsRank;
    public Integer usnewsRank;
    public String rankingSource;
    public Integer primaryRank;
    public BigDecimal gpaMinRecommend;
    public String languageType;
    public BigDecimal languageMinScore;
    public BigDecimal tuitionAmount;
    public String backgroundPreference;
    public Integer ruleMatchScore;
    public String ruleMatchTier;
    public List<String> ruleReasonTags = new ArrayList<>();
  }

  public static class AiRecommendationResponse {
    public String status;
    public String overallSummary;
    public String generatedAt;
    public String modelProvider;
    public String modelVersion;
    public List<AiProgramRecommendationItem> recommendations = new ArrayList<>();
    public List<GapAnalysisItem> gapAnalysis = new ArrayList<>();
    public List<ImprovementSuggestionItem> improvementSuggestions = new ArrayList<>();
    public List<String> riskWarnings = new ArrayList<>();
  }
}
