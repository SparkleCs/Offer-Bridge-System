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
    // AI 亮点入口：后端先收集学生画像和候选院校，再调用 Python FastAPI 服务生成择校报告。
    return ApiResponse.ok(aiService.generateRecommendations(AuthContext.getUserId()));
  }

  @GetMapping("/reports/latest")
  public ApiResponse<AiDtos.AiReportView> getLatestReport() {
    return ApiResponse.ok(aiService.getLatestReport(AuthContext.getUserId()));
  }

  @PostMapping("/programs/{programId}/analysis")
  public ApiResponse<AiDtos.AiReportView> analyzeProgram(@PathVariable Long programId) {
    // 单项目分析用于解释某个具体项目的申请竞争力，和批量推荐共用同一套学生画像输入。
    return ApiResponse.ok(aiService.analyzeProgram(AuthContext.getUserId(), programId));
  }
}
