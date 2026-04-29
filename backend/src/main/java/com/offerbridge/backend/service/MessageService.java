package com.offerbridge.backend.service;

import com.offerbridge.backend.dto.MessageDtos;

public interface MessageService {
  MessageDtos.PagedResult<MessageDtos.SystemNotificationItem> listSystemNotifications(Long userId, int page, int pageSize);
  MessageDtos.MarkReadResult markSystemNotificationsRead(Long userId, MessageDtos.MarkReadRequest request);
  MessageDtos.ChatStartResult startChat(Long userId, MessageDtos.StartChatRequest request);
  MessageDtos.ChatStartResult agentStartChat(Long userId, MessageDtos.AgentStartChatRequest request);
  MessageDtos.PagedResult<MessageDtos.ChatConversationItem> listChatConversations(Long userId, int page, int pageSize, String filter);
  MessageDtos.PagedResult<MessageDtos.ChatMessageItem> listChatMessages(Long userId, String conversationId, int page, int pageSize);
  MessageDtos.ChatMessageItem sendChatMessage(Long userId, String conversationId, MessageDtos.SendChatMessageRequest request);
  MessageDtos.ChatMessageItem startChatAction(Long userId, String conversationId, MessageDtos.ChatActionRequest request);
  MessageDtos.ChatMessageItem respondChatAction(Long userId, String conversationId, String actionId, MessageDtos.ChatActionRespondRequest request);
  MessageDtos.StudentAcademicResumeView getStudentResume(Long userId, String conversationId);
  MessageDtos.ContactExchangeView getExchangedContact(Long userId, String conversationId, String contactType);
  MessageDtos.MarkReadResult markChatRead(Long userId, String conversationId);
  MessageDtos.ChatConversationItem starChatConversation(Long userId, String conversationId, boolean starred);
  MessageDtos.ChatUnreadSummary getChatUnreadSummary(Long userId);
}
