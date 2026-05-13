package com.offerbridge.backend.config;

import com.offerbridge.backend.security.AuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {
  private final AuthInterceptor authInterceptor;
  private final AppProperties appProperties;

  public SecurityConfig(AuthInterceptor authInterceptor, AppProperties appProperties) {
    this.authInterceptor = authInterceptor;
    this.appProperties = appProperties;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 学习入口：这里定义 API 鉴权边界。公开接口直接放行，其余 /api/v1/** 交给 AuthInterceptor 校验 JWT。
    // 答辩时要强调：前端路由限制只是用户体验，后端拦截器才是真正的安全控制。
    registry.addInterceptor(authInterceptor)
      .addPathPatterns("/api/v1/**")
      .excludePathPatterns(
        "/api/v1/auth/sms/send",
        "/api/v1/auth/sms/login-or-register",
        "/api/v1/auth/password/login",
        "/api/v1/auth/admin/sms/send",
        "/api/v1/auth/admin/sms/login",
        "/api/v1/auth/refresh",
        "/api/v1/payments/alipay/notify",
        "/api/v1/agency/discovery/members",
        "/api/v1/agency/discovery/members/*",
        "/api/v1/agency/discovery/teams",
        "/api/v1/agency/discovery/teams/*",
        "/api/v1/agency/discovery/**",
        "/api/v1/reviews/discovery/**"
      );
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOriginPatterns("*")
      .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
      .allowedHeaders("*")
      .allowCredentials(false);
  }

  @Override
  public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
    String dir = appProperties.getUpload().getLocalDir();
    if (dir == null || dir.isBlank()) {
      dir = "uploads";
    }
    java.nio.file.Path absolute = java.nio.file.Paths.get(dir).toAbsolutePath().normalize();
    registry.addResourceHandler("/uploads/**")
      .addResourceLocations("file:" + absolute + "/");
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }
}
