package com.offerbridge.backend.service.impl;

import com.offerbridge.backend.dto.MessageDtos;
import com.offerbridge.backend.mapper.SystemNotificationMapper;
import com.offerbridge.backend.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
  private final SystemNotificationMapper systemNotificationMapper;

  public MessageServiceImpl(SystemNotificationMapper systemNotificationMapper) {
    this.systemNotificationMapper = systemNotificationMapper;
  }

  @Override
  public MessageDtos.PagedResult<MessageDtos.SystemNotificationItem> listSystemNotifications(Long userId, int page, int pageSize) {
    List<MessageDtos.SystemNotificationItem> all = systemNotificationMapper.listByUserId(userId);
    return toPaged(all, page, pageSize, systemNotificationMapper.countUnreadByUserId(userId));
  }

  @Override
  public MessageDtos.MarkReadResult markSystemNotificationsRead(Long userId, MessageDtos.MarkReadRequest request) {
    int updated;
    if (Boolean.TRUE.equals(request.getMarkAll())) {
      updated = systemNotificationMapper.markAllRead(userId);
    } else {
      List<Long> ids = request.getIds() == null ? List.of() : request.getIds().stream().distinct().toList();
      if (ids.isEmpty()) {
        updated = 0;
      } else {
        updated = systemNotificationMapper.markReadByIds(userId, ids);
      }
    }
    MessageDtos.MarkReadResult result = new MessageDtos.MarkReadResult();
    result.setUpdatedCount(updated);
    return result;
  }

  private MessageDtos.PagedResult<MessageDtos.SystemNotificationItem> toPaged(List<MessageDtos.SystemNotificationItem> all,
                                                                               int page,
                                                                               int pageSize,
                                                                               long unreadCount) {
    int safePage = Math.max(1, page);
    int safePageSize = Math.max(1, Math.min(pageSize, 100));
    int from = (safePage - 1) * safePageSize;
    int to = Math.min(from + safePageSize, all.size());
    List<MessageDtos.SystemNotificationItem> records = from >= all.size() ? new ArrayList<>() : all.subList(from, to);
    MessageDtos.PagedResult<MessageDtos.SystemNotificationItem> result = new MessageDtos.PagedResult<>();
    result.setRecords(records);
    result.setTotal(all.size());
    result.setPage(safePage);
    result.setPageSize(safePageSize);
    result.setUnreadCount(unreadCount);
    return result;
  }
}

