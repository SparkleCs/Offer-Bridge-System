package com.offerbridge.backend.service.impl;

import com.offerbridge.backend.dto.AiDtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AiClient {
  private static final Logger log = LoggerFactory.getLogger(AiClient.class);

  private final RestTemplate restTemplate = new RestTemplate();
  private final String serviceUrl;

  public AiClient(@Value("${app.ai.service-url:http://127.0.0.1:8001}") String serviceUrl) {
    this.serviceUrl = serviceUrl == null ? "http://127.0.0.1:8001" : serviceUrl.replaceAll("/+$", "");
  }

  public AiDtos.AiRecommendationResponse recommendations(AiDtos.AiRecommendationRequest request) {
    return post("/ai/recommendations", request);
  }

  public AiDtos.AiRecommendationResponse programAnalysis(AiDtos.AiRecommendationRequest request) {
    return post("/ai/program-analysis", request);
  }

  private AiDtos.AiRecommendationResponse post(String path, AiDtos.AiRecommendationRequest request) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      return restTemplate.postForObject(
        serviceUrl + path,
        new HttpEntity<>(request, headers),
        AiDtos.AiRecommendationResponse.class
      );
    } catch (RestClientException ex) {
      log.warn("AI service call failed: {}", path, ex);
      return null;
    }
  }
}
