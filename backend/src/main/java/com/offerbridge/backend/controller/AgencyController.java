package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.dto.AgencyDtos;
import com.offerbridge.backend.security.AuthContext;
import com.offerbridge.backend.service.AgencyService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agency")
@Validated
public class AgencyController {
  private final AgencyService agencyService;

  public AgencyController(AgencyService agencyService) {
    this.agencyService = agencyService;
  }

  @PostMapping("/org/profile")
  public ApiResponse<AgencyDtos.OrgProfileView> createOrgProfile(@Valid @RequestBody AgencyDtos.OrgProfileUpsertRequest request) {
    return ApiResponse.ok(agencyService.createOrgProfile(AuthContext.getUserId(), request));
  }

  @PutMapping("/org/profile")
  public ApiResponse<AgencyDtos.OrgProfileView> updateOrgProfile(@Valid @RequestBody AgencyDtos.OrgProfileUpsertRequest request) {
    return ApiResponse.ok(agencyService.updateOrgProfile(AuthContext.getUserId(), request));
  }

  @GetMapping("/org/profile")
  public ApiResponse<AgencyDtos.OrgProfileView> getOrgProfile() {
    return ApiResponse.ok(agencyService.getOrgProfile(AuthContext.getUserId()));
  }

  @GetMapping("/org/verification")
  public ApiResponse<AgencyDtos.OrgVerificationView> getOrgVerification() {
    return ApiResponse.ok(agencyService.getOrgVerification(AuthContext.getUserId()));
  }

  @PostMapping("/org/verification")
  public ApiResponse<AgencyDtos.OrgVerificationView> submitOrgVerification(@Valid @RequestBody AgencyDtos.OrgVerificationSubmitRequest request) {
    return ApiResponse.ok(agencyService.submitOrgVerification(AuthContext.getUserId(), request));
  }

  @PutMapping("/org/verification")
  public ApiResponse<AgencyDtos.OrgVerificationView> updateOrgVerification(@Valid @RequestBody AgencyDtos.OrgVerificationSubmitRequest request) {
    return ApiResponse.ok(agencyService.updateOrgVerification(AuthContext.getUserId(), request));
  }

