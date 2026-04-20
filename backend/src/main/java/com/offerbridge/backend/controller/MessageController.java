package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.dto.MessageDtos;
import com.offerbridge.backend.security.AuthContext;
import com.offerbridge.backend.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messages")
@Validated
public class MessageController {
  private final MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @GetMapping("/system")
  public ApiResponse<MessageDtos.PagedResult<MessageDtos.SystemNotificationItem>> listSystemNotifications(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "20") int pageSize
  ) {
    return ApiResponse.ok(messageService.listSystemNotifications(AuthContext.getUserId(), page, pageSize));
  }

  @PostMapping("/system/read")
  public ApiResponse<MessageDtos.MarkReadResult> markSystemNotificationsRead(@Valid @RequestBody MessageDtos.MarkReadRequest request) {
    return ApiResponse.ok(messageService.markSystemNotificationsRead(AuthContext.getUserId(), request));
  }
}

