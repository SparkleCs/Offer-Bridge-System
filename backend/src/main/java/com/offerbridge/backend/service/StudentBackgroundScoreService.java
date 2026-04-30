package com.offerbridge.backend.service;

import com.offerbridge.backend.entity.StudentBackgroundScore;

public interface StudentBackgroundScoreService {
  StudentBackgroundScore refreshScore(Long userId);
  StudentBackgroundScore getOrRefreshScore(Long userId);
}
