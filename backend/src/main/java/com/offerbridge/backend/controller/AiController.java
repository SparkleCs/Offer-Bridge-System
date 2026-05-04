package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.dto.AiDtos;
import com.offerbridge.backend.security.AuthContext;
import com.offerbridge.backend.service.AiService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@Validated
public class AiController {
  private final AiService aiService;

  public AiController(AiService aiService) {
    this.aiService = aiService;
  }

  @PostMapping("/recommendations")
  public ApiResponse<AiDtos.AiReportView> generateRecommendations() {
    return ApiResponse.ok(aiService.generateRecommendations(AuthContext.getUserId()));
  }

  @GetMapping("/reports/latest")
  public ApiResponse<AiDtos.AiReportView> getLatestReport() {
    return ApiResponse.ok(aiService.getLatestReport(AuthContext.getUserId()));
  }

  @PostMapping("/programs/{programId}/analysis")
  public ApiResponse<AiDtos.AiReportView> analyzeProgram(@PathVariable Long programId) {
    return ApiResponse.ok(aiService.analyzeProgram(AuthContext.getUserId(), programId));
  }
}
