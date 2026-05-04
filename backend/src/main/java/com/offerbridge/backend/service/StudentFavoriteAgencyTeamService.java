package com.offerbridge.backend.service;

import com.offerbridge.backend.dto.StudentDtos;

import java.util.List;

public interface StudentFavoriteAgencyTeamService {
  List<StudentDtos.FavoriteAgencyTeamItem> listFavorites(Long userId);
  StudentDtos.FavoriteAgencyTeamItem addFavorite(Long userId, Long teamId);
  StudentDtos.FavoriteAgencyTeamItem removeFavorite(Long userId, Long teamId);
}
