package com.offerbridge.backend.service.impl;

import com.offerbridge.backend.dto.AdminDtos;
import com.offerbridge.backend.dto.MessageDtos;
import com.offerbridge.backend.entity.AgencyOrg;
import com.offerbridge.backend.entity.SystemNotification;
import com.offerbridge.backend.entity.UserAccount;
import com.offerbridge.backend.exception.BizException;
import com.offerbridge.backend.mapper.AdminReviewMapper;
import com.offerbridge.backend.mapper.AgencyMemberProfileMapper;
import com.offerbridge.backend.mapper.AgencyOrgMapper;
import com.offerbridge.backend.mapper.SystemNotificationMapper;
import com.offerbridge.backend.mapper.UserAccountMapper;
import com.offerbridge.backend.mapper.VerificationRecordMapper;
import com.offerbridge.backend.service.AdminReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AdminReviewServiceImpl implements AdminReviewService {
  private final AdminReviewMapper adminReviewMapper;
  private final VerificationRecordMapper verificationRecordMapper;
  private final AgencyOrgMapper agencyOrgMapper;
  private final AgencyMemberProfileMapper agencyMemberProfileMapper;
  private final UserAccountMapper userAccountMapper;
  private final SystemNotificationMapper systemNotificationMapper;

  public AdminReviewServiceImpl(AdminReviewMapper adminReviewMapper,
                                VerificationRecordMapper verificationRecordMapper,
                                AgencyOrgMapper agencyOrgMapper,
                                AgencyMemberProfileMapper agencyMemberProfileMapper,
                                UserAccountMapper userAccountMapper,
                                SystemNotificationMapper systemNotificationMapper) {
    this.adminReviewMapper = adminReviewMapper;
    this.verificationRecordMapper = verificationRecordMapper;
    this.agencyOrgMapper = agencyOrgMapper;
    this.agencyMemberProfileMapper = agencyMemberProfileMapper;
    this.userAccountMapper = userAccountMapper;
    this.systemNotificationMapper = systemNotificationMapper;
  }

  @Override
  public AdminDtos.PagedResult<AdminDtos.ReviewListItem> listOrgReviews(Long adminUserId, int page, int pageSize, String status, String keyword) {
    requireAdmin(adminUserId);
    return toPaged(filter(adminReviewMapper.listOrgReviews(), status, keyword), page, pageSize);
  }

  @Override
  public AdminDtos.PagedResult<AdminDtos.ReviewListItem> listMemberReviews(Long adminUserId, int page, int pageSize, String status, String keyword) {
    requireAdmin(adminUserId);
    return toPaged(filter(adminReviewMapper.listMemberReviews(), status, keyword), page, pageSize);
  }

  @Override
  public AdminDtos.PagedResult<AdminDtos.ReviewListItem> listStudentReviews(Long adminUserId, int page, int pageSize, String status, String keyword) {
    requireAdmin(adminUserId);
    return toPaged(filter(adminReviewMapper.listStudentReviews(), status, keyword), page, pageSize);
  }

  @Override
  public AdminDtos.ReviewDetailView getReviewDetail(Long adminUserId, String subjectType, Long userId) {
    requireAdmin(adminUserId);
    String subject = normalizeSubjectType(subjectType);
    AdminDtos.ReviewDetailView detail = switch (subject) {
      case "ORG" -> adminReviewMapper.findOrgReviewDetail(userId);
      case "MEMBER" -> adminReviewMapper.findMemberReviewDetail(userId);
      case "STUDENT" -> adminReviewMapper.findStudentReviewDetail(userId);
      default -> null;
    };
    if (detail == null) {
      throw new BizException("BIZ_NOT_FOUND", "审核记录不存在");
    }
    return detail;
  }

  @Override
  @Transactional
  public void approve(Long adminUserId, String subjectType, Long userId) {
    requireAdmin(adminUserId);
    String subject = normalizeSubjectType(subjectType);
    switch (subject) {
      case "ORG" -> {
        verificationRecordMapper.review(userId, "AGENT_ORG", "APPROVED", null, adminUserId);
        AgencyOrg org = agencyOrgMapper.findByAdminUserId(userId);
        if (org != null) {
          agencyOrgMapper.updateVerificationStatus(org.getId(), "APPROVED");
        }
      }
      case "MEMBER" -> {
        verificationRecordMapper.review(userId, "AGENT_MEMBER_CERT", "APPROVED", null, adminUserId);
        agencyMemberProfileMapper.updateProfileAuditStatusByUserId(userId, "APPROVED");
      }
      case "STUDENT" -> {
        verificationRecordMapper.review(userId, "REAL_NAME", "APPROVED", null, adminUserId);
        verificationRecordMapper.review(userId, "EDUCATION", "APPROVED", null, adminUserId);
      }
      default -> throw new BizException("BIZ_BAD_REQUEST", "不支持的审核类型");
    }
    sendAuditNotification(userId, subject, true, null);
  }

  @Override
  @Transactional
  public void reject(Long adminUserId, String subjectType, Long userId, String reason) {
    requireAdmin(adminUserId);
    String subject = normalizeSubjectType(subjectType);
    String rejectReason = reason == null ? "" : reason.trim();
    if (rejectReason.isBlank()) {
      throw new BizException("BIZ_BAD_REQUEST", "驳回原因不能为空");
    }
    switch (subject) {
      case "ORG" -> {
        verificationRecordMapper.review(userId, "AGENT_ORG", "REJECTED", rejectReason, adminUserId);
        AgencyOrg org = agencyOrgMapper.findByAdminUserId(userId);
        if (org != null) {
          agencyOrgMapper.updateVerificationStatus(org.getId(), "REJECTED");
        }
      }
      case "MEMBER" -> {
        verificationRecordMapper.review(userId, "AGENT_MEMBER_CERT", "REJECTED", rejectReason, adminUserId);
        agencyMemberProfileMapper.updateProfileAuditStatusByUserId(userId, "REJECTED");
      }
      case "STUDENT" -> {
        verificationRecordMapper.review(userId, "REAL_NAME", "REJECTED", rejectReason, adminUserId);
        verificationRecordMapper.review(userId, "EDUCATION", "REJECTED", rejectReason, adminUserId);
      }
      default -> throw new BizException("BIZ_BAD_REQUEST", "不支持的审核类型");
    }
    sendAuditNotification(userId, subject, false, rejectReason);
  }

  @Override
  public MessageDtos.PagedResult<MessageDtos.SystemNotificationItem> listAllNotifications(Long adminUserId, int page, int pageSize) {
    requireAdmin(adminUserId);
    List<MessageDtos.SystemNotificationItem> all = systemNotificationMapper.listAll();
    int safePage = Math.max(1, page);
    int safePageSize = Math.max(1, Math.min(pageSize, 100));
    int from = (safePage - 1) * safePageSize;
    int to = Math.min(from + safePageSize, all.size());
    List<MessageDtos.SystemNotificationItem> records = from >= all.size() ? List.of() : all.subList(from, to);
    MessageDtos.PagedResult<MessageDtos.SystemNotificationItem> result = new MessageDtos.PagedResult<>();
    result.setRecords(records);
    result.setTotal(all.size());
    result.setPage(safePage);
    result.setPageSize(safePageSize);
    result.setUnreadCount(0);
    return result;
  }

  private List<AdminDtos.ReviewListItem> filter(List<AdminDtos.ReviewListItem> source, String status, String keyword) {
    String statusFilter = status == null ? "" : status.trim().toUpperCase();
    if (statusFilter.isBlank()) {
      statusFilter = "PENDING";
    }
    String keywordFilter = keyword == null ? "" : keyword.trim();
    List<AdminDtos.ReviewListItem> filtered = new ArrayList<>();
    for (AdminDtos.ReviewListItem item : source) {
      if (!statusFilter.equals((item.getStatus() == null ? "" : item.getStatus().toUpperCase()))) {
        continue;
      }
      if (!keywordFilter.isBlank()) {
        boolean hit = contains(item.getSubjectName(), keywordFilter) || contains(item.getPhone(), keywordFilter) || contains(item.getOrgName(), keywordFilter);
        if (!hit) continue;
      }
      filtered.add(item);
    }
    filtered.sort(Comparator.comparing(AdminDtos.ReviewListItem::getSubmittedAt, Comparator.nullsLast(Comparator.reverseOrder())));
    return filtered;
  }

  private AdminDtos.PagedResult<AdminDtos.ReviewListItem> toPaged(List<AdminDtos.ReviewListItem> all, int page, int pageSize) {
    int safePage = Math.max(1, page);
    int safePageSize = Math.max(1, Math.min(pageSize, 200));
    int from = (safePage - 1) * safePageSize;
    int to = Math.min(from + safePageSize, all.size());
    List<AdminDtos.ReviewListItem> records = from >= all.size() ? List.of() : all.subList(from, to);
    AdminDtos.PagedResult<AdminDtos.ReviewListItem> result = new AdminDtos.PagedResult<>();
    result.setRecords(records);
    result.setTotal(all.size());
    result.setPage(safePage);
    result.setPageSize(safePageSize);
    return result;
  }

  private String normalizeSubjectType(String subjectType) {
    if (subjectType == null) {
      throw new BizException("BIZ_BAD_REQUEST", "subjectType 不能为空");
    }
    String value = subjectType.trim().toUpperCase();
    if (!"ORG".equals(value) && !"MEMBER".equals(value) && !"STUDENT".equals(value)) {
      throw new BizException("BIZ_BAD_REQUEST", "subjectType 不合法");
    }
    return value;
  }

  private void requireAdmin(Long userId) {
    UserAccount user = userAccountMapper.findById(userId);
    if (user == null || !"ACTIVE".equals(user.getStatus())) {
      throw new BizException("BIZ_UNAUTHORIZED", "账号不可用");
    }
    if (!"ADMIN".equals(user.getRole())) {
      throw new BizException("BIZ_FORBIDDEN", "仅管理员可执行该操作");
    }
  }

  private void sendAuditNotification(Long userId, String subjectType, boolean approved, String reason) {
    SystemNotification notice = new SystemNotification();
    notice.setUserId(userId);
    notice.setType("AUDIT_RESULT");
    notice.setTitle(approved ? "认证审核已通过" : "认证审核未通过");
    String content = approved
      ? "你的" + toSubjectName(subjectType) + "认证已通过，现在可以使用对应功能。"
      : "你的" + toSubjectName(subjectType) + "认证未通过，原因：" + reason;
    notice.setContent(content);
    notice.setStatus("UNREAD");
    notice.setRelatedType(subjectType);
    notice.setRelatedId(String.valueOf(userId));
    systemNotificationMapper.insertOne(notice);
  }

  private String toSubjectName(String subjectType) {
    return switch (subjectType) {
      case "ORG" -> "机构";
      case "MEMBER" -> "员工";
      case "STUDENT" -> "学生";
      default -> "账号";
    };
  }

  private boolean contains(String source, String keyword) {
    return source != null && source.contains(keyword);
  }
}
