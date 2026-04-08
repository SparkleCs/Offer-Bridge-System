package com.offerbridge.backend.common;

public record ApiResponse<T>(String code, String message, T data, String traceId, String detail) {
  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>("OK", "success", data, null, null);
  }

  public static ApiResponse<Void> ok() {
    return new ApiResponse<>("OK", "success", null, null, null);
  }

  public static ApiResponse<Void> error(String code, String message) {
    return new ApiResponse<>(code, message, null, null, null);
  }

  public static ApiResponse<Void> error(String code, String message, String traceId, String detail) {
    return new ApiResponse<>(code, message, null, traceId, detail);
  }
}
