package com.offerbridge.backend.mapper;

import com.offerbridge.backend.dto.OrderDtos;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServiceStageAttachmentMapper {
  List<OrderDtos.StageAttachment> listByOrderId(@Param("orderId") Long orderId);
  int deleteByStageId(@Param("stageId") Long stageId);
  int insertOne(@Param("stageId") Long stageId,
                @Param("fileName") String fileName,
                @Param("fileUrl") String fileUrl,
                @Param("contentType") String contentType,
                @Param("mimeType") String mimeType,
                @Param("sizeBytes") Long sizeBytes,
                @Param("uploadedByUserId") Long uploadedByUserId,
                @Param("sortOrder") int sortOrder);
}
