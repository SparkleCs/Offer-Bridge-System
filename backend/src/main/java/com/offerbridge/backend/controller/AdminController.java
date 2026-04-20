package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.dto.AdminDtos;
import com.offerbridge.backend.dto.MessageDtos;
import com.offerbridge.backend.security.AuthContext;
import com.offerbridge.backend.service.AdminReviewService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@Validated
public class AdminController {
  private final AdminReviewService adminReviewService;

  public AdminController(AdminReviewService adminReviewService) {
    this.adminReviewService = adminReviewService;
  }

  @GetMapping("/reviews/orgs")
  public ApiResponse<AdminDtos.PagedResult<AdminDtos.ReviewListItem>> listOrgReviews(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "10") int pageSize,
    @RequestParam(required = false) String status,
    @RequestParam(required = false) String keyword
  ) {
    return ApiResponse.ok(adminReviewService.listOrgReviews(AuthContext.getUserId(), page, pageSize, status, keyword));
  }

  @GetMapping("/reviews/members")
  public ApiResponse<AdminDtos.PagedResult<AdminDtos.ReviewListItem>> listMemberReviews(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "10") int pageSize,
    @RequestParam(required = false) String status,
    @RequestParam(required = false) String keyword
  ) {
    return ApiResponse.ok(adminReviewService.listMemberReviews(AuthContext.getUserId(), page, pageSize, status, keyword));
  }

  @GetMapping("/reviews/students")
  public ApiResponse<AdminDtos.PagedResult<AdminDtos.ReviewListItem>> listStudentReviews(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "10") int pageSize,
    @RequestParam(required = false) String status,
    @RequestParam(required = false) String keyword
  ) {
    return ApiResponse.ok(adminReviewService.listStudentReviews(AuthContext.getUserId(), page, pageSize, status, keyword));
  }

  @GetMapping("/reviews/{subjectType}/{userId}")
  public ApiResponse<AdminDtos.ReviewDetailView> getReviewDetail(@PathVariable String subjectType, @PathVariable Long userId) {
    return ApiResponse.ok(adminReviewService.getReviewDetail(AuthContext.getUserId(), subjectType, userId));
  }

  @PostMapping("/reviews/{subjectType}/{userId}/approve")
  public ApiResponse<Void> approve(@PathVariable String subjectType, @PathVariable Long userId) {
    adminReviewService.approve(AuthContext.getUserId(), subjectType, userId);
    return ApiResponse.ok();
  }

  @PostMapping("/reviews/{subjectType}/{userId}/reject")
  public ApiResponse<Void> reject(@PathVariable String subjectType,
                                  @PathVariable Long userId,
                                  @Valid @RequestBody AdminDtos.RejectRequest request) {
    adminReviewService.reject(AuthContext.getUserId(), subjectType, userId, request.getReason());
    return ApiResponse.ok();
  }

  @GetMapping("/notifications")
  public ApiResponse<MessageDtos.PagedResult<MessageDtos.SystemNotificationItem>> listNotifications(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "20") int pageSize
  ) {
    return ApiResponse.ok(adminReviewService.listAllNotifications(AuthContext.getUserId(), page, pageSize));
  }
}

