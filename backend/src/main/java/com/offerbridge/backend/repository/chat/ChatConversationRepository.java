package com.offerbridge.backend.repository.chat;

import com.offerbridge.backend.entity.chat.ChatConversationDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatConversationRepository extends MongoRepository<ChatConversationDoc, String> {
  Optional<ChatConversationDoc> findByStudentUserIdAndTeamIdAndAgentMemberId(Long studentUserId, Long teamId, Long agentMemberId);
}
