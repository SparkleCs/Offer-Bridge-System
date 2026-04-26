package com.offerbridge.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface PaymentRecordMapper {
  int insertCreated(@Param("orderId") Long orderId,
                    @Param("paymentNo") String paymentNo,
                    @Param("amount") BigDecimal amount);
  int markPaid(@Param("paymentNo") String paymentNo,
               @Param("gatewayTradeNo") String gatewayTradeNo,
               @Param("payload") String payload);
  int countPaidByOrderId(@Param("orderId") Long orderId);
}
