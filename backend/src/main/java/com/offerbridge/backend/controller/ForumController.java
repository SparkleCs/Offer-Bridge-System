package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.dto.ForumDtos;
import com.offerbridge.backend.security.AuthContext;
import com.offerbridge.backend.service.ForumService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/forum")
@Validated
public class ForumController {
  private final ForumService forumService;

  public ForumController(ForumService forumService) {
    this.forumService = forumService;
  }

  @PostMapping("/posts")
  public ApiResponse<ForumDtos.PostItem> createPost(@Valid @RequestBody ForumDtos.CreatePostRequest request) {
    return ApiResponse.ok(forumService.createPost(AuthContext.getUserId(), request));
  }

  @GetMapping("/posts")
  public ApiResponse<ForumDtos.PostListView> listPosts(
    @RequestParam(value = "channel", required = false) String channel,
    @RequestParam(value = "keyword", required = false) String keyword,
    @RequestParam(value = "page", required = false) Integer page,
    @RequestParam(value = "pageSize", required = false) Integer pageSize
  ) {
    return ApiResponse.ok(forumService.listPosts(AuthContext.getUserId(), channel, keyword, page, pageSize));
  }

  @GetMapping("/posts/{postId}")
  public ApiResponse<ForumDtos.PostItem> getPost(@PathVariable("postId") String postId) {
    return ApiResponse.ok(forumService.getPostDetail(AuthContext.getUserId(), postId));
  }

  @PostMapping("/posts/{postId}/like")
  public ApiResponse<ForumDtos.InteractionStateView> likePost(@PathVariable("postId") String postId) {
    return ApiResponse.ok(forumService.likePost(AuthContext.getUserId(), postId));
  }

  @DeleteMapping("/posts/{postId}/like")
  public ApiResponse<ForumDtos.InteractionStateView> unlikePost(@PathVariable("postId") String postId) {
    return ApiResponse.ok(forumService.unlikePost(AuthContext.getUserId(), postId));
  }

  @PostMapping("/posts/{postId}/favorite")
  public ApiResponse<ForumDtos.InteractionStateView> favoritePost(@PathVariable("postId") String postId) {
    return ApiResponse.ok(forumService.favoritePost(AuthContext.getUserId(), postId));
  }

  @DeleteMapping("/posts/{postId}/favorite")
  public ApiResponse<ForumDtos.InteractionStateView> unfavoritePost(@PathVariable("postId") String postId) {
    return ApiResponse.ok(forumService.unfavoritePost(AuthContext.getUserId(), postId));
  }

  @PostMapping("/posts/{postId}/comments")
  public ApiResponse<ForumDtos.CommentItem> addComment(@PathVariable("postId") String postId,
                                                       @Valid @RequestBody ForumDtos.CreateCommentRequest request) {
    return ApiResponse.ok(forumService.addComment(AuthContext.getUserId(), postId, request));
  }

  @GetMapping("/posts/{postId}/comments")
  public ApiResponse<ForumDtos.CommentListView> listComments(@PathVariable("postId") String postId) {
    return ApiResponse.ok(forumService.listComments(AuthContext.getUserId(), postId));
  }

  @PostMapping("/posts/{postId}/share")
  public ApiResponse<ForumDtos.ShareView> sharePost(@PathVariable("postId") String postId, HttpServletRequest request) {
    return ApiResponse.ok(forumService.sharePost(AuthContext.getUserId(), postId, request.getHeader("Origin")));
  }

  @GetMapping("/notifications")
  public ApiResponse<ForumDtos.NotificationListView> listNotifications(
    @RequestParam(value = "page", required = false) Integer page,
    @RequestParam(value = "pageSize", required = false) Integer pageSize
  ) {
    return ApiResponse.ok(forumService.listNotifications(AuthContext.getUserId(), page, pageSize));
  }

  @PostMapping("/notifications/read")
  public ApiResponse<ForumDtos.NotificationReadResult> markNotificationsRead(
    @RequestBody(required = false) ForumDtos.MarkNotificationsReadRequest request
  ) {
    return ApiResponse.ok(forumService.markNotificationsRead(AuthContext.getUserId(), request));
  }
}
