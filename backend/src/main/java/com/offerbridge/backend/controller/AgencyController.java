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

  @PostMapping("/teams")
  public ApiResponse<AgencyDtos.TeamView> createTeam(@Valid @RequestBody AgencyDtos.TeamCreateRequest request) {
    return ApiResponse.ok(agencyService.createTeam(AuthContext.getUserId(), request));
  }

  @GetMapping("/teams")
  public ApiResponse<List<AgencyDtos.TeamView>> listTeams() {
    return ApiResponse.ok(agencyService.listTeams(AuthContext.getUserId()));
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

  @PutMapping("/members/me/profile")
  public ApiResponse<Void> updateMyProfile(@Valid @RequestBody AgencyDtos.MemberProfileUpdateRequest request) {
    agencyService.updateMyProfile(AuthContext.getUserId(), request);
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
