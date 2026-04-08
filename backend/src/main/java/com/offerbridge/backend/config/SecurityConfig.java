package com.offerbridge.backend.config;

import com.offerbridge.backend.security.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {
  private final AuthInterceptor authInterceptor;

  public SecurityConfig(AuthInterceptor authInterceptor) {
    this.authInterceptor = authInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authInterceptor)
      .addPathPatterns("/api/v1/**")
      .excludePathPatterns(
        "/api/v1/auth/sms/send",
        "/api/v1/auth/sms/login-or-register",
        "/api/v1/auth/refresh"
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

}
