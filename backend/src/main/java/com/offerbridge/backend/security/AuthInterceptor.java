package com.offerbridge.backend.security;

import com.offerbridge.backend.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
  private final JwtService jwtService;

  public AuthInterceptor(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      return true;
    }
    String uri = request.getRequestURI();
    // Discovery endpoints are public for pre-sign visibility.
    if (uri != null && uri.startsWith("/api/v1/agency/discovery/")) {
      return true;
    }

    String auth = request.getHeader("Authorization");
    if (auth == null || !auth.startsWith("Bearer ")) {
      throw new BizException("BIZ_UNAUTHORIZED", "未登录或登录已过期");
    }

    String token = auth.substring(7);
    try {
      Long userId = jwtService.parseUserId(token);
      AuthContext.setUserId(userId);
      return true;
    } catch (Exception ex) {
      throw new BizException("BIZ_UNAUTHORIZED", "未登录或登录已过期");
    }
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    AuthContext.clear();
  }
}
