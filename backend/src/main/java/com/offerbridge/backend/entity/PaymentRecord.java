package com.offerbridge.backend.entity;

import java.math.BigDecimal;

public class PaymentRecord {
  private Long id;
  private Long orderId;
  private String paymentNo;
  private String channel;
  private String status;
  private BigDecimal amount;
  private String gatewayTradeNo;
  private Boolean verified;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getOrderId() { return orderId; }
  public void setOrderId(Long orderId) { this.orderId = orderId; }
  public String getPaymentNo() { return paymentNo; }
  public void setPaymentNo(String paymentNo) { this.paymentNo = paymentNo; }
  public String getChannel() { return channel; }
  public void setChannel(String channel) { this.channel = channel; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public BigDecimal getAmount() { return amount; }
  public void setAmount(BigDecimal amount) { this.amount = amount; }
  public String getGatewayTradeNo() { return gatewayTradeNo; }
  public void setGatewayTradeNo(String gatewayTradeNo) { this.gatewayTradeNo = gatewayTradeNo; }
  public Boolean getVerified() { return verified; }
  public void setVerified(Boolean verified) { this.verified = verified; }
}