  @GetMapping("/members")
  public ApiResponse<AgencyDtos.PagedResult<AgencyDtos.MemberAdminItem>> listOrgMembers(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "10") int pageSize,
    @RequestParam(required = false) String keyword,
    @RequestParam(required = false) String status
  ) {
    return ApiResponse.ok(agencyService.listOrgMembers(AuthContext.getUserId(), page, pageSize, keyword, status));
  }

  @GetMapping("/permissions/members")
  public ApiResponse<AgencyDtos.PagedResult<AgencyDtos.MemberAdminItem>> listPermissionMembers(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "10") int pageSize,
    @RequestParam(required = false) String keyword
  ) {
    return ApiResponse.ok(agencyService.listPermissionMembers(AuthContext.getUserId(), page, pageSize, keyword));
  }

  @PostMapping("/members")
  public ApiResponse<AgencyDtos.MemberAdminItem> createOrgMember(@Valid @RequestBody AgencyDtos.MemberCreateRequest request) {
    return ApiResponse.ok(agencyService.createOrgMember(AuthContext.getUserId(), request));
  }

  @PutMapping("/members/{memberId}")
  public ApiResponse<Void> updateOrgMember(@PathVariable Long memberId, @Valid @RequestBody AgencyDtos.MemberProfileUpdateRequest request) {
    agencyService.updateOrgMember(AuthContext.getUserId(), memberId, request);
    return ApiResponse.ok();
  }

  @PutMapping("/members/{memberId}/roles")
  public ApiResponse<Void> updateOrgMemberRoles(@PathVariable Long memberId, @Valid @RequestBody AgencyDtos.MemberRolesUpdateRequest request) {
    agencyService.updateOrgMemberRoles(AuthContext.getUserId(), memberId, request);
    return ApiResponse.ok();
  }

  @PutMapping("/members/{memberId}/status")
  public ApiResponse<Void> updateOrgMemberStatus(@PathVariable Long memberId, @Valid @RequestBody AgencyDtos.MemberStatusUpdateRequest request) {
    agencyService.updateOrgMemberStatus(AuthContext.getUserId(), memberId, request);
    return ApiResponse.ok();
  }

  @PutMapping("/members/{memberId}/soft-delete")
  public ApiResponse<Void> softDeleteOrgMember(@PathVariable Long memberId) {
    agencyService.softDeleteOrgMember(AuthContext.getUserId(), memberId);
    return ApiResponse.ok();
  }

  @PutMapping("/members/{memberId}/permissions")
  public ApiResponse<Void> updateOrgMemberPermissions(@PathVariable Long memberId, @Valid @RequestBody AgencyDtos.MemberPermissionsUpdateRequest request) {
    agencyService.updateOrgMemberPermissions(AuthContext.getUserId(), memberId, request);
    return ApiResponse.ok();
  }

  @PostMapping("/teams")
  public ApiResponse<AgencyDtos.TeamView> createTeam(@Valid @RequestBody AgencyDtos.TeamCreateRequest request) {
    return ApiResponse.ok(agencyService.createTeam(AuthContext.getUserId(), request));
  }

  @GetMapping("/teams")
  public ApiResponse<List<AgencyDtos.TeamView>> listTeams() {
    return ApiResponse.ok(agencyService.listTeams(AuthContext.getUserId()));
  }

  @GetMapping("/team-products")
  public ApiResponse<List<AgencyDtos.TeamProductSummaryItem>> listTeamProducts() {
    return ApiResponse.ok(agencyService.listTeamProducts(AuthContext.getUserId()));
  }

  @GetMapping("/team-products/org-members")
  public ApiResponse<List<AgencyDtos.TeamProductOrgMemberItem>> listTeamProductOrgMembers(
    @RequestParam(required = false) String keyword
  ) {
    return ApiResponse.ok(agencyService.listTeamProductOrgMembers(AuthContext.getUserId(), keyword));
  }

  @GetMapping("/team-products/{teamId}")
  public ApiResponse<AgencyDtos.TeamProductDetailView> getTeamProduct(@PathVariable Long teamId) {
    return ApiResponse.ok(agencyService.getTeamProduct(AuthContext.getUserId(), teamId));
  }

  @PostMapping("/team-products")
  public ApiResponse<AgencyDtos.TeamProductDetailView> createTeamProduct(@Valid @RequestBody AgencyDtos.TeamProductUpsertRequest request) {
    return ApiResponse.ok(agencyService.createTeamProduct(AuthContext.getUserId(), request));
  }

  @PutMapping("/team-products/{teamId}")
  public ApiResponse<AgencyDtos.TeamProductDetailView> updateTeamProduct(
    @PathVariable Long teamId,
    @Valid @RequestBody AgencyDtos.TeamProductUpsertRequest request
  ) {
    return ApiResponse.ok(agencyService.updateTeamProduct(AuthContext.getUserId(), teamId, request));
  }

  @PostMapping("/team-products/{teamId}/publish")
  public ApiResponse<Void> publishTeamProduct(@PathVariable Long teamId) {
    agencyService.publishTeamProduct(AuthContext.getUserId(), teamId);
    return ApiResponse.ok();
  }

  @PostMapping("/invitations")
  public ApiResponse<AgencyDtos.InvitationView> createInvitation(@Valid @RequestBody AgencyDtos.InvitationCreateRequest request) {
    return ApiResponse.ok(agencyService.createInvitation(AuthContext.getUserId(), request));
  }

  @PostMapping("/invitations/{token}/accept")
  public ApiResponse<Void> acceptInvitation(@PathVariable String token) {
    agencyService.acceptInvitation(AuthContext.getUserId(), token);
    return ApiResponse.ok();
  }

  @GetMapping("/members/me/profile")
  public ApiResponse<AgencyDtos.MemberSelfProfileView> getMyProfile() {
    return ApiResponse.ok(agencyService.getMyProfile(AuthContext.getUserId()));
  }

  @GetMapping("/members/me/verification/status")
  public ApiResponse<AgencyDtos.MemberVerificationStatusView> getMyVerificationStatus() {
    return ApiResponse.ok(agencyService.getMyVerificationStatus(AuthContext.getUserId()));
  }

  @PutMapping("/members/me/profile")
  public ApiResponse<Void> updateMyProfile(@Valid @RequestBody AgencyDtos.MemberProfileUpdateRequest request) {
    agencyService.updateMyProfile(AuthContext.getUserId(), request);
    return ApiResponse.ok();
  }

  @PutMapping("/members/me/avatar")
  public ApiResponse<Void> updateMyAvatar(@Valid @RequestBody AgencyDtos.MemberAvatarUpdateRequest request) {
    agencyService.updateMyAvatar(AuthContext.getUserId(), request);
    return ApiResponse.ok();
  }

  @PostMapping("/members/me/verification/submit")
  public ApiResponse<Void> submitMyVerification(@Valid @RequestBody AgencyDtos.MemberVerificationSubmitRequest request) {
    agencyService.submitMyVerification(AuthContext.getUserId(), request);
    return ApiResponse.ok();
  }

  @PutMapping("/members/me/profile/submit")
  public ApiResponse<Void> submitMyProfileForAudit() {
    agencyService.submitMyProfileForAudit(AuthContext.getUserId());
    return ApiResponse.ok();
  }

  @PutMapping("/members/me/roles")
  public ApiResponse<Void> updateMyRoles(@Valid @RequestBody AgencyDtos.MemberRolesUpdateRequest request) {
    agencyService.updateMyRoles(AuthContext.getUserId(), request);
    return ApiResponse.ok();
  }

  @PutMapping("/members/me/metrics")
  public ApiResponse<Void> updateMyMetrics(@Valid @RequestBody AgencyDtos.MemberMetricsUpdateRequest request) {
    agencyService.updateMyMetrics(AuthContext.getUserId(), request);
    return ApiResponse.ok();
  }

  @GetMapping("/members/me/workbench-access")
  public ApiResponse<AgencyDtos.MemberWorkbenchAccessView> getMyWorkbenchAccess() {
    return ApiResponse.ok(agencyService.getMyWorkbenchAccess(AuthContext.getUserId()));
  }

  @GetMapping("/discovery/members")
  public ApiResponse<List<AgencyDtos.DiscoveryMemberItem>> listDiscoveryMembers(
    @RequestParam(required = false) String roleCode,
    @RequestParam(required = false) String country,
    @RequestParam(required = false) String direction,
    @RequestParam(required = false) String city,
    @RequestParam(required = false) String serviceTag,
    @RequestParam(required = false) String budgetTag
  ) {
    return ApiResponse.ok(agencyService.listDiscoveryMembers(roleCode, country, direction, city, serviceTag, budgetTag));
  }

  @GetMapping("/discovery/members/{memberId}")
  public ApiResponse<AgencyDtos.DiscoveryMemberDetail> getDiscoveryMemberDetail(@PathVariable Long memberId) {
    return ApiResponse.ok(agencyService.getDiscoveryMemberDetail(memberId));
  }

  @GetMapping("/discovery/teams")
  public ApiResponse<List<AgencyDtos.DiscoveryTeamItem>> listDiscoveryTeams(
    @RequestParam(required = false) String keyword,
    @RequestParam(required = false) String country,
    @RequestParam(required = false) String direction,
    @RequestParam(required = false) String city,
    @RequestParam(required = false) String roleCode,
    @RequestParam(required = false) String serviceTag
  ) {
    return ApiResponse.ok(agencyService.listDiscoveryTeams(keyword, country, direction, city, roleCode, serviceTag));
  }

  @GetMapping("/discovery/teams/{teamId}")
  public ApiResponse<AgencyDtos.DiscoveryTeamDetail> getDiscoveryTeamDetail(@PathVariable Long teamId) {
    return ApiResponse.ok(agencyService.getDiscoveryTeamDetail(teamId));
  }
}
