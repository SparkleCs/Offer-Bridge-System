package com.offerbridge.backend.repository.forum;

import com.offerbridge.backend.entity.forum.ForumReactionDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ForumReactionRepository extends MongoRepository<ForumReactionDoc, String> {
  Optional<ForumReactionDoc> findByUserIdAndPostIdAndActionType(Long userId, String postId, String actionType);
  List<ForumReactionDoc> findByUserIdAndPostIdIn(Long userId, Collection<String> postIds);
  List<ForumReactionDoc> findByUserIdAndActionType(Long userId, String actionType, Sort sort);
  long deleteByPostId(String postId);
}
