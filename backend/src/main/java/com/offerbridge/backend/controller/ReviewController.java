package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.dto.ReviewDtos;
import com.offerbridge.backend.security.AuthContext;
import com.offerbridge.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
  private final ReviewService reviewService;

  public ReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @GetMapping("/orders/{orderId}/status")
  public ApiResponse<ReviewDtos.OrderReviewStatus> getOrderReviewStatus(@PathVariable Long orderId) {
    return ApiResponse.ok(reviewService.getOrderReviewStatus(AuthContext.getUserId(), orderId));
  }

  @PostMapping("/orders/{orderId}")
  public ApiResponse<ReviewDtos.OrderReviewStatus> submitOrderReview(
    @PathVariable Long orderId,
    @Valid @RequestBody ReviewDtos.SubmitOrderReviewRequest request
  ) {
    return ApiResponse.ok(reviewService.submitOrderReview(AuthContext.getUserId(), orderId, request));
  }

  @GetMapping("/discovery/teams/summaries")
  public ApiResponse<List<ReviewDtos.RatingSummary>> getTeamSummaries(@RequestParam String teamIds) {
    List<Long> ids = Arrays.stream(teamIds.split(","))
      .map(String::trim)
      .filter(item -> !item.isBlank())
      .map(Long::valueOf)
      .toList();
    return ApiResponse.ok(reviewService.getTeamSummaries(ids));
  }

  @GetMapping("/discovery/teams/{teamId}/summary")
  public ApiResponse<ReviewDtos.RatingSummary> getTeamSummary(@PathVariable Long teamId) {
    return ApiResponse.ok(reviewService.getTeamSummary(teamId));
  }

  @GetMapping("/discovery/teams/{teamId}/members")
  public ApiResponse<List<ReviewDtos.TeamMemberReviewSummary>> getTeamMemberSummaries(@PathVariable Long teamId) {
    return ApiResponse.ok(reviewService.getTeamMemberSummaries(teamId));
  }

  @GetMapping("/discovery/teams/{teamId}/members/{memberId}/reviews")
  public ApiResponse<List<ReviewDtos.MemberReviewItem>> getMemberReviews(@PathVariable Long teamId, @PathVariable Long memberId) {
    return ApiResponse.ok(reviewService.getMemberReviews(teamId, memberId));
  }

  @GetMapping("/agency/dashboard")
  public ApiResponse<ReviewDtos.AgencyDashboard> getAgencyDashboard() {
    return ApiResponse.ok(reviewService.getAgencyDashboard(AuthContext.getUserId()));
  }
}
