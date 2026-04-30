package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.dto.AgencyDtos;
import com.offerbridge.backend.dto.RecommendationDtos;
import com.offerbridge.backend.security.AuthContext;
import com.offerbridge.backend.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {
  private final RecommendationService recommendationService;

  public RecommendationController(RecommendationService recommendationService) {
    this.recommendationService = recommendationService;
  }

  @GetMapping("/student/agency-teams")
  public ApiResponse<List<RecommendationDtos.StudentAgencyTeamRecommendationItem>> listStudentAgencyTeamRecommendations() {
    return ApiResponse.ok(recommendationService.listStudentAgencyTeamRecommendations(AuthContext.getUserId()));
  }

  @GetMapping("/agency/team-students")
  public ApiResponse<AgencyDtos.PagedResult<RecommendationDtos.AgencyTeamStudentRecommendationItem>> listAgencyTeamStudentRecommendations(
    @RequestParam Long teamId,
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "12") int pageSize
  ) {
    return ApiResponse.ok(recommendationService.listAgencyTeamStudentRecommendations(AuthContext.getUserId(), teamId, page, pageSize));
  }
}
