package com.offerbridge.backend.service;

import com.offerbridge.backend.dto.MessageDtos;

public interface MessageService {
  MessageDtos.PagedResult<MessageDtos.SystemNotificationItem> listSystemNotifications(Long userId, int page, int pageSize);
  MessageDtos.MarkReadResult markSystemNotificationsRead(Long userId, MessageDtos.MarkReadRequest request);
}

