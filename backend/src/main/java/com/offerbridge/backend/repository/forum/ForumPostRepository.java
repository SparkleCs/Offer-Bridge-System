package com.offerbridge.backend.repository.forum;

import com.offerbridge.backend.entity.forum.ForumPostDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForumPostRepository extends MongoRepository<ForumPostDoc, String> {
}
