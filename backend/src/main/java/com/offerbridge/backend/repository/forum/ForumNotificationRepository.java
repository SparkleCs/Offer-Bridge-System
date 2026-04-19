package com.offerbridge.backend.repository.forum;

import com.offerbridge.backend.entity.forum.ForumNotificationDoc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ForumNotificationRepository extends MongoRepository<ForumNotificationDoc, String> {
  long countByReceiverUserIdAndRead(Long receiverUserId, boolean read);
  List<ForumNotificationDoc> findByReceiverUserId(Long receiverUserId, Pageable pageable);
  long deleteByPostId(String postId);
}
