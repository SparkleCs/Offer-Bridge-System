package com.offerbridge.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
  private final Jwt jwt = new Jwt();
  private final Sms sms = new Sms();
  private final Upload upload = new Upload();
  private final Alipay alipay = new Alipay();

  public Jwt getJwt() {
    return jwt;
  }

  public Sms getSms() {
    return sms;
  }

  public Upload getUpload() {
    return upload;
  }

  public Alipay getAlipay() {
    return alipay;
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

  public static class Upload {
    private String localDir = "uploads";

    public String getLocalDir() { return localDir; }
    public void setLocalDir(String localDir) { this.localDir = localDir; }
  }

  public static class Alipay {
    private boolean enabled;
    private String appId;
    private String gatewayUrl;
    private String signType = "RSA2";
    private String charset = "UTF-8";
    private String format = "json";
    private String notifyUrl;
    private String returnUrl;
    private String appPrivateKey;
    private String alipayPublicKey;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getGatewayUrl() { return gatewayUrl; }
    public void setGatewayUrl(String gatewayUrl) { this.gatewayUrl = gatewayUrl; }
    public String getSignType() { return signType; }
    public void setSignType(String signType) { this.signType = signType; }
    public String getCharset() { return charset; }
    public void setCharset(String charset) { this.charset = charset; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String getNotifyUrl() { return notifyUrl; }
    public void setNotifyUrl(String notifyUrl) { this.notifyUrl = notifyUrl; }
    public String getReturnUrl() { return returnUrl; }
    public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }
    public String getAppPrivateKey() { return appPrivateKey; }
    public void setAppPrivateKey(String appPrivateKey) { this.appPrivateKey = appPrivateKey; }
    public String getAlipayPublicKey() { return alipayPublicKey; }
    public void setAlipayPublicKey(String alipayPublicKey) { this.alipayPublicKey = alipayPublicKey; }
  }
}
