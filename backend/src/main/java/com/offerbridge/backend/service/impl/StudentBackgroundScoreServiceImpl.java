package com.offerbridge.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.offerbridge.backend.entity.SchoolTierDictionary;
import com.offerbridge.backend.entity.StudentBackgroundScore;
import com.offerbridge.backend.entity.StudentCompetitionExperience;
import com.offerbridge.backend.entity.StudentExchangeExperience;
import com.offerbridge.backend.entity.StudentLanguageScore;
import com.offerbridge.backend.entity.StudentProfile;
import com.offerbridge.backend.entity.StudentPublication;
import com.offerbridge.backend.entity.StudentResearchExperience;
import com.offerbridge.backend.entity.StudentWorkExperience;
import com.offerbridge.backend.exception.BizException;
import com.offerbridge.backend.mapper.SchoolTierDictionaryMapper;
import com.offerbridge.backend.mapper.StudentBackgroundScoreMapper;
import com.offerbridge.backend.mapper.StudentCompetitionExperienceMapper;
import com.offerbridge.backend.mapper.StudentExchangeExperienceMapper;
import com.offerbridge.backend.mapper.StudentLanguageScoreMapper;
import com.offerbridge.backend.mapper.StudentProfileMapper;
import com.offerbridge.backend.mapper.StudentPublicationMapper;
import com.offerbridge.backend.mapper.StudentResearchExperienceMapper;
import com.offerbridge.backend.mapper.StudentWorkExperienceMapper;
import com.offerbridge.backend.service.StudentBackgroundScoreService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class StudentBackgroundScoreServiceImpl implements StudentBackgroundScoreService {
  private static final BigDecimal GPA_WEIGHT = new BigDecimal("0.35");
  private static final BigDecimal LANGUAGE_WEIGHT = new BigDecimal("0.20");
  private static final BigDecimal PUBLICATION_WEIGHT = new BigDecimal("0.15");
  private static final BigDecimal RESEARCH_WEIGHT = new BigDecimal("0.10");
  private static final BigDecimal INTERNSHIP_WEIGHT = new BigDecimal("0.08");
  private static final BigDecimal EXCHANGE_WEIGHT = new BigDecimal("0.05");
  private static final BigDecimal COMPETITION_WEIGHT = new BigDecimal("0.04");
  private static final BigDecimal UNDERGRAD_WEIGHT = new BigDecimal("0.03");

  private final StudentProfileMapper studentProfileMapper;
  private final StudentLanguageScoreMapper studentLanguageScoreMapper;
  private final StudentPublicationMapper studentPublicationMapper;
  private final StudentResearchExperienceMapper studentResearchExperienceMapper;
  private final StudentWorkExperienceMapper studentWorkExperienceMapper;
  private final StudentExchangeExperienceMapper studentExchangeExperienceMapper;
  private final StudentCompetitionExperienceMapper studentCompetitionExperienceMapper;
  private final StudentBackgroundScoreMapper studentBackgroundScoreMapper;
  private final SchoolTierDictionaryMapper schoolTierDictionaryMapper;
  private final ObjectMapper objectMapper;

  public StudentBackgroundScoreServiceImpl(StudentProfileMapper studentProfileMapper,
                                           StudentLanguageScoreMapper studentLanguageScoreMapper,
                                           StudentPublicationMapper studentPublicationMapper,
                                           StudentResearchExperienceMapper studentResearchExperienceMapper,
                                           StudentWorkExperienceMapper studentWorkExperienceMapper,
                                           StudentExchangeExperienceMapper studentExchangeExperienceMapper,
                                           StudentCompetitionExperienceMapper studentCompetitionExperienceMapper,
                                           StudentBackgroundScoreMapper studentBackgroundScoreMapper,
                                           SchoolTierDictionaryMapper schoolTierDictionaryMapper,
                                           ObjectMapper objectMapper) {
    this.studentProfileMapper = studentProfileMapper;
    this.studentLanguageScoreMapper = studentLanguageScoreMapper;
    this.studentPublicationMapper = studentPublicationMapper;
    this.studentResearchExperienceMapper = studentResearchExperienceMapper;
    this.studentWorkExperienceMapper = studentWorkExperienceMapper;
    this.studentExchangeExperienceMapper = studentExchangeExperienceMapper;
    this.studentCompetitionExperienceMapper = studentCompetitionExperienceMapper;
    this.studentBackgroundScoreMapper = studentBackgroundScoreMapper;
    this.schoolTierDictionaryMapper = schoolTierDictionaryMapper;
    this.objectMapper = objectMapper;
  }

  @Override
  public StudentBackgroundScore getOrRefreshScore(Long userId) {
    StudentBackgroundScore existing = studentBackgroundScoreMapper.findByUserId(userId);
    return existing == null ? refreshScore(userId) : existing;
  }

  @Override
  public StudentBackgroundScore refreshScore(Long userId) {
    StudentProfile profile = studentProfileMapper.findByUserId(userId);
    if (profile == null) {
      throw new BizException("BIZ_NOT_FOUND", "学生画像不存在");
    }

    List<StudentLanguageScore> languages = studentLanguageScoreMapper.listByUserId(userId);
    List<StudentPublication> publications = studentPublicationMapper.listByUserId(userId);
    List<StudentResearchExperience> researches = studentResearchExperienceMapper.listByUserId(userId);
    List<StudentWorkExperience> works = studentWorkExperienceMapper.listByUserId(userId);
    StudentExchangeExperience exchange = studentExchangeExperienceMapper.findByUserId(userId);
    List<StudentCompetitionExperience> competitions = studentCompetitionExperienceMapper.listByUserId(userId);

    BigDecimal gpaScore = bd(scoreGpa(profile));
    BigDecimal languageScore = bd(scoreLanguage(languages));
    BigDecimal publicationScore = bd(scorePublications(publications));
    BigDecimal researchScore = bd(scoreResearches(researches));
    BigDecimal internshipScore = bd(scoreWorks(works));
    BigDecimal exchangeScore = bd(scoreExchange(exchange));
    BigDecimal competitionScore = bd(scoreCompetitions(competitions));
    SchoolTier schoolTier = resolveSchoolTier(profile);
    BigDecimal undergraduateSchoolScore = bd(schoolTier.score());

    BigDecimal overall = gpaScore.multiply(GPA_WEIGHT)
      .add(languageScore.multiply(LANGUAGE_WEIGHT))
      .add(publicationScore.multiply(PUBLICATION_WEIGHT))
      .add(researchScore.multiply(RESEARCH_WEIGHT))
      .add(internshipScore.multiply(INTERNSHIP_WEIGHT))
      .add(exchangeScore.multiply(EXCHANGE_WEIGHT))
      .add(competitionScore.multiply(COMPETITION_WEIGHT))
      .add(undergraduateSchoolScore.multiply(UNDERGRAD_WEIGHT))
      .setScale(2, RoundingMode.HALF_UP);

    StudentBackgroundScore score = new StudentBackgroundScore();
    score.setUserId(userId);
    score.setGpaScore(gpaScore);
    score.setLanguageScore(languageScore);
    score.setPublicationScore(publicationScore);
    score.setResearchScore(researchScore);
    score.setInternshipScore(internshipScore);
    score.setExchangeScore(exchangeScore);
    score.setCompetitionScore(competitionScore);
    score.setUndergraduateSchoolScore(undergraduateSchoolScore);
    score.setOverallAcademicScore(overall);
    score.setScoreVersion("BACKGROUND_SCORE_V1");
    score.setScoreDetailJson(toJson(buildDetail(profile, schoolTier, publications, researches, works, exchange, competitions)));

    studentBackgroundScoreMapper.upsert(score);
    return studentBackgroundScoreMapper.findByUserId(userId);
  }

  private Map<String, Object> buildDetail(StudentProfile profile,
                                          SchoolTier schoolTier,
                                          List<StudentPublication> publications,
                                          List<StudentResearchExperience> researches,
                                          List<StudentWorkExperience> works,
                                          StudentExchangeExperience exchange,
                                          List<StudentCompetitionExperience> competitions) {
    Map<String, Object> detail = new LinkedHashMap<>();
    detail.put("weights", Map.of(
      "gpa", 35,
      "language", 20,
      "publication", 15,
      "research", 10,
      "internship", 8,
      "exchange", 5,
      "competition", 4,
      "undergraduateSchool", 3
    ));
    detail.put("undergraduateSchoolTier", schoolTier.code());
    detail.put("undergraduateSchoolSource", schoolTier.source());
    detail.put("gpaScale", profile.getGpaScale());
    detail.put("publicationCount", publications.size());
    detail.put("researchCount", researches.size());
    detail.put("internshipCount", works.size());
    detail.put("hasExchange", exchange != null);
    detail.put("competitionCount", competitions.size());
    return detail;
  }

  private double scoreGpa(StudentProfile profile) {
    if (profile.getGpaValue() == null || profile.getGpaScale() == null) return 0;
    double normalized = switch (profile.getGpaScale()) {
      case "FOUR_POINT" -> profile.getGpaValue().doubleValue() / 4.0 * 100.0;
      case "PERCENTAGE" -> profile.getGpaValue().doubleValue();
      default -> 0;
    };
    if (profile.getRankValue() == null) return clamp(normalized);
    double rankScore = clamp(101 - profile.getRankValue());
    return clamp(normalized * 0.85 + rankScore * 0.15);
  }

  private double scoreLanguage(List<StudentLanguageScore> languages) {
    return languages.stream()
      .mapToDouble(this::scoreSingleLanguage)
      .max()
      .orElse(0);
  }

  private double scoreSingleLanguage(StudentLanguageScore item) {
    if (item.getScore() == null || item.getLanguageType() == null) return 0;
    double value = item.getScore().doubleValue();
    return switch (item.getLanguageType()) {
      case "IELTS" -> clamp(value / 9.0 * 100.0);
      case "TOEFL" -> clamp(value / 120.0 * 100.0);
      case "PTE" -> clamp((value - 10.0) / 80.0 * 100.0);
      case "CET4", "CET6" -> clamp(value / 710.0 * 100.0);
      default -> 0;
    };
  }

  private double scorePublications(List<StudentPublication> publications) {
    if (publications.isEmpty()) return 0;
    List<Double> scores = publications.stream()
      .map(this::scorePublication)
      .sorted(Comparator.reverseOrder())
      .toList();
    double best = scores.get(0);
    double other = scores.stream().skip(1).mapToDouble(Double::doubleValue).sum();
    double otherSupplement = Math.min(100, other * 0.35);
    return clamp(best * 0.7 + otherSupplement * 0.3);
  }

  private double scorePublication(StudentPublication pub) {
    String joined = join(pub.getPublicationLevel(), pub.getJournalPartition(), pub.getIndexedInfo(), pub.getJournalName(), pub.getAuthorRole(), pub.getAuthorOrder());
    boolean firstAuthor = containsAny(joined, "一作", "第一作者", "first author", "first-author", "first_author");
    boolean secondAuthor = containsAny(joined, "二作", "第二作者", "second author", "second-author", "second_author");
    boolean submitted = containsAny(joined, "在投", "投稿", "submitted", "under review");

    if (submitted) return 15;
    if (containsAny(joined, "sci一区", "sci 1", "q1", "一区", "顶会", "top conference", "top_conference")) return firstAuthor ? 100 : secondAuthor ? 55 : 25;
    if (containsAny(joined, "sci二区", "sci 2", "q2", "二区", "高水平会议")) return firstAuthor ? 85 : secondAuthor ? 55 : 25;
    if (containsAny(joined, "sci三区", "sci 3", "q3", "三区", "普通会议")) return firstAuthor ? 70 : secondAuthor ? 40 : 25;
    if (containsAny(joined, "sci四区", "sci 4", "q4", "四区", "普通期刊")) return firstAuthor ? 60 : secondAuthor ? 40 : 25;
    if (containsAny(joined, "sci", "ei", "cssci", "核心")) return firstAuthor ? 70 : secondAuthor ? 45 : 25;
    if (firstAuthor) return 55;
    if (secondAuthor) return 35;
    return 25;
  }

  private double scoreResearches(List<StudentResearchExperience> researches) {
    if (researches.isEmpty()) return 0;
    return scoreTopWithSupplement(researches.stream().map(this::scoreResearch).toList());
  }

  private double scoreResearch(StudentResearchExperience research) {
    double relevance = levelScore(research.getRelevanceLevel(), 100, 65, 35, 10, inferRelevance(join(research.getProjectName(), research.getContentSummary())));
    double role = levelScore(research.getRoleLevel(), 100, 75, 50, 30, inferResearchRole(join(research.getRoleName(), research.getContentSummary())));
    double duration = durationScore(resolveDuration(research.getDurationMonths(), research.getStartDate(), research.getEndDate()), 12);
    double outcome = Boolean.TRUE.equals(research.getHasPublication()) ? 100 : 35;
    return clamp(relevance * 0.4 + role * 0.3 + duration * 0.15 + outcome * 0.15);
  }

  private double scoreWorks(List<StudentWorkExperience> works) {
    if (works.isEmpty()) return 0;
    return scoreTopWithSupplement(works.stream().map(this::scoreWork).toList());
  }

  private double scoreWork(StudentWorkExperience work) {
    double company = companyTierScore(work.getCompanyTier(), work.getCompanyName());
    double relevance = levelScore(work.getRelevanceLevel(), 100, 65, 35, 10, inferRelevance(join(work.getPositionName(), work.getKeywords(), work.getContentSummary())));
    double title = titleScore(work.getTitleLevel(), join(work.getPositionName(), work.getContentSummary()));
    double duration = durationScore(resolveDuration(work.getDurationMonths(), work.getStartDate(), work.getEndDate()), 6);
    return clamp(company * 0.35 + relevance * 0.4 + title * 0.15 + duration * 0.1);
  }

  private double scoreExchange(StudentExchangeExperience exchange) {
    if (exchange == null) return 0;
    double school = schoolTierScore(exchange.getSchoolTier(), exchange.getUniversityName()).score();
    double gpa = exchange.getGpaValue() == null ? 0 : clamp(exchange.getGpaValue().doubleValue() / 4.0 * 100.0);
    double relevance = levelScore(exchange.getRelevanceLevel(), 100, 65, 35, 10, inferRelevance(exchange.getMajorCourses()));
    double duration = durationScore(resolveDuration(exchange.getDurationMonths(), exchange.getStartDate(), exchange.getEndDate()), 6);
    return clamp(school * 0.4 + gpa * 0.35 + relevance * 0.15 + duration * 0.1);
  }

  private double scoreCompetitions(List<StudentCompetitionExperience> competitions) {
    if (competitions.isEmpty()) return 0;
    return scoreTopWithSupplement(competitions.stream().map(this::scoreCompetition).toList());
  }

  private double scoreCompetition(StudentCompetitionExperience competition) {
    double level = competitionLevelScore(competition.getCompetitionLevel(), competition.getCompetitionName());
    double award = awardLevelScore(competition.getAwardLevel(), competition.getAward());
    double relevance = levelScore(competition.getRelevanceLevel(), 100, 65, 35, 10, inferRelevance(join(competition.getCompetitionName(), competition.getRoleDesc())));
    return clamp(level * 0.4 + award * 0.4 + relevance * 0.2);
  }

  private double scoreTopWithSupplement(List<Double> scores) {
    List<Double> sorted = scores.stream().sorted(Comparator.reverseOrder()).toList();
    double best = sorted.get(0);
    double other = sorted.stream().skip(1).mapToDouble(Double::doubleValue).sum();
    return clamp(best * 0.75 + Math.min(100, other * 0.4) * 0.25);
  }

  private SchoolTier resolveSchoolTier(StudentProfile profile) {
    if (profile.getUndergraduateSchoolTier() != null && !profile.getUndergraduateSchoolTier().isBlank()) {
      return schoolTierScore(profile.getUndergraduateSchoolTier(), profile.getSchoolName()).withSource("manual");
    }
    SchoolTierDictionary item = null;
    if (profile.getSchoolName() != null && !profile.getSchoolName().isBlank()) {
      item = schoolTierDictionaryMapper.findBySchoolName(profile.getSchoolName().trim());
    }
    if (item == null) return new SchoolTier("UNKNOWN", 50, "fallback");
    return new SchoolTier(item.getTierCode(), item.getTierScore().doubleValue(), "dictionary");
  }

  private SchoolTier schoolTierScore(String tierOrName, String name) {
    String text = join(tierOrName, name);
    if (containsAny(text, "top_30", "c9", "清华", "北大", "北京大学", "复旦", "上海交通", "浙大", "浙江大学", "中科大", "南京大学", "哈工大", "西安交通")) {
      return new SchoolTier("C9", 100, "inferred");
    }
    if (containsAny(text, "985")) return new SchoolTier("985", 90, "inferred");
    if (containsAny(text, "top_100", "211", "双一流", "double_first_class")) return new SchoolTier("211", 80, "inferred");
    if (containsAny(text, "海外", "overseas", "foreign")) return new SchoolTier("OVERSEAS", 75, "inferred");
    if (containsAny(text, "大专", "专科", "junior_college", "college")) return new SchoolTier("JUNIOR_COLLEGE", 35, "inferred");
    if (containsAny(text, "普通本科", "双非", "normal_undergrad", "undergrad")) return new SchoolTier("NORMAL_UNDERGRAD", 60, "inferred");
    return new SchoolTier("UNKNOWN", 50, "fallback");
  }

  private double companyTierScore(String tier, String companyName) {
    String text = join(tier, companyName);
    if (containsAny(text, "top", "tier1", "头部", "大厂", "google", "microsoft", "amazon", "meta", "apple", "字节", "腾讯", "阿里", "华为", "百度", "美团", "京东", "商汤", "旷视", "openai")) return 100;
    if (containsAny(text, "known", "知名", "上市", "银行", "证券", "咨询", "四大", "big four", "deloitte", "pwc", "kpmg", "ey")) return 80;
    if (containsAny(text, "small", "startup", "小型", "校内")) return 35;
    if (text.isBlank()) return 45;
    return 55;
  }

  private double titleScore(String titleLevel, String text) {
    String joined = join(titleLevel, text);
    if (containsAny(joined, "核心", "owner", "lead", "负责人", "主导", "算法", "研发", "投研", "咨询", "数据分析", "后端", "前端")) return 90;
    if (containsAny(joined, "助理", "assistant", "support", "运营", "整理", "录入")) return 45;
    if (joined.isBlank()) return 50;
    return 65;
  }

  private double competitionLevelScore(String level, String name) {
    String text = join(level, name);
    if (containsAny(text, "国际", "international", "world")) return 100;
    if (containsAny(text, "国家", "全国", "national")) return 85;
    if (containsAny(text, "省", "省部", "regional", "provincial")) return 65;
    if (containsAny(text, "校", "school", "university")) return 40;
    return text.isBlank() ? 15 : 45;
  }

  private double awardLevelScore(String level, String award) {
    String text = join(level, award);
    if (containsAny(text, "一等奖", "冠军", "金奖", "first", "gold", "winner")) return 100;
    if (containsAny(text, "二等奖", "银奖", "second", "silver")) return 80;
    if (containsAny(text, "三等奖", "铜奖", "third", "bronze")) return 60;
    if (containsAny(text, "优胜", "入围", "finalist", "honorable")) return 35;
    if (containsAny(text, "参与", "participant")) return 15;
    return text.isBlank() ? 15 : 45;
  }

  private double levelScore(String level, double high, double medium, double low, double none, double inferred) {
    String text = normalize(level);
    if (text.isBlank()) return inferred;
    if (containsAny(text, "高", "high", "strong", "core", "高度", "核心")) return high;
    if (containsAny(text, "中", "medium", "partial", "major", "部分", "主要")) return medium;
    if (containsAny(text, "低", "low", "weak", "participant", "course", "弱", "普通", "课程")) return low;
    if (containsAny(text, "无", "none", "not")) return none;
    return inferred;
  }

  private double inferResearchRole(String text) {
    if (containsAny(text, "负责人", "主导", "核心", "leader", "lead")) return 100;
    if (containsAny(text, "主要", "重要", "main", "major")) return 75;
    if (containsAny(text, "参与", "participant", "member")) return 50;
    if (containsAny(text, "课程", "短期", "course")) return 30;
    return text.isBlank() ? 30 : 50;
  }

  private double inferRelevance(String text) {
    if (text == null || text.isBlank()) return 35;
    if (containsAny(text, "相关", "算法", "机器学习", "人工智能", "数据", "软件", "金融", "投研", "商业分析", "管理", "工程", "目标方向")) return 100;
    if (containsAny(text, "部分", "辅助", "跨学科")) return 65;
    return 35;
  }

  private int resolveDuration(Integer explicitMonths, String start, String end) {
    if (explicitMonths != null && explicitMonths > 0) return explicitMonths;
    YearMonth startMonth = parseYearMonth(start);
    YearMonth endMonth = parseYearMonth(end);
    if (startMonth == null || endMonth == null || endMonth.isBefore(startMonth)) return 0;
    return (endMonth.getYear() - startMonth.getYear()) * 12 + endMonth.getMonthValue() - startMonth.getMonthValue() + 1;
  }

  private YearMonth parseYearMonth(String value) {
    if (value == null || value.isBlank()) return null;
    String text = value.trim();
    List<DateTimeFormatter> formatters = List.of(
      DateTimeFormatter.ofPattern("yyyy-MM"),
      DateTimeFormatter.ofPattern("yyyy/MM"),
      DateTimeFormatter.ofPattern("yyyy.M")
    );
    for (DateTimeFormatter formatter : formatters) {
      try {
        return YearMonth.parse(text, formatter);
      } catch (DateTimeParseException ignored) {
      }
    }
    if (text.matches("\\d{4}")) {
      return YearMonth.of(Integer.parseInt(text), 1);
    }
    return null;
  }

  private double durationScore(int months, int fullScoreMonths) {
    if (months <= 0) return 30;
    return clamp(months * 100.0 / fullScoreMonths);
  }

  private String toJson(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (Exception ex) {
      return "{}";
    }
  }

  private BigDecimal bd(double value) {
    return BigDecimal.valueOf(clamp(value)).setScale(2, RoundingMode.HALF_UP);
  }

  private double clamp(double value) {
    if (Double.isNaN(value) || Double.isInfinite(value)) return 0;
    if (value < 0) return 0;
    return Math.min(value, 100);
  }

  private boolean containsAny(String value, String... needles) {
    String text = normalize(value);
    for (String needle : needles) {
      if (text.contains(needle.toLowerCase(Locale.ROOT))) return true;
    }
    return false;
  }

  private String join(String... values) {
    List<String> parts = new ArrayList<>();
    for (String value : values) {
      if (value != null && !value.isBlank()) parts.add(value);
    }
    return String.join(" ", parts);
  }

  private String normalize(String value) {
    return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
  }

  private record SchoolTier(String code, double score, String source) {
    private SchoolTier withSource(String nextSource) {
      return new SchoolTier(code, score, nextSource);
    }
  }
}
