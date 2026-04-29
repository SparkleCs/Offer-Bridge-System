package com.offerbridge.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class ReviewDtos {
  public static class OrderReviewTarget {
    private Long orderId;
    private Long memberId;
    private String displayName;
    private String avatarUrl;
    private String jobTitle;
    private String roleCode;
    private Boolean reviewed;
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public Boolean getReviewed() { return reviewed; }
    public void setReviewed(Boolean reviewed) { this.reviewed = reviewed; }
  }

  public static class OrderReviewStatus {
    private Boolean canReview;
    private String reason;
    private List<OrderReviewTarget> targets;
    public Boolean getCanReview() { return canReview; }
    public void setCanReview(Boolean canReview) { this.canReview = canReview; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public List<OrderReviewTarget> getTargets() { return targets; }
    public void setTargets(List<OrderReviewTarget> targets) { this.targets = targets; }
  }

  public static class MemberReviewSubmitItem {
    @NotNull private Long memberId;
    @NotNull @DecimalMin("1.0") @DecimalMax("5.0") private BigDecimal professionalScore;
    @NotNull @DecimalMin("1.0") @DecimalMax("5.0") private BigDecimal communicationScore;
    @NotNull @DecimalMin("1.0") @DecimalMax("5.0") private BigDecimal materialScore;
    @NotNull @DecimalMin("1.0") @DecimalMax("5.0") private BigDecimal transparencyScore;
    @NotNull @DecimalMin("1.0") @DecimalMax("5.0") private BigDecimal responsibilityScore;
    @Min(0) @Max(10) private Integer npsScore;
    private String commentText;
    private Boolean anonymous;
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public BigDecimal getProfessionalScore() { return professionalScore; }
    public void setProfessionalScore(BigDecimal professionalScore) { this.professionalScore = professionalScore; }
    public BigDecimal getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(BigDecimal communicationScore) { this.communicationScore = communicationScore; }
    public BigDecimal getMaterialScore() { return materialScore; }
    public void setMaterialScore(BigDecimal materialScore) { this.materialScore = materialScore; }
    public BigDecimal getTransparencyScore() { return transparencyScore; }
    public void setTransparencyScore(BigDecimal transparencyScore) { this.transparencyScore = transparencyScore; }
    public BigDecimal getResponsibilityScore() { return responsibilityScore; }
    public void setResponsibilityScore(BigDecimal responsibilityScore) { this.responsibilityScore = responsibilityScore; }
    public Integer getNpsScore() { return npsScore; }
    public void setNpsScore(Integer npsScore) { this.npsScore = npsScore; }
    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }
    public Boolean getAnonymous() { return anonymous; }
    public void setAnonymous(Boolean anonymous) { this.anonymous = anonymous; }
  }

  public static class SubmitOrderReviewRequest {
    @NotEmpty @Valid private List<MemberReviewSubmitItem> reviews;
    public List<MemberReviewSubmitItem> getReviews() { return reviews; }
    public void setReviews(List<MemberReviewSubmitItem> reviews) { this.reviews = reviews; }
  }

  public static class RatingSummary {
    private Long teamId;
    private BigDecimal totalScore;
    private BigDecimal studentReviewScore;
    private BigDecimal offerOutcomeScore;
    private BigDecimal processPerformanceScore;
    private BigDecimal platformTrustScore;
    private Integer reviewCount;
    private BigDecimal positiveRate;
    private String confidenceLabel;
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }
    public BigDecimal getStudentReviewScore() { return studentReviewScore; }
    public void setStudentReviewScore(BigDecimal studentReviewScore) { this.studentReviewScore = studentReviewScore; }
    public BigDecimal getOfferOutcomeScore() { return offerOutcomeScore; }
    public void setOfferOutcomeScore(BigDecimal offerOutcomeScore) { this.offerOutcomeScore = offerOutcomeScore; }
    public BigDecimal getProcessPerformanceScore() { return processPerformanceScore; }
    public void setProcessPerformanceScore(BigDecimal processPerformanceScore) { this.processPerformanceScore = processPerformanceScore; }
    public BigDecimal getPlatformTrustScore() { return platformTrustScore; }
    public void setPlatformTrustScore(BigDecimal platformTrustScore) { this.platformTrustScore = platformTrustScore; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public BigDecimal getPositiveRate() { return positiveRate; }
    public void setPositiveRate(BigDecimal positiveRate) { this.positiveRate = positiveRate; }
    public String getConfidenceLabel() { return confidenceLabel; }
    public void setConfidenceLabel(String confidenceLabel) { this.confidenceLabel = confidenceLabel; }
  }

  public static class TeamMemberReviewSummary {
    private Long memberId;
    private String displayName;
    private String avatarUrl;
    private String jobTitle;
    private String roleCode;
    private BigDecimal ratingScore;
    private BigDecimal avgRating;
    private Integer reviewCount;
    private BigDecimal positiveRate;
    private String keywordSummary;
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public BigDecimal getRatingScore() { return ratingScore; }
    public void setRatingScore(BigDecimal ratingScore) { this.ratingScore = ratingScore; }
    public BigDecimal getAvgRating() { return avgRating; }
    public void setAvgRating(BigDecimal avgRating) { this.avgRating = avgRating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public BigDecimal getPositiveRate() { return positiveRate; }
    public void setPositiveRate(BigDecimal positiveRate) { this.positiveRate = positiveRate; }
    public String getKeywordSummary() { return keywordSummary; }
    public void setKeywordSummary(String keywordSummary) { this.keywordSummary = keywordSummary; }
  }

  public static class MemberReviewItem {
    private Long reviewId;
    private Long memberId;
    private String studentName;
    private BigDecimal overallRating;
    private BigDecimal professionalScore;
    private BigDecimal communicationScore;
    private BigDecimal materialScore;
    private BigDecimal transparencyScore;
    private BigDecimal responsibilityScore;
    private Integer npsScore;
    private String commentText;
    private String createdAt;
    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public BigDecimal getOverallRating() { return overallRating; }
    public void setOverallRating(BigDecimal overallRating) { this.overallRating = overallRating; }
    public BigDecimal getProfessionalScore() { return professionalScore; }
    public void setProfessionalScore(BigDecimal professionalScore) { this.professionalScore = professionalScore; }
    public BigDecimal getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(BigDecimal communicationScore) { this.communicationScore = communicationScore; }
    public BigDecimal getMaterialScore() { return materialScore; }
    public void setMaterialScore(BigDecimal materialScore) { this.materialScore = materialScore; }
    public BigDecimal getTransparencyScore() { return transparencyScore; }
    public void setTransparencyScore(BigDecimal transparencyScore) { this.transparencyScore = transparencyScore; }
    public BigDecimal getResponsibilityScore() { return responsibilityScore; }
    public void setResponsibilityScore(BigDecimal responsibilityScore) { this.responsibilityScore = responsibilityScore; }
    public Integer getNpsScore() { return npsScore; }
    public void setNpsScore(Integer npsScore) { this.npsScore = npsScore; }
    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
  }

  public static class AgencyDashboard {
    private RatingSummary orgSummary;
    private List<TeamMemberReviewSummary> memberRankings;
    private List<RatingSummary> teamSummaries;
    public RatingSummary getOrgSummary() { return orgSummary; }
    public void setOrgSummary(RatingSummary orgSummary) { this.orgSummary = orgSummary; }
    public List<TeamMemberReviewSummary> getMemberRankings() { return memberRankings; }
    public void setMemberRankings(List<TeamMemberReviewSummary> memberRankings) { this.memberRankings = memberRankings; }
    public List<RatingSummary> getTeamSummaries() { return teamSummaries; }
    public void setTeamSummaries(List<RatingSummary> teamSummaries) { this.teamSummaries = teamSummaries; }
  }

  public static class TeamMemberReviewStats extends TeamMemberReviewSummary {
    private BigDecimal reviewSum;
    private Integer positiveCount;
    public BigDecimal getReviewSum() { return reviewSum; }
    public void setReviewSum(BigDecimal reviewSum) { this.reviewSum = reviewSum; }
    public Integer getPositiveCount() { return positiveCount; }
    public void setPositiveCount(Integer positiveCount) { this.positiveCount = positiveCount; }
  }

  public static class OfferOutcomeStats {
    private Integer outcomeCount;
    private Integer admittedCount;
    private Integer highQualityOfferCount;
    public Integer getOutcomeCount() { return outcomeCount; }
    public void setOutcomeCount(Integer outcomeCount) { this.outcomeCount = outcomeCount; }
    public Integer getAdmittedCount() { return admittedCount; }
    public void setAdmittedCount(Integer admittedCount) { this.admittedCount = admittedCount; }
    public Integer getHighQualityOfferCount() { return highQualityOfferCount; }
    public void setHighQualityOfferCount(Integer highQualityOfferCount) { this.highQualityOfferCount = highQualityOfferCount; }
  }

  public static class ProcessStats {
    private Integer totalStages;
    private Integer completedStages;
    private Integer rejectedStages;
    private Integer totalOrders;
    private Integer completedOrders;
    public Integer getTotalStages() { return totalStages; }
    public void setTotalStages(Integer totalStages) { this.totalStages = totalStages; }
    public Integer getCompletedStages() { return completedStages; }
    public void setCompletedStages(Integer completedStages) { this.completedStages = completedStages; }
    public Integer getRejectedStages() { return rejectedStages; }
    public void setRejectedStages(Integer rejectedStages) { this.rejectedStages = rejectedStages; }
    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
    public Integer getCompletedOrders() { return completedOrders; }
    public void setCompletedOrders(Integer completedOrders) { this.completedOrders = completedOrders; }
  }

  public static class PlatformTrustStats {
    private String orgVerificationStatus;
    private Integer totalMembers;
    private Integer approvedMembers;
    private Integer refundCount;
    public String getOrgVerificationStatus() { return orgVerificationStatus; }
    public void setOrgVerificationStatus(String orgVerificationStatus) { this.orgVerificationStatus = orgVerificationStatus; }
    public Integer getTotalMembers() { return totalMembers; }
    public void setTotalMembers(Integer totalMembers) { this.totalMembers = totalMembers; }
    public Integer getApprovedMembers() { return approvedMembers; }
    public void setApprovedMembers(Integer approvedMembers) { this.approvedMembers = approvedMembers; }
    public Integer getRefundCount() { return refundCount; }
    public void setRefundCount(Integer refundCount) { this.refundCount = refundCount; }
  }
}
