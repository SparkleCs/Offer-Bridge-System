package com.offerbridge.backend.config;

import com.offerbridge.backend.security.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
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
    registry.addInterceptor(authInterceptor)
      .addPathPatterns("/api/v1/**")
      .excludePathPatterns(
        "/api/v1/auth/sms/send",
        "/api/v1/auth/sms/login-or-register",
        "/api/v1/auth/admin/sms/send",
        "/api/v1/auth/admin/sms/login",
        "/api/v1/auth/refresh",
        "/api/v1/payments/alipay/notify",
        "/api/v1/agency/discovery/members",
        "/api/v1/agency/discovery/members/*",
        "/api/v1/agency/discovery/teams",
        "/api/v1/agency/discovery/teams/*",
        "/api/v1/agency/discovery/**"
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

}
