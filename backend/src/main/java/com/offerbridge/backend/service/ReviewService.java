package com.offerbridge.backend.service;

import com.offerbridge.backend.dto.ReviewDtos;

import java.util.List;

public interface ReviewService {
  void snapshotOrderMembers(Long orderId, Long teamId);
  ReviewDtos.OrderReviewStatus getOrderReviewStatus(Long userId, Long orderId);
  ReviewDtos.OrderReviewStatus submitOrderReview(Long userId, Long orderId, ReviewDtos.SubmitOrderReviewRequest request);
  List<ReviewDtos.RatingSummary> getTeamSummaries(List<Long> teamIds);
  ReviewDtos.RatingSummary getTeamSummary(Long teamId);
  List<ReviewDtos.TeamMemberReviewSummary> getTeamMemberSummaries(Long teamId);
  List<ReviewDtos.MemberReviewItem> getMemberReviews(Long teamId, Long memberId);
  ReviewDtos.AgencyDashboard getAgencyDashboard(Long userId);
}
