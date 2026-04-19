package com.offerbridge.backend.service;

import com.offerbridge.backend.dto.ForumDtos;

public interface ForumService {
  ForumDtos.PostItem createPost(Long userId, ForumDtos.CreatePostRequest request);
  ForumDtos.PostItem updatePost(Long userId, String postId, ForumDtos.CreatePostRequest request);
  void deletePost(Long userId, String postId);
  ForumDtos.PostListView listPosts(Long userId, String mode, String channel, String reaction, String keyword, Integer page, Integer pageSize);
  ForumDtos.PostItem getPostDetail(Long userId, String postId);
  ForumDtos.InteractionStateView likePost(Long userId, String postId);
  ForumDtos.InteractionStateView unlikePost(Long userId, String postId);
  ForumDtos.InteractionStateView favoritePost(Long userId, String postId);
  ForumDtos.InteractionStateView unfavoritePost(Long userId, String postId);
  ForumDtos.CommentItem addComment(Long userId, String postId, ForumDtos.CreateCommentRequest request);
  ForumDtos.CommentListView listComments(Long userId, String postId);
  ForumDtos.ShareView sharePost(Long userId, String postId, String origin);
  ForumDtos.NotificationListView listNotifications(Long userId, Integer page, Integer pageSize);
  ForumDtos.NotificationReadResult markNotificationsRead(Long userId, ForumDtos.MarkNotificationsReadRequest request);
}
