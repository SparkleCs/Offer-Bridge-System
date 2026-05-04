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
    // Discovery reads are public, but an optional token lets the UI enrich results with viewer state.
    if (isPublicAgencyDiscoveryRead(request, uri)) {
      String auth = request.getHeader("Authorization");
      if (auth != null && auth.startsWith("Bearer ")) {
        try {
          AuthContext.setUserId(jwtService.parseUserId(auth.substring(7)));
        } catch (Exception ignored) {
          AuthContext.clear();
        }
      }
      return true;
    }
    if (uri != null && uri.startsWith("/api/v1/reviews/discovery/") && "GET".equalsIgnoreCase(request.getMethod())) {
      return true;
    }

    String auth = request.getHeader("Authorization");
    if (isPublicReadEndpoint(request, uri)) {
      if (auth != null && auth.startsWith("Bearer ")) {
        try {
          AuthContext.setUserId(jwtService.parseUserId(auth.substring(7)));
        } catch (Exception ignored) {
          AuthContext.clear();
        }
      }
      return true;
    }

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

  private boolean isPublicReadEndpoint(HttpServletRequest request, String uri) {
    if (!"GET".equalsIgnoreCase(request.getMethod()) || uri == null) {
      return false;
    }
    if (uri.equals("/api/v1/universities/meta")) return true;
    if (uri.startsWith("/api/v1/reviews/discovery/")) return true;
    if (uri.startsWith("/api/v1/universities/schools")) return true;
    if (uri.startsWith("/api/v1/universities/programs")) return true;
    if (uri.equals("/api/v1/forum/posts")) {
      String mode = request.getParameter("mode");
      return mode == null || !"MINE".equalsIgnoreCase(mode);
    }
    if (uri.matches("^/api/v1/forum/posts/[^/]+$")) return true;
    return uri.matches("^/api/v1/forum/posts/[^/]+/comments$");
  }

  private boolean isPublicAgencyDiscoveryRead(HttpServletRequest request, String uri) {
    if (!"GET".equalsIgnoreCase(request.getMethod()) || uri == null) {
      return false;
    }
    if (!uri.startsWith("/api/v1/agency/discovery/")) {
      return false;
    }
    return !uri.equals("/api/v1/agency/discovery/favorite-teams");
  }
}
