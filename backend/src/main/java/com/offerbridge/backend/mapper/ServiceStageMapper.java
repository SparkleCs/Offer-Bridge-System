package com.offerbridge.backend.mapper;

import com.offerbridge.backend.dto.OrderDtos;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServiceStageMapper {
  int insertStage(@Param("caseId") Long caseId,
                  @Param("stageKey") String stageKey,
                  @Param("stageName") String stageName,
                  @Param("stageOrder") int stageOrder,
                  @Param("status") String status,
                  @Param("assignedMemberId") Long assignedMemberId);
  List<OrderDtos.StageItem> listByOrderId(@Param("orderId") Long orderId);
  OrderDtos.StageItem findById(@Param("id") Long id);
  int submitStage(@Param("id") Long id,
                  @Param("caseId") Long caseId,
                  @Param("deliverableText") String deliverableText,
                  @Param("deliverableUrl") String deliverableUrl);
  int completeStage(@Param("id") Long id, @Param("caseId") Long caseId);
  int rejectStage(@Param("id") Long id, @Param("caseId") Long caseId, @Param("feedback") String feedback);
  int startNextStage(@Param("caseId") Long caseId, @Param("stageOrder") int stageOrder);
}
