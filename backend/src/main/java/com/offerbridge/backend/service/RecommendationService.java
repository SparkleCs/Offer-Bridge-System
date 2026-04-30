package com.offerbridge.backend.service;

import com.offerbridge.backend.dto.AgencyDtos;
import com.offerbridge.backend.dto.RecommendationDtos;

import java.util.List;

public interface RecommendationService {
  List<RecommendationDtos.StudentAgencyTeamRecommendationItem> listStudentAgencyTeamRecommendations(Long userId);

  AgencyDtos.PagedResult<RecommendationDtos.AgencyTeamStudentRecommendationItem> listAgencyTeamStudentRecommendations(
    Long userId,
    Long teamId,
    int page,
    int pageSize
  );
}
