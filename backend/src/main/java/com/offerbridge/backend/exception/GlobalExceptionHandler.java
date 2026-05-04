package com.offerbridge.backend.exception;

import com.offerbridge.backend.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Arrays;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private final Environment environment;

  public GlobalExceptionHandler(Environment environment) {
    this.environment = environment;
  }

  private boolean isDevProfile() {
    return Arrays.stream(environment.getActiveProfiles()).anyMatch("dev"::equalsIgnoreCase);
  }

  @ExceptionHandler(BizException.class)
  public ResponseEntity<ApiResponse<Void>> handleBiz(BizException ex, HttpServletRequest request) {
    String traceId = UUID.randomUUID().toString();
    boolean dev = isDevProfile();
    log.warn("[{}] BizException path={} code={} msg={}", traceId, request.getRequestURI(), ex.getCode(), ex.getMessage());
    ApiResponse<Void> body = dev
      ? ApiResponse.error(ex.getCode(), ex.getMessage(), traceId, request.getRequestURI())
      : ApiResponse.error(ex.getCode(), ex.getMessage());
    return ResponseEntity.status(resolveBizStatus(ex.getCode())).body(body);
  }

  private HttpStatus resolveBizStatus(String code) {
    if ("BIZ_AI_UNAVAILABLE".equals(code)) return HttpStatus.SERVICE_UNAVAILABLE;
    return HttpStatus.BAD_REQUEST;
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class, HttpMessageNotReadableException.class})
  public ApiResponse<Void> handleBadRequest(Exception ex, HttpServletRequest request) {
    String traceId = UUID.randomUUID().toString();
    boolean dev = isDevProfile();
    log.warn("[{}] BadRequest path={} type={} msg={}", traceId, request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage());
    return dev
      ? ApiResponse.error("BIZ_BAD_REQUEST", "请求参数不合法", traceId, ex.getMessage())
      : ApiResponse.error("BIZ_BAD_REQUEST", "请求参数不合法");
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ApiResponse<Void> handleMaxUploadSize(MaxUploadSizeExceededException ex, HttpServletRequest request) {
    String traceId = UUID.randomUUID().toString();
    boolean dev = isDevProfile();
    log.warn("[{}] MaxUploadSize path={} msg={}", traceId, request.getRequestURI(), ex.getMessage());
    return dev
      ? ApiResponse.error("BIZ_FILE_TOO_LARGE", "文件不能超过10MB", traceId, ex.getMessage())
      : ApiResponse.error("BIZ_FILE_TOO_LARGE", "文件不能超过10MB");
  }

  @ExceptionHandler(Exception.class)
  public ApiResponse<Void> handleAny(Exception ex, HttpServletRequest request) {
    String traceId = UUID.randomUUID().toString();
    boolean dev = isDevProfile();
    log.error("[{}] InternalError path={} type={} msg={}", traceId, request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage(), ex);
    return dev
      ? ApiResponse.error("BIZ_INTERNAL_ERROR", "系统异常", traceId, ex.getMessage())
      : ApiResponse.error("BIZ_INTERNAL_ERROR", "系统异常");
  }
}
