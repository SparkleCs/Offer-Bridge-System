package com.offerbridge.backend.mapper;

import com.offerbridge.backend.dto.OrderDtos;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServiceTodoMapper {
  int insertTodo(@Param("caseId") Long caseId,
                 @Param("stageId") Long stageId,
                 @Param("title") String title,
                 @Param("description") String description,
                 @Param("ownerRole") String ownerRole);
  List<OrderDtos.TodoItem> listByOrderId(@Param("orderId") Long orderId);
  int markCompleted(@Param("id") Long id, @Param("caseId") Long caseId);
  int confirmCompleted(@Param("id") Long id, @Param("caseId") Long caseId);
}
