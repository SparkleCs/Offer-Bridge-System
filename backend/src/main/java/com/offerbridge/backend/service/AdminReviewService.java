package com.offerbridge.backend.service;

import com.offerbridge.backend.dto.AdminDtos;
import com.offerbridge.backend.dto.MessageDtos;

public interface AdminReviewService {
  AdminDtos.PagedResult<AdminDtos.ReviewListItem> listOrgReviews(Long adminUserId, int page, int pageSize, String status, String keyword);
  AdminDtos.PagedResult<AdminDtos.ReviewListItem> listMemberReviews(Long adminUserId, int page, int pageSize, String status, String keyword);
  AdminDtos.PagedResult<AdminDtos.ReviewListItem> listStudentReviews(Long adminUserId, int page, int pageSize, String status, String keyword);
  AdminDtos.ReviewDetailView getReviewDetail(Long adminUserId, String subjectType, Long userId);
  void approve(Long adminUserId, String subjectType, Long userId);
  void reject(Long adminUserId, String subjectType, Long userId, String reason);
  MessageDtos.PagedResult<MessageDtos.SystemNotificationItem> listAllNotifications(Long adminUserId, int page, int pageSize);
}

