package com.offerbridge.backend.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationServiceImplTest {
  @Test
  void hasCountryMatchRequiresOverlapBetweenStudentTargetsAndTeamScope() {
    assertFalse(RecommendationServiceImpl.hasCountryMatch("美国", "英国、新西兰"));
    assertTrue(RecommendationServiceImpl.hasCountryMatch("英国", "英国、新西兰"));
    assertTrue(RecommendationServiceImpl.hasCountryMatch("美国、英国", "英国/新西兰"));
  }

  @Test
  void hasCountryMatchSupportsCountryAliases() {
    assertTrue(RecommendationServiceImpl.hasCountryMatch("澳大利亚", "澳洲"));
    assertTrue(RecommendationServiceImpl.hasCountryMatch("澳洲", "澳大利亚"));
    assertTrue(RecommendationServiceImpl.hasCountryMatch("中国香港", "香港"));
    assertTrue(RecommendationServiceImpl.hasCountryMatch("香港", "中国香港"));
  }

  @Test
  void hasCountryMatchRejectsMissingCountriesOrTeamScope() {
    assertFalse(RecommendationServiceImpl.hasCountryMatch(null, "英国、新西兰"));
    assertFalse(RecommendationServiceImpl.hasCountryMatch("", "英国、新西兰"));
    assertFalse(RecommendationServiceImpl.hasCountryMatch("美国", null));
    assertFalse(RecommendationServiceImpl.hasCountryMatch("美国", ""));
  }
}
