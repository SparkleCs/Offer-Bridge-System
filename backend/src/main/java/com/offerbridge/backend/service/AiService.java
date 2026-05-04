package com.offerbridge.backend.service;

import com.offerbridge.backend.dto.AiDtos;

public interface AiService {
  AiDtos.AiReportView generateRecommendations(Long userId);
  AiDtos.AiReportView getLatestReport(Long userId);
  AiDtos.AiReportView analyzeProgram(Long userId, Long programId);
}
