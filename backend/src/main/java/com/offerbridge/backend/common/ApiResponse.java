package com.offerbridge.backend.common;

/**
 * 学习入口：后端统一响应格式。
 *
 * <p>前端 src/services/http.ts 只需要判断 code 是否为 OK，就能统一处理成功、业务错误、
 * traceId 和调试 detail。答辩时可以用它说明前后端接口契约是稳定的。</p>
 */
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
