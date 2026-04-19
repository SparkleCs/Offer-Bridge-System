package com.offerbridge.backend.repository.forum;

import com.offerbridge.backend.entity.forum.ForumCommentDoc;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ForumCommentRepository extends MongoRepository<ForumCommentDoc, String> {
  List<ForumCommentDoc> findByPostIdAndStatus(String postId, String status, Sort sort);
}
