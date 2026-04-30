package com.offerbridge.backend.service.impl;

import com.offerbridge.backend.dto.AgencyDtos;
import com.offerbridge.backend.dto.RecommendationDtos;
import com.offerbridge.backend.entity.AgencyMemberProfile;
import com.offerbridge.backend.entity.AgencyOrg;
import com.offerbridge.backend.entity.AgencyTeam;
import com.offerbridge.backend.entity.StudentLanguageScore;
import com.offerbridge.backend.entity.StudentProfile;
import com.offerbridge.backend.entity.StudentTargetCountry;
import com.offerbridge.backend.entity.UserAccount;
import com.offerbridge.backend.entity.VerificationRecord;
import com.offerbridge.backend.exception.BizException;
import com.offerbridge.backend.mapper.AgencyMemberProfileMapper;
import com.offerbridge.backend.mapper.AgencyOrgMapper;
import com.offerbridge.backend.mapper.AgencyTeamMapper;
import com.offerbridge.backend.mapper.StudentLanguageScoreMapper;
import com.offerbridge.backend.mapper.StudentProfileMapper;
import com.offerbridge.backend.mapper.StudentTargetCountryMapper;
import com.offerbridge.backend.mapper.UserAccountMapper;
import com.offerbridge.backend.mapper.VerificationRecordMapper;
import com.offerbridge.backend.service.RecommendationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class RecommendationServiceImpl implements RecommendationService {
  private final UserAccountMapper userAccountMapper;
  private final StudentProfileMapper studentProfileMapper;
  private final StudentTargetCountryMapper studentTargetCountryMapper;
  private final StudentLanguageScoreMapper studentLanguageScoreMapper;
  private final AgencyTeamMapper agencyTeamMapper;
  private final AgencyMemberProfileMapper agencyMemberProfileMapper;
  private final AgencyOrgMapper agencyOrgMapper;
  private final VerificationRecordMapper verificationRecordMapper;

  public RecommendationServiceImpl(UserAccountMapper userAccountMapper,
                                   StudentProfileMapper studentProfileMapper,
                                   StudentTargetCountryMapper studentTargetCountryMapper,
                                   StudentLanguageScoreMapper studentLanguageScoreMapper,
                                   AgencyTeamMapper agencyTeamMapper,
                                   AgencyMemberProfileMapper agencyMemberProfileMapper,
                                   AgencyOrgMapper agencyOrgMapper,
                                   VerificationRecordMapper verificationRecordMapper) {
    this.userAccountMapper = userAccountMapper;
    this.studentProfileMapper = studentProfileMapper;
    this.studentTargetCountryMapper = studentTargetCountryMapper;
    this.studentLanguageScoreMapper = studentLanguageScoreMapper;
    this.agencyTeamMapper = agencyTeamMapper;
    this.agencyMemberProfileMapper = agencyMemberProfileMapper;
    this.agencyOrgMapper = agencyOrgMapper;
    this.verificationRecordMapper = verificationRecordMapper;
  }

  @Override
  public List<RecommendationDtos.StudentAgencyTeamRecommendationItem> listStudentAgencyTeamRecommendations(Long userId) {
    UserAccount user = requireActiveUser(userId);
    if (!"STUDENT".equals(user.getRole())) {
      throw new BizException("BIZ_FORBIDDEN", "仅学生可查看中介推荐");
    }

    StudentProfile profile = studentProfileMapper.findByUserId(userId);
    List<StudentTargetCountry> countries = studentTargetCountryMapper.listByUserId(userId);
    List<StudentLanguageScore> languageScores = studentLanguageScoreMapper.listByUserId(userId);
    if (profile == null || countries.isEmpty() || isBlank(profile.getTargetMajorText())) {
      return List.of();
    }

    List<RecommendationDtos.StudentAgencyTeamRecommendationItem> scored = new ArrayList<>();
    for (RecommendationDtos.StudentAgencyTeamRecommendationCandidate candidate : agencyTeamMapper.listStudentAgencyTeamRecommendationCandidates()) {
      RecommendationDtos.StudentAgencyTeamRecommendationItem item = toStudentAgencyItem(candidate);
      applyStudentAgencyScore(item, candidate, profile, countries, languageScores);
      scored.add(item);
    }
    scored.sort(Comparator
      .comparing(RecommendationDtos.StudentAgencyTeamRecommendationItem::getRecommendScore, Comparator.nullsLast(Comparator.reverseOrder()))
      .thenComparing(RecommendationDtos.StudentAgencyTeamRecommendationItem::getTeamId, Comparator.nullsLast(Comparator.reverseOrder())));
    return scored.stream().limit(30).toList();
  }

  @Override
  public AgencyDtos.PagedResult<RecommendationDtos.AgencyTeamStudentRecommendationItem> listAgencyTeamStudentRecommendations(Long userId,
                                                                                                                              Long teamId,
                                                                                                                              int page,
                                                                                                                              int pageSize) {
    UserAccount user = requireActiveUser(userId);
    if (!"AGENT_MEMBER".equals(user.getRole())) {
      throw new BizException("BIZ_FORBIDDEN", "仅中介成员可查看学生推荐");
    }
    AgencyMemberProfile member = agencyMemberProfileMapper.findByUserId(userId);
    if (member == null || !"ACTIVE".equals(member.getStatus())) {
      throw new BizException("BIZ_FORBIDDEN", "中介成员档案不可用");
    }
    AgencyOrg org = agencyOrgMapper.findById(member.getOrgId());
    if (org == null || !"APPROVED".equals(org.getVerificationStatus())) {
      throw new BizException("BIZ_FORBIDDEN", "当前机构尚未通过认证");
    }
    VerificationRecord cert = verificationRecordMapper.findOne(userId, "AGENT_MEMBER_CERT");
    if (cert == null || !"APPROVED".equals(cert.getStatus())) {
      throw new BizException("BIZ_FORBIDDEN", "员工认证未通过，暂不可执行该操作");
    }
    AgencyTeam team = agencyTeamMapper.findById(teamId);
    if (team == null || !member.getOrgId().equals(team.getOrgId()) || !"PUBLISHED".equals(team.getPublishStatus())) {
      throw new BizException("BIZ_NOT_FOUND", "请选择本机构已发布套餐");
    }

    List<RecommendationDtos.AgencyTeamStudentRecommendationItem> scored = new ArrayList<>();
    for (RecommendationDtos.AgencyTeamStudentRecommendationCandidate candidate : studentProfileMapper.listAgencyTeamStudentRecommendationCandidates()) {
      RecommendationDtos.AgencyTeamStudentRecommendationItem item = toAgencyTeamStudentItem(candidate);
      applyAgencyTeamStudentScore(item, candidate, team);
      scored.add(item);
    }
    scored.sort(Comparator
      .comparing(RecommendationDtos.AgencyTeamStudentRecommendationItem::getRecommendScore, Comparator.nullsLast(Comparator.reverseOrder()))
      .thenComparing(RecommendationDtos.AgencyTeamStudentRecommendationItem::getStudentUserId, Comparator.nullsLast(Comparator.reverseOrder())));

    int safePage = Math.max(1, page);
    int safePageSize = Math.max(1, Math.min(pageSize, 50));
    int from = (safePage - 1) * safePageSize;
    int to = Math.min(from + safePageSize, scored.size());
    AgencyDtos.PagedResult<RecommendationDtos.AgencyTeamStudentRecommendationItem> result = new AgencyDtos.PagedResult<>();
    result.setRecords(from >= scored.size() ? List.of() : scored.subList(from, to));
    result.setTotal(scored.size());
    result.setPage(safePage);
    result.setPageSize(safePageSize);
    return result;
  }

  private void applyStudentAgencyScore(RecommendationDtos.StudentAgencyTeamRecommendationItem item,
                                       RecommendationDtos.StudentAgencyTeamRecommendationCandidate candidate,
                                       StudentProfile profile,
                                       List<StudentTargetCountry> countries,
                                       List<StudentLanguageScore> languageScores) {
    int score = 0;
    List<String> reasons = new ArrayList<>();
    LinkedHashSet<String> tags = new LinkedHashSet<>();

    List<String> countryNames = countries.stream().map(StudentTargetCountry::getCountryName).filter(this::notBlank).toList();
    String matchedCountry = firstMatchedToken(countryNames, candidate.getServiceCountryScope());
    if (matchedCountry != null) {
      score += 30;
      reasons.add("目标国家匹配：" + matchedCountry);
      tags.add(matchedCountry);
    }

    String matchedDirection = firstMatchedDirection(profile.getMajor(), profile.getTargetMajorText(), candidate.getServiceMajorScope());
    if (matchedDirection != null) {
      score += 25;
      reasons.add("专业方向匹配：" + matchedDirection);
      tags.add(matchedDirection);
    }

    int budgetScore = budgetOverlapScore(profile.getBudgetMin(), profile.getBudgetMax(), candidate.getPriceMin(), candidate.getPriceMax());
    score += budgetScore;
    if (budgetScore >= 20) {
      reasons.add("预算与套餐价格区间匹配");
      tags.add("预算匹配");
    } else if (budgetScore > 0) {
      reasons.add("价格可进一步沟通");
    }

    if (!languageScores.isEmpty()) {
      score += 10;
      reasons.add("语言成绩已填写，可直接进入申请评估");
      tags.add("语言已完善");
    }

    int qualityScore = qualityScore(candidate.getAvgRating(), candidate.getCaseCount(), candidate.getSuccessRate());
    score += qualityScore;
    if (qualityScore >= 10) {
      reasons.add("团队评分与案例表现较好");
      tags.add("评分表现");
    }

    item.setRecommendScore(Math.min(score, 100));
    item.setMatchLevel(matchLevel(item.getRecommendScore()));
    item.setMatchReasons(defaultReasons(reasons));
    item.setMatchedTags(new ArrayList<>(tags));
  }

  private void applyAgencyTeamStudentScore(RecommendationDtos.AgencyTeamStudentRecommendationItem item,
                                           RecommendationDtos.AgencyTeamStudentRecommendationCandidate candidate,
                                           AgencyTeam team) {
    int score = 0;
    List<String> reasons = new ArrayList<>();
    LinkedHashSet<String> tags = new LinkedHashSet<>();

    String matchedCountry = firstMatchedToken(splitTokens(candidate.getTargetCountries()), team.getServiceCountryScope());
    if (matchedCountry != null) {
      score += 35;
      reasons.add("目标国家匹配：" + matchedCountry);
      tags.add(matchedCountry);
    }

    String matchedDirection = firstMatchedDirection(candidate.getMajor(), candidate.getTargetMajorText(), team.getServiceMajorScope());
    if (matchedDirection != null) {
      score += 30;
      reasons.add("专业方向匹配：" + matchedDirection);
      tags.add(matchedDirection);
    }

    int budgetScore = budgetOverlapScore(candidate.getBudgetMin(), candidate.getBudgetMax(), team.getPriceMin(), team.getPriceMax());
    score += budgetScore;
    if (budgetScore >= 20) {
      reasons.add("预算与套餐价格区间匹配");
      tags.add("预算匹配");
    } else if (budgetScore > 0) {
      reasons.add("预算或套餐价格可进一步沟通");
    }

    int readiness = 0;
    if (notBlank(candidate.getLanguageSummary())) readiness += 8;
    if (candidate.getGpaValue() != null) readiness += 4;
    if (notBlank(candidate.getSchoolName()) && notBlank(candidate.getTargetMajorText())) readiness += 3;
    score += readiness;
    if (readiness >= 10) {
      reasons.add("学生资料和成绩信息较完整");
      tags.add("资料完整");
    } else if (readiness > 0) {
      reasons.add("学生已填写部分申请背景");
    }

    item.setRecommendScore(Math.min(score, 100));
    item.setMatchLevel(matchLevel(item.getRecommendScore()));
    item.setMatchReasons(defaultReasons(reasons));
    item.setMatchedTags(new ArrayList<>(tags));
  }

  private RecommendationDtos.StudentAgencyTeamRecommendationItem toStudentAgencyItem(RecommendationDtos.StudentAgencyTeamRecommendationCandidate candidate) {
    RecommendationDtos.StudentAgencyTeamRecommendationItem item = new RecommendationDtos.StudentAgencyTeamRecommendationItem();
    item.setTeamId(candidate.getTeamId());
    item.setTeamName(candidate.getTeamName());
    item.setTeamIntro(candidate.getTeamIntro());
    item.setOrgName(candidate.getOrgName());
    item.setCity(candidate.getCity());
    item.setServiceCountryScope(candidate.getServiceCountryScope());
    item.setServiceMajorScope(candidate.getServiceMajorScope());
    item.setCaseCount(candidate.getCaseCount());
    item.setSuccessRate(candidate.getSuccessRate());
    item.setAvgRating(candidate.getAvgRating());
    item.setResponseEfficiencyScore(candidate.getResponseEfficiencyScore());
    item.setPriceTextPlaceholder(candidate.getPriceTextPlaceholder());
    return item;
  }

  private RecommendationDtos.AgencyTeamStudentRecommendationItem toAgencyTeamStudentItem(RecommendationDtos.AgencyTeamStudentRecommendationCandidate candidate) {
    RecommendationDtos.AgencyTeamStudentRecommendationItem item = new RecommendationDtos.AgencyTeamStudentRecommendationItem();
    item.setStudentUserId(candidate.getStudentUserId());
    item.setDisplayName(candidate.getDisplayName());
    item.setEducationLevel(candidate.getEducationLevel());
    item.setSchoolName(candidate.getSchoolName());
    item.setMajor(candidate.getMajor());
    item.setGpaValue(candidate.getGpaValue());
    item.setGpaScale(candidate.getGpaScale());
    item.setLanguageSummary(candidate.getLanguageSummary());
    item.setTargetCountries(candidate.getTargetCountries());
    item.setTargetMajorText(candidate.getTargetMajorText());
    item.setIntakeTerm(candidate.getIntakeTerm());
    return item;
  }

  private UserAccount requireActiveUser(Long userId) {
    UserAccount user = userAccountMapper.findById(userId);
    if (user == null || !"ACTIVE".equals(user.getStatus())) {
      throw new BizException("BIZ_UNAUTHORIZED", "账号不可用");
    }
    return user;
  }

  private int qualityScore(BigDecimal rating, Integer caseCount, BigDecimal successRate) {
    int score = 0;
    double ratingValue = rating == null ? 0 : rating.doubleValue();
    int cases = caseCount == null ? 0 : caseCount;
    double success = successRate == null ? 0 : successRate.doubleValue();
    if (ratingValue >= 4.5) score += 7;
    else if (ratingValue >= 4.0) score += 5;
    else if (ratingValue > 0) score += 3;
    if (cases >= 20) score += 4;
    else if (cases >= 5) score += 3;
    else if (cases > 0) score += 1;
    if (success >= 80) score += 4;
    else if (success >= 60) score += 2;
    return Math.min(score, 15);
  }

  private int budgetOverlapScore(BigDecimal userMin, BigDecimal userMax, BigDecimal priceMin, BigDecimal priceMax) {
    if (isZeroOrNull(priceMin) && isZeroOrNull(priceMax)) {
      return 8;
    }
    if (userMin == null && userMax == null) {
      return 0;
    }
    BigDecimal low = userMin != null ? userMin : userMax;
    BigDecimal high = userMax != null ? userMax : userMin;
    BigDecimal teamLow = priceMin != null ? priceMin : priceMax;
    BigDecimal teamHigh = priceMax != null ? priceMax : priceMin;
    if (low == null || high == null || teamLow == null || teamHigh == null) return 0;
    if (low.compareTo(teamHigh) <= 0 && high.compareTo(teamLow) >= 0) return 20;
    BigDecimal gap = low.compareTo(teamHigh) > 0 ? low.subtract(teamHigh) : teamLow.subtract(high);
    return gap.compareTo(BigDecimal.valueOf(5000)) <= 0 ? 10 : 0;
  }

  private boolean isZeroOrNull(BigDecimal value) {
    return value == null || value.compareTo(BigDecimal.ZERO) <= 0;
  }

  private String firstMatchedDirection(String currentMajor, String targetMajor, String serviceMajorScope) {
    Set<String> studentTokens = new LinkedHashSet<>();
    studentTokens.addAll(splitTokens(currentMajor));
    studentTokens.addAll(splitTokens(targetMajor));
    List<String> broadDirections = List.of("工科", "商科", "理科", "文社科");
    for (String direction : broadDirections) {
      if (containsDirection(currentMajor, direction) || containsDirection(targetMajor, direction)) {
        studentTokens.add(direction);
      }
    }
    return firstMatchedToken(new ArrayList<>(studentTokens), serviceMajorScope);
  }

  private boolean containsDirection(String text, String direction) {
    if (isBlank(text)) return false;
    return switch (direction) {
      case "工科" -> text.matches(".*(工程|工科|计算机|软件|数据|人工智能|机械|电子|电气|材料|土木|自动化).*");
      case "商科" -> text.matches(".*(商科|金融|会计|管理|经济|市场|商业|运营|供应链|MBA|BA|商业分析).*");
      case "理科" -> text.matches(".*(理科|数学|物理|化学|生物|统计|环境|地理|科学).*");
      case "文社科" -> text.matches(".*(文社科|传媒|教育|法学|法律|社会|心理|政治|公共|语言|文学|历史|艺术|设计).*");
      default -> false;
    };
  }

  private String firstMatchedToken(List<String> preferredTokens, String targetText) {
    if (isBlank(targetText)) return null;
    for (String token : preferredTokens) {
      if (notBlank(token) && targetText.contains(token)) return token;
      if ("澳洲".equals(token) && targetText.contains("澳大利亚")) return token;
      if ("澳大利亚".equals(token) && targetText.contains("澳洲")) return token;
      if ("中国香港".equals(token) && targetText.contains("香港")) return token;
      if ("香港".equals(token) && targetText.contains("中国香港")) return token;
    }
    return null;
  }

  private List<String> splitTokens(String value) {
    if (isBlank(value)) return List.of();
    List<String> tokens = new ArrayList<>();
    for (String part : value.split("[,，、/\\s]+")) {
      if (notBlank(part)) tokens.add(part.trim());
    }
    return tokens;
  }

  private String matchLevel(int score) {
    if (score >= 80) return "HIGH";
    if (score >= 60) return "MEDIUM";
    return "LOW";
  }

  private List<String> defaultReasons(List<String> reasons) {
    if (!reasons.isEmpty()) return reasons;
    return List.of("可作为备选对象进一步沟通确认");
  }

  private boolean notBlank(String value) {
    return !isBlank(value);
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
