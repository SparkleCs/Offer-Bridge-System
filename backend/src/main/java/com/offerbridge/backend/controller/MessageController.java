package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.dto.MessageDtos;
import com.offerbridge.backend.security.AuthContext;
import com.offerbridge.backend.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @PostMapping("/chats/start")
  public ApiResponse<MessageDtos.ChatStartResult> startChat(@Valid @RequestBody MessageDtos.StartChatRequest request) {
    return ApiResponse.ok(messageService.startChat(AuthContext.getUserId(), request));
  }

  @GetMapping("/chats")
  public ApiResponse<MessageDtos.PagedResult<MessageDtos.ChatConversationItem>> listChatConversations(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "50") int pageSize
  ) {
    return ApiResponse.ok(messageService.listChatConversations(AuthContext.getUserId(), page, pageSize));
  }

  @GetMapping("/chats/unread")
  public ApiResponse<MessageDtos.ChatUnreadSummary> getChatUnreadSummary() {
    return ApiResponse.ok(messageService.getChatUnreadSummary(AuthContext.getUserId()));
  }

  @GetMapping("/chats/{conversationId}/messages")
  public ApiResponse<MessageDtos.PagedResult<MessageDtos.ChatMessageItem>> listChatMessages(
    @PathVariable String conversationId,
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "100") int pageSize
  ) {
    return ApiResponse.ok(messageService.listChatMessages(AuthContext.getUserId(), conversationId, page, pageSize));
  }

  @PostMapping("/chats/{conversationId}/messages")
  public ApiResponse<MessageDtos.ChatMessageItem> sendChatMessage(
    @PathVariable String conversationId,
    @Valid @RequestBody MessageDtos.SendChatMessageRequest request
  ) {
    return ApiResponse.ok(messageService.sendChatMessage(AuthContext.getUserId(), conversationId, request));
  }

  @PostMapping("/chats/{conversationId}/read")
  public ApiResponse<MessageDtos.MarkReadResult> markChatRead(@PathVariable String conversationId) {
    return ApiResponse.ok(messageService.markChatRead(AuthContext.getUserId(), conversationId));
  }
}
