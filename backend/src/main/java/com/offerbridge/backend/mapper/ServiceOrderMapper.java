package com.offerbridge.backend.mapper;

import com.offerbridge.backend.dto.OrderDtos;
import com.offerbridge.backend.entity.ServiceOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ServiceOrderMapper {
  int insertOne(ServiceOrder order);
  ServiceOrder findById(@Param("id") Long id);
  ServiceOrder findByIdForUpdate(@Param("id") Long id);
  OrderDtos.OrderSummary findStudentSummary(@Param("id") Long id, @Param("studentUserId") Long studentUserId);
  OrderDtos.OrderSummary findSummaryById(@Param("id") Long id);
  OrderDtos.AgentOrderSummary findAgentSummaryById(@Param("id") Long id);
  ServiceOrder findLatestByConversationKeys(@Param("studentUserId") Long studentUserId, @Param("orgId") Long orgId, @Param("teamId") Long teamId);
  List<OrderDtos.OrderSummary> listByStudent(@Param("studentUserId") Long studentUserId);
  List<OrderDtos.AgentOrderSummary> listByOrg(@Param("orgId") Long orgId);
  int quoteOrder(@Param("id") Long id,
                 @Param("orgId") Long orgId,
                 @Param("serviceTitle") String serviceTitle,
                 @Param("quoteDesc") String quoteDesc,
                 @Param("finalAmount") BigDecimal finalAmount,
                 @Param("assignedMemberId") Long assignedMemberId);
  int closeStudentOrder(@Param("id") Long id, @Param("studentUserId") Long studentUserId);
  int markPaid(@Param("id") Long id);
  int markInService(@Param("id") Long id);
  int markCompleted(@Param("id") Long id);
  int requestRefund(@Param("id") Long id, @Param("studentUserId") Long studentUserId);
}
