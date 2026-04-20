package com.offerbridge.backend.mapper;

import com.offerbridge.backend.dto.MessageDtos;
import com.offerbridge.backend.entity.SystemNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SystemNotificationMapper {
  int insertOne(SystemNotification entity);
  List<MessageDtos.SystemNotificationItem> listByUserId(@Param("userId") Long userId);
  long countUnreadByUserId(@Param("userId") Long userId);
  int markAllRead(@Param("userId") Long userId);
  int markReadByIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);
  List<MessageDtos.SystemNotificationItem> listAll();
}

