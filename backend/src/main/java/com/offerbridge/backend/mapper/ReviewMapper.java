package com.offerbridge.backend.mapper;

import com.offerbridge.backend.dto.ReviewDtos;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ReviewMapper {
  int insertOrderMemberSnapshots(@Param("orderId") Long orderId, @Param("teamId") Long teamId);
  List<ReviewDtos.OrderReviewTarget> listOrderReviewTargets(@Param("orderId") Long orderId);
  int countSnapshotMember(@Param("orderId") Long orderId, @Param("memberId") Long memberId);
  int countReviewByOrderAndMember(@Param("orderId") Long orderId, @Param("memberId") Long memberId);
  int insertMemberReview(@Param("orderId") Long orderId,
                         @Param("studentUserId") Long studentUserId,
                         @Param("orgId") Long orgId,
                         @Param("teamId") Long teamId,
                         @Param("memberId") Long memberId,
                         @Param("professionalScore") BigDecimal professionalScore,
                         @Param("communicationScore") BigDecimal communicationScore,
                         @Param("materialScore") BigDecimal materialScore,
                         @Param("transparencyScore") BigDecimal transparencyScore,
                         @Param("responsibilityScore") BigDecimal responsibilityScore,
                         @Param("overallRating") BigDecimal overallRating,
                         @Param("npsScore") Integer npsScore,
                         @Param("commentText") String commentText,
                         @Param("anonymous") Boolean anonymous);

  List<ReviewDtos.TeamMemberReviewStats> listTeamMemberReviewStats(@Param("teamId") Long teamId);
  List<ReviewDtos.MemberReviewItem> listMemberReviews(@Param("teamId") Long teamId, @Param("memberId") Long memberId);
  ReviewDtos.OfferOutcomeStats getOfferOutcomeStats(@Param("teamId") Long teamId);
  ReviewDtos.ProcessStats getProcessStats(@Param("teamId") Long teamId);
  ReviewDtos.PlatformTrustStats getPlatformTrustStats(@Param("teamId") Long teamId);
  int upsertRatingSummary(@Param("scopeType") String scopeType,
                          @Param("scopeId") Long scopeId,
                          @Param("teamId") Long teamId,
                          @Param("orgId") Long orgId,
                          @Param("memberId") Long memberId,
                          @Param("totalScore") BigDecimal totalScore,
                          @Param("studentReviewScore") BigDecimal studentReviewScore,
                          @Param("offerOutcomeScore") BigDecimal offerOutcomeScore,
                          @Param("processPerformanceScore") BigDecimal processPerformanceScore,
                          @Param("platformTrustScore") BigDecimal platformTrustScore,
                          @Param("reviewCount") Integer reviewCount,
                          @Param("positiveRate") BigDecimal positiveRate,
                          @Param("confidenceLabel") String confidenceLabel);
  List<Long> listOrgPublishedTeamIds(@Param("orgId") Long orgId);
  List<ReviewDtos.TeamMemberReviewStats> listOrgMemberReviewStats(@Param("orgId") Long orgId);
}
