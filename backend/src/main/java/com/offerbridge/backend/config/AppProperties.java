package com.offerbridge.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
  private final Jwt jwt = new Jwt();
  private final Sms sms = new Sms();

  public Jwt getJwt() {
    return jwt;
  }

  public Sms getSms() {
    return sms;
  }

  public static class Jwt {
    private String issuer;
    private long accessExpireSeconds;
    private long refreshExpireSeconds;
    private String secret;

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public long getAccessExpireSeconds() { return accessExpireSeconds; }
    public void setAccessExpireSeconds(long accessExpireSeconds) { this.accessExpireSeconds = accessExpireSeconds; }
    public long getRefreshExpireSeconds() { return refreshExpireSeconds; }
    public void setRefreshExpireSeconds(long refreshExpireSeconds) { this.refreshExpireSeconds = refreshExpireSeconds; }
    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
  }

  public static class Sms {
    private boolean mockEnabled;
    private long codeTtlSeconds;
    private long sendIntervalSeconds;

    public boolean isMockEnabled() { return mockEnabled; }
    public void setMockEnabled(boolean mockEnabled) { this.mockEnabled = mockEnabled; }
    public long getCodeTtlSeconds() { return codeTtlSeconds; }
    public void setCodeTtlSeconds(long codeTtlSeconds) { this.codeTtlSeconds = codeTtlSeconds; }
    public long getSendIntervalSeconds() { return sendIntervalSeconds; }
    public void setSendIntervalSeconds(long sendIntervalSeconds) { this.sendIntervalSeconds = sendIntervalSeconds; }
  }
}
