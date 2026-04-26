package com.offerbridge.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface RefundRequestMapper {
  int insertOne(@Param("orderId") Long orderId,
                @Param("requestedBy") Long requestedBy,
                @Param("refundAmount") BigDecimal refundAmount,
                @Param("reason") String reason);
}
