package com.offerbridge.backend.repository.chat;

import com.offerbridge.backend.entity.chat.ChatMessageDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessageDoc, String> {
}
