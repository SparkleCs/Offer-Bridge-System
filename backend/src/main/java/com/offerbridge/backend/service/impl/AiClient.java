package com.offerbridge.backend.service.impl;

import com.offerbridge.backend.dto.AiDtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AiClient {
  private static final Logger log = LoggerFactory.getLogger(AiClient.class);

  private final RestTemplate restTemplate;
  private final String serviceUrl;

  public AiClient(@Value("${app.ai.service-url:http://127.0.0.1:8001}") String serviceUrl) {
    // 学习入口：Java 后端通过这个客户端访问独立的 Python AI 服务。
    // 超时设置避免 AI 服务不可用时拖垮主业务接口，失败时返回 null 交给 Service 转成业务错误。
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(3000);
    requestFactory.setReadTimeout(30000);
    this.restTemplate = new RestTemplate(requestFactory);
    this.serviceUrl = serviceUrl == null ? "http://127.0.0.1:8001" : serviceUrl.replaceAll("/+$", "");
  }

  public AiDtos.AiRecommendationResponse recommendations(AiDtos.AiRecommendationRequest request) {
    return post("/ai/recommendations", request);
  }

  public AiDtos.AiRecommendationResponse programAnalysis(AiDtos.AiRecommendationRequest request) {
    return post("/ai/program-analysis", request);
  }

  public AiDtos.AiUsSchoolRecommendationResponse usSchoolRecommendations(AiDtos.AiUsSchoolRecommendationRequest request) {
    return post("/ai/us-school-recommendations", request, AiDtos.AiUsSchoolRecommendationResponse.class);
  }

  private AiDtos.AiRecommendationResponse post(String path, AiDtos.AiRecommendationRequest request) {
    return post(path, request, AiDtos.AiRecommendationResponse.class);
  }

  private <T> T post(String path, Object request, Class<T> responseType) {
    try {
      // 所有 AI 调用都用 JSON HTTP 请求，便于 Python FastAPI 和 Java Spring Boot 解耦部署。
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      return restTemplate.postForObject(
        serviceUrl + path,
        new HttpEntity<>(request, headers),
        responseType
      );
    } catch (HttpStatusCodeException ex) {
      log.warn(
        "AI service returned HTTP error: url={} status={} body={}",
        serviceUrl + path,
        ex.getStatusCode().value(),
        abbreviate(ex.getResponseBodyAsString())
      );
      return null;
    } catch (ResourceAccessException ex) {
      log.warn("AI service is unreachable: url={} reason={}", serviceUrl + path, ex.getMessage());
      return null;
    } catch (RestClientException ex) {
      log.warn("AI service call failed: url={} reason={}", serviceUrl + path, ex.getMessage(), ex);
      return null;
    }
  }

  private String abbreviate(String body) {
    if (body == null || body.isBlank()) return "";
    return body.length() <= 500 ? body : body.substring(0, 500) + "...";
  }
}
