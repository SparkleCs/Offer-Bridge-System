package com.offerbridge.backend.service.impl;

import com.offerbridge.backend.dto.ReviewDtos;
import com.offerbridge.backend.entity.AgencyOrg;
import com.offerbridge.backend.entity.AgencyTeam;
import com.offerbridge.backend.entity.ServiceOrder;
import com.offerbridge.backend.entity.UserAccount;
import com.offerbridge.backend.exception.BizException;
import com.offerbridge.backend.mapper.AgencyOrgMapper;
import com.offerbridge.backend.mapper.AgencyTeamMapper;
import com.offerbridge.backend.mapper.ReviewMapper;
import com.offerbridge.backend.mapper.ServiceOrderMapper;
import com.offerbridge.backend.mapper.UserAccountMapper;
import com.offerbridge.backend.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {
  private static final BigDecimal PRIOR_RATING = new BigDecimal("4.0");
  private static final BigDecimal PRIOR_COUNT = new BigDecimal("8");
  private static final BigDecimal HUNDRED = new BigDecimal("100");

  private final ReviewMapper reviewMapper;
  private final ServiceOrderMapper serviceOrderMapper;
  private final AgencyTeamMapper agencyTeamMapper;
  private final AgencyOrgMapper agencyOrgMapper;
  private final UserAccountMapper userAccountMapper;

  public ReviewServiceImpl(ReviewMapper reviewMapper,
                           ServiceOrderMapper serviceOrderMapper,
                           AgencyTeamMapper agencyTeamMapper,
                           AgencyOrgMapper agencyOrgMapper,
                           UserAccountMapper userAccountMapper) {
    this.reviewMapper = reviewMapper;
    this.serviceOrderMapper = serviceOrderMapper;
    this.agencyTeamMapper = agencyTeamMapper;
    this.agencyOrgMapper = agencyOrgMapper;
    this.userAccountMapper = userAccountMapper;
  }

  @Override
  public void snapshotOrderMembers(Long orderId, Long teamId) {
    reviewMapper.insertOrderMemberSnapshots(orderId, teamId);
  }

  @Override
  @Transactional
  public ReviewDtos.OrderReviewStatus getOrderReviewStatus(Long userId, Long orderId) {
    requireRole(userId, "STUDENT");
    ServiceOrder order = serviceOrderMapper.findById(orderId);
    if (order == null || !userId.equals(order.getStudentUserId())) {
      throw new BizException("BIZ_NOT_FOUND", "订单不存在");
    }
    if ("COMPLETED".equals(order.getOrderStatus())) {
      snapshotOrderMembers(order.getId(), order.getTeamId());
    }
    return buildOrderReviewStatus(order);
  }

  @Override
  @Transactional
  public ReviewDtos.OrderReviewStatus submitOrderReview(Long userId, Long orderId, ReviewDtos.SubmitOrderReviewRequest request) {
    requireRole(userId, "STUDENT");
    ServiceOrder order = serviceOrderMapper.findByIdForUpdate(orderId);
    if (order == null || !userId.equals(order.getStudentUserId())) {
      throw new BizException("BIZ_NOT_FOUND", "订单不存在");
    }
    if (!"COMPLETED".equals(order.getOrderStatus())) {
      throw new BizException("BIZ_BAD_REQUEST", "服务完成后才能评价");
    }
    snapshotOrderMembers(order.getId(), order.getTeamId());
    for (ReviewDtos.MemberReviewSubmitItem item : request.getReviews()) {
      if (reviewMapper.countSnapshotMember(orderId, item.getMemberId()) == 0) {
        throw new BizException("BIZ_BAD_REQUEST", "只能评价本次订单实际服务老师");
      }
      if (reviewMapper.countReviewByOrderAndMember(orderId, item.getMemberId()) > 0) {
        throw new BizException("BIZ_BAD_REQUEST", "同一老师已评价，不能重复提交");
      }
      BigDecimal overall = average(
        item.getProfessionalScore(),
        item.getCommunicationScore(),
        item.getMaterialScore(),
        item.getTransparencyScore(),
        item.getResponsibilityScore()
      );
      reviewMapper.insertMemberReview(
        orderId,
        userId,
        order.getOrgId(),
        order.getTeamId(),
        item.getMemberId(),
        scale(item.getProfessionalScore()),
        scale(item.getCommunicationScore()),
        scale(item.getMaterialScore()),
        scale(item.getTransparencyScore()),
        scale(item.getResponsibilityScore()),
        overall,
        item.getNpsScore(),
        blankToNull(item.getCommentText()),
        item.getAnonymous() == null || item.getAnonymous()
      );
    }
    refreshTeamSummary(order.getTeamId());
    return buildOrderReviewStatus(order);
  }

  @Override
  public List<ReviewDtos.RatingSummary> getTeamSummaries(List<Long> teamIds) {
    if (teamIds == null || teamIds.isEmpty()) return List.of();
    Map<Long, ReviewDtos.RatingSummary> unique = new LinkedHashMap<>();
    for (Long teamId : teamIds) {
      if (teamId != null && !unique.containsKey(teamId)) {
        unique.put(teamId, refreshTeamSummary(teamId));
      }
    }
    return new ArrayList<>(unique.values());
  }

  @Override
  public ReviewDtos.RatingSummary getTeamSummary(Long teamId) {
    return refreshTeamSummary(teamId);
  }

  @Override
  public List<ReviewDtos.TeamMemberReviewSummary> getTeamMemberSummaries(Long teamId) {
    List<ReviewDtos.TeamMemberReviewStats> rows = reviewMapper.listTeamMemberReviewStats(teamId);
    for (ReviewDtos.TeamMemberReviewStats row : rows) {
      decorateMemberStats(row);
    }
    return new ArrayList<>(rows);
  }

  @Override
  public List<ReviewDtos.MemberReviewItem> getMemberReviews(Long teamId, Long memberId) {
    return reviewMapper.listMemberReviews(teamId, memberId);
  }

  @Override
  public ReviewDtos.AgencyDashboard getAgencyDashboard(Long userId) {
    AgencyOrg org = agencyOrgMapper.findByMemberUserId(userId);
    if (org == null) {
      org = agencyOrgMapper.findByAdminUserId(userId);
    }
    if (org == null) {
      throw new BizException("BIZ_NOT_FOUND", "机构不存在");
    }
    List<Long> teamIds = reviewMapper.listOrgPublishedTeamIds(org.getId());
    List<ReviewDtos.RatingSummary> teamSummaries = getTeamSummaries(teamIds);
    ReviewDtos.RatingSummary orgSummary = mergeOrgSummary(org.getId(), teamSummaries);
    List<ReviewDtos.TeamMemberReviewStats> memberRows = reviewMapper.listOrgMemberReviewStats(org.getId());
    for (ReviewDtos.TeamMemberReviewStats row : memberRows) {
      decorateMemberStats(row);
    }
    ReviewDtos.AgencyDashboard dashboard = new ReviewDtos.AgencyDashboard();
    dashboard.setOrgSummary(orgSummary);
    dashboard.setTeamSummaries(teamSummaries);
    dashboard.setMemberRankings(new ArrayList<>(memberRows));
    return dashboard;
  }

  private ReviewDtos.OrderReviewStatus buildOrderReviewStatus(ServiceOrder order) {
    List<ReviewDtos.OrderReviewTarget> targets = reviewMapper.listOrderReviewTargets(order.getId());
    boolean canReview = "COMPLETED".equals(order.getOrderStatus()) && targets.stream().anyMatch(t -> !Boolean.TRUE.equals(t.getReviewed()));
    ReviewDtos.OrderReviewStatus status = new ReviewDtos.OrderReviewStatus();
    status.setCanReview(canReview);
    status.setReason("COMPLETED".equals(order.getOrderStatus()) ? "" : "服务完成后才能评价");
    status.setTargets(targets);
    return status;
  }

  private ReviewDtos.RatingSummary refreshTeamSummary(Long teamId) {
    AgencyTeam team = agencyTeamMapper.findById(teamId);
    if (team == null) {
      throw new BizException("BIZ_NOT_FOUND", "套餐不存在");
    }
    List<ReviewDtos.TeamMemberReviewStats> memberStats = reviewMapper.listTeamMemberReviewStats(teamId);
    BigDecimal studentReviewScore = calcTeacherGroupScore(memberStats);
    int reviewCount = memberStats.stream().mapToInt(row -> safeInt(row.getReviewCount())).sum();
    int positiveCount = memberStats.stream().mapToInt(row -> safeInt(row.getPositiveCount())).sum();
    BigDecimal positiveRate = reviewCount == 0 ? BigDecimal.ZERO : pct(positiveCount, reviewCount);
    BigDecimal offerOutcomeScore = calcOfferOutcomeScore(reviewMapper.getOfferOutcomeStats(teamId));
    BigDecimal processScore = calcProcessPerformanceScore(reviewMapper.getProcessStats(teamId));
    BigDecimal platformScore = calcPlatformTrustScore(reviewMapper.getPlatformTrustStats(teamId));
    BigDecimal totalScore = studentReviewScore.multiply(new BigDecimal("0.50"))
      .add(offerOutcomeScore.multiply(new BigDecimal("0.35")))
      .add(processScore.multiply(new BigDecimal("0.10")))
      .add(platformScore.multiply(new BigDecimal("0.05")));
    ReviewDtos.RatingSummary summary = new ReviewDtos.RatingSummary();
    summary.setTeamId(teamId);
    summary.setTotalScore(scaleScore(totalScore));
    summary.setStudentReviewScore(scaleScore(studentReviewScore));
    summary.setOfferOutcomeScore(scaleScore(offerOutcomeScore));
    summary.setProcessPerformanceScore(scaleScore(processScore));
    summary.setPlatformTrustScore(scaleScore(platformScore));
    summary.setReviewCount(reviewCount);
    summary.setPositiveRate(scaleScore(positiveRate));
    summary.setConfidenceLabel(confidenceLabel(reviewCount));
    reviewMapper.upsertRatingSummary(
      "TEAM",
      teamId,
      teamId,
      team.getOrgId(),
      null,
      summary.getTotalScore(),
      summary.getStudentReviewScore(),
      summary.getOfferOutcomeScore(),
      summary.getProcessPerformanceScore(),
      summary.getPlatformTrustScore(),
      reviewCount,
      summary.getPositiveRate(),
      summary.getConfidenceLabel()
    );
    return summary;
  }

  private BigDecimal calcTeacherGroupScore(List<ReviewDtos.TeamMemberReviewStats> memberStats) {
    BigDecimal weighted = BigDecimal.ZERO;
    BigDecimal activeWeight = BigDecimal.ZERO;
    for (ReviewDtos.TeamMemberReviewStats row : memberStats) {
      BigDecimal weight = roleWeight(row.getRoleCode());
      if (weight.compareTo(BigDecimal.ZERO) <= 0) continue;
      BigDecimal memberScore = calcBayesianScore(row.getReviewSum(), row.getReviewCount());
      weighted = weighted.add(memberScore.multiply(weight));
      activeWeight = activeWeight.add(weight);
    }
    if (activeWeight.compareTo(BigDecimal.ZERO) == 0) {
      return new BigDecimal("70.00");
    }
    return weighted.divide(activeWeight, 2, RoundingMode.HALF_UP);
  }

  private void decorateMemberStats(ReviewDtos.TeamMemberReviewStats row) {
    BigDecimal score = calcBayesianScore(row.getReviewSum(), row.getReviewCount());
    row.setRatingScore(score);
    row.setAvgRating(scale(row.getAvgRating()));
    row.setPositiveRate(scaleScore(row.getPositiveRate()));
    if (row.getKeywordSummary() == null || row.getKeywordSummary().isBlank()) {
      row.setKeywordSummary("暂无评价关键词");
    }
  }

  private BigDecimal calcBayesianScore(BigDecimal reviewSum, Integer reviewCount) {
    BigDecimal count = new BigDecimal(safeInt(reviewCount));
    BigDecimal sum = reviewSum == null ? BigDecimal.ZERO : reviewSum;
    BigDecimal avg = sum.add(PRIOR_RATING.multiply(PRIOR_COUNT))
      .divide(count.add(PRIOR_COUNT), 4, RoundingMode.HALF_UP);
    return scaleScore(avg.multiply(new BigDecimal("20")));
  }

  private BigDecimal calcOfferOutcomeScore(ReviewDtos.OfferOutcomeStats stats) {
    int outcomes = stats == null ? 0 : safeInt(stats.getOutcomeCount());
    if (outcomes == 0) return new BigDecimal("70.00");
    BigDecimal conversion = pct(safeInt(stats.getAdmittedCount()), outcomes);
    BigDecimal highQuality = pct(safeInt(stats.getHighQualityOfferCount()), outcomes);
    BigDecimal targetMatch = conversion;
    return scaleScore(
      conversion.multiply(new BigDecimal("0.50"))
        .add(highQuality.multiply(new BigDecimal("0.30")))
        .add(targetMatch.multiply(new BigDecimal("0.20")))
    );
  }

  private BigDecimal calcProcessPerformanceScore(ReviewDtos.ProcessStats stats) {
    int totalStages = stats == null ? 0 : safeInt(stats.getTotalStages());
    if (totalStages == 0) return new BigDecimal("70.00");
    BigDecimal completion = pct(safeInt(stats.getCompletedStages()), totalStages);
    BigDecimal orderCompletion = safeInt(stats.getTotalOrders()) == 0 ? new BigDecimal("70.00") : pct(safeInt(stats.getCompletedOrders()), safeInt(stats.getTotalOrders()));
    BigDecimal rejectionPenalty = new BigDecimal(Math.min(20, safeInt(stats.getRejectedStages()) * 3));
    BigDecimal score = completion.multiply(new BigDecimal("0.70"))
      .add(orderCompletion.multiply(new BigDecimal("0.30")))
      .subtract(rejectionPenalty);
    return clamp(score);
  }

  private BigDecimal calcPlatformTrustScore(ReviewDtos.PlatformTrustStats stats) {
    if (stats == null) return new BigDecimal("60.00");
    BigDecimal orgScore = "APPROVED".equals(stats.getOrgVerificationStatus()) ? new BigDecimal("50") : new BigDecimal("25");
    int totalMembers = safeInt(stats.getTotalMembers());
    BigDecimal memberScore = totalMembers == 0 ? new BigDecimal("25") : pct(safeInt(stats.getApprovedMembers()), totalMembers).multiply(new BigDecimal("0.50"));
    BigDecimal refundPenalty = new BigDecimal(Math.min(20, safeInt(stats.getRefundCount()) * 5));
    return clamp(orgScore.add(memberScore).subtract(refundPenalty));
  }

  private ReviewDtos.RatingSummary mergeOrgSummary(Long orgId, List<ReviewDtos.RatingSummary> teams) {
    ReviewDtos.RatingSummary summary = new ReviewDtos.RatingSummary();
    summary.setTeamId(null);
    if (teams == null || teams.isEmpty()) {
      summary.setTotalScore(BigDecimal.ZERO);
      summary.setStudentReviewScore(BigDecimal.ZERO);
      summary.setOfferOutcomeScore(BigDecimal.ZERO);
      summary.setProcessPerformanceScore(BigDecimal.ZERO);
      summary.setPlatformTrustScore(BigDecimal.ZERO);
      summary.setReviewCount(0);
      summary.setPositiveRate(BigDecimal.ZERO);
      summary.setConfidenceLabel("暂无套餐数据");
      return summary;
    }
    BigDecimal total = BigDecimal.ZERO;
    BigDecimal student = BigDecimal.ZERO;
    BigDecimal offer = BigDecimal.ZERO;
    BigDecimal process = BigDecimal.ZERO;
    BigDecimal trust = BigDecimal.ZERO;
    int reviewCount = 0;
    BigDecimal positiveWeighted = BigDecimal.ZERO;
    for (ReviewDtos.RatingSummary team : teams) {
      total = total.add(team.getTotalScore());
      student = student.add(team.getStudentReviewScore());
      offer = offer.add(team.getOfferOutcomeScore());
      process = process.add(team.getProcessPerformanceScore());
      trust = trust.add(team.getPlatformTrustScore());
      reviewCount += safeInt(team.getReviewCount());
      positiveWeighted = positiveWeighted.add(team.getPositiveRate().multiply(new BigDecimal(Math.max(1, safeInt(team.getReviewCount())))));
    }
    BigDecimal size = new BigDecimal(teams.size());
    summary.setTotalScore(scaleScore(total.divide(size, 2, RoundingMode.HALF_UP)));
    summary.setStudentReviewScore(scaleScore(student.divide(size, 2, RoundingMode.HALF_UP)));
    summary.setOfferOutcomeScore(scaleScore(offer.divide(size, 2, RoundingMode.HALF_UP)));
    summary.setProcessPerformanceScore(scaleScore(process.divide(size, 2, RoundingMode.HALF_UP)));
    summary.setPlatformTrustScore(scaleScore(trust.divide(size, 2, RoundingMode.HALF_UP)));
    summary.setReviewCount(reviewCount);
    summary.setPositiveRate(reviewCount == 0 ? BigDecimal.ZERO : scaleScore(positiveWeighted.divide(new BigDecimal(reviewCount), 2, RoundingMode.HALF_UP)));
    summary.setConfidenceLabel(confidenceLabel(reviewCount));
    reviewMapper.upsertRatingSummary("ORG", orgId, null, orgId, null, summary.getTotalScore(), summary.getStudentReviewScore(),
      summary.getOfferOutcomeScore(), summary.getProcessPerformanceScore(), summary.getPlatformTrustScore(), reviewCount,
      summary.getPositiveRate(), summary.getConfidenceLabel());
    return summary;
  }

  private BigDecimal roleWeight(String roleCode) {
    if ("CONSULTANT".equals(roleCode)) return new BigDecimal("0.25");
    if ("PLANNER".equals(roleCode)) return new BigDecimal("0.25");
    if ("WRITER".equals(roleCode)) return new BigDecimal("0.25");
    if ("APPLY_SPECIALIST".equals(roleCode)) return new BigDecimal("0.15");
    if ("VISA_SPECIALIST".equals(roleCode) || "AFTERCARE".equals(roleCode)) return new BigDecimal("0.10");
    return BigDecimal.ZERO;
  }

  private void requireRole(Long userId, String role) {
    UserAccount user = userAccountMapper.findById(userId);
    if (user == null || !role.equals(user.getRole())) {
      throw new BizException("BIZ_FORBIDDEN", "账号角色无权执行该操作");
    }
  }

  private BigDecimal average(BigDecimal... values) {
    BigDecimal sum = BigDecimal.ZERO;
    for (BigDecimal value : values) {
      sum = sum.add(value);
    }
    return scale(sum.divide(new BigDecimal(values.length), 2, RoundingMode.HALF_UP));
  }

  private BigDecimal pct(int part, int total) {
    if (total <= 0) return BigDecimal.ZERO;
    return new BigDecimal(part).multiply(HUNDRED).divide(new BigDecimal(total), 2, RoundingMode.HALF_UP);
  }

  private BigDecimal clamp(BigDecimal value) {
    if (value.compareTo(BigDecimal.ZERO) < 0) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    if (value.compareTo(HUNDRED) > 0) return HUNDRED.setScale(2, RoundingMode.HALF_UP);
    return scaleScore(value);
  }

  private BigDecimal scale(BigDecimal value) {
    return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
  }

  private BigDecimal scaleScore(BigDecimal value) {
    return scale(value);
  }

  private int safeInt(Integer value) {
    return value == null ? 0 : value;
  }

  private String confidenceLabel(int reviewCount) {
    if (reviewCount <= 0) return "暂无足够评价";
    if (reviewCount < 5) return "样本较少";
    if (reviewCount < 20) return "评价稳定";
    return "高可信样本";
  }

  private String blankToNull(String value) {
    return value == null || value.isBlank() ? null : value.trim();
  }
}
