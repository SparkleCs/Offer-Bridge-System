package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.dto.StudentDtos;
import com.offerbridge.backend.security.AuthContext;
import com.offerbridge.backend.service.StudentFavoriteAgencyTeamService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student/favorite-agency-teams")
public class StudentFavoriteAgencyTeamController {
  private final StudentFavoriteAgencyTeamService favoriteAgencyTeamService;

  public StudentFavoriteAgencyTeamController(StudentFavoriteAgencyTeamService favoriteAgencyTeamService) {
    this.favoriteAgencyTeamService = favoriteAgencyTeamService;
  }

  @GetMapping
  public ApiResponse<List<StudentDtos.FavoriteAgencyTeamItem>> listFavorites() {
    return ApiResponse.ok(favoriteAgencyTeamService.listFavorites(AuthContext.getUserId()));
  }

  @PostMapping("/{teamId}")
  public ApiResponse<StudentDtos.FavoriteAgencyTeamItem> addFavorite(@PathVariable Long teamId) {
    return ApiResponse.ok(favoriteAgencyTeamService.addFavorite(AuthContext.getUserId(), teamId));
  }

  @DeleteMapping("/{teamId}")
  public ApiResponse<StudentDtos.FavoriteAgencyTeamItem> removeFavorite(@PathVariable Long teamId) {
    return ApiResponse.ok(favoriteAgencyTeamService.removeFavorite(AuthContext.getUserId(), teamId));
  }
}
