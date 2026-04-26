package com.offerbridge.backend.controller;

import com.offerbridge.backend.dto.MessageDtos;
import com.offerbridge.backend.service.MessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatSocketController {
  private final MessageService messageService;

  public ChatSocketController(MessageService messageService) {
    this.messageService = messageService;
  }

  @MessageMapping("/chats/{conversationId}/send")
  public void send(@DestinationVariable String conversationId, MessageDtos.SendChatMessageRequest request, Principal principal) {
    messageService.sendChatMessage(Long.valueOf(principal.getName()), conversationId, request);
  }
}
