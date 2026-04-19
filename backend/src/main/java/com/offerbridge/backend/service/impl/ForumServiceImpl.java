package com.offerbridge.backend.service.impl;

import com.offerbridge.backend.dto.ForumDtos;
import com.offerbridge.backend.entity.UserAccount;
import com.offerbridge.backend.entity.forum.ForumCommentDoc;
import com.offerbridge.backend.entity.forum.ForumNotificationDoc;
import com.offerbridge.backend.entity.forum.ForumPostDoc;
import com.offerbridge.backend.entity.forum.ForumReactionDoc;
import com.offerbridge.backend.exception.BizException;
import com.offerbridge.backend.mapper.UserAccountMapper;
import com.offerbridge.backend.repository.forum.ForumCommentRepository;
import com.offerbridge.backend.repository.forum.ForumNotificationRepository;
import com.offerbridge.backend.repository.forum.ForumPostRepository;
import com.offerbridge.backend.repository.forum.ForumReactionRepository;
import com.offerbridge.backend.service.ForumService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ForumServiceImpl implements ForumService {
  private static final String ROLE_STUDENT = "STUDENT";
  private static final String STATUS_PUBLISHED = "PUBLISHED";
  private static final String CHANNEL_EXPERIENCE = "EXPERIENCE";
  private static final String CHANNEL_OFFER_WALL = "OFFER_WALL";
  private static final String ACTION_LIKE = "LIKE";
  private static final String ACTION_FAVORITE = "FAVORITE";

  private final ForumPostRepository forumPostRepository;
  private final ForumCommentRepository forumCommentRepository;
  private final ForumReactionRepository forumReactionRepository;
  private final ForumNotificationRepository forumNotificationRepository;
  private final UserAccountMapper userAccountMapper;
  private final MongoTemplate mongoTemplate;

  public ForumServiceImpl(ForumPostRepository forumPostRepository,
                          ForumCommentRepository forumCommentRepository,
                          ForumReactionRepository forumReactionRepository,
                          ForumNotificationRepository forumNotificationRepository,
                          UserAccountMapper userAccountMapper,
                          MongoTemplate mongoTemplate) {
    this.forumPostRepository = forumPostRepository;
    this.forumCommentRepository = forumCommentRepository;
    this.forumReactionRepository = forumReactionRepository;
    this.forumNotificationRepository = forumNotificationRepository;
    this.userAccountMapper = userAccountMapper;
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public ForumDtos.PostItem createPost(Long userId, ForumDtos.CreatePostRequest request) {
    requireStudentRole(userId);
    String channel = normalizeChannel(request.getChannel());
    String title = normalizeRequiredText(request.getTitle(), 120, "标题不能为空");
    String contentHtml = normalizeRequiredText(request.getContentHtml(), 20000, "内容不能为空");
    String contentText = stripHtml(contentHtml);
    if (!StringUtils.hasText(contentText)) {
      throw new BizException("BIZ_BAD_REQUEST", "内容不能为空");
    }

    ForumPostDoc post = new ForumPostDoc();
    post.setAuthorUserId(userId);
    post.setChannel(channel);
    post.setTitle(title);
    post.setContentHtml(contentHtml);
    post.setContentText(contentText);
    post.setTags(normalizeTags(request.getTags(), channel));
    post.setStatus(STATUS_PUBLISHED);
    post.setLikeCount(0);
    post.setCommentCount(0);
    post.setFavoriteCount(0);
    post.setShareCount(0);
    Instant now = Instant.now();
    post.setCreatedAt(now);
    post.setUpdatedAt(now);

    ForumPostDoc saved = forumPostRepository.save(post);
    return toPostItem(saved, false, false, true);
  }

  @Override
  public ForumDtos.PostListView listPosts(Long userId, String channel, String keyword, Integer page, Integer pageSize) {
    int safePage = normalizePage(page);
    int safePageSize = normalizePageSize(pageSize);

    Query query = buildPostListQuery(channel, keyword);
    long total = mongoTemplate.count(query, ForumPostDoc.class);

    query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
    query.skip((long) (safePage - 1) * safePageSize).limit(safePageSize);
    List<ForumPostDoc> docs = mongoTemplate.find(query, ForumPostDoc.class);

    Set<String> likedPostIds = Collections.emptySet();
    Set<String> favoritedPostIds = Collections.emptySet();
    if (docs.size() > 0 && isStudent(userId)) {
      List<String> postIds = docs.stream().map(ForumPostDoc::getId).filter(Objects::nonNull).toList();
      List<ForumReactionDoc> reactions = forumReactionRepository.findByUserIdAndPostIdIn(userId, postIds);
      likedPostIds = reactions.stream()
        .filter(item -> ACTION_LIKE.equals(item.getActionType()))
        .map(ForumReactionDoc::getPostId)
        .collect(Collectors.toSet());
      favoritedPostIds = reactions.stream()
        .filter(item -> ACTION_FAVORITE.equals(item.getActionType()))
        .map(ForumReactionDoc::getPostId)
        .collect(Collectors.toSet());
    }

    List<ForumDtos.PostItem> items = new ArrayList<>();
    for (ForumPostDoc doc : docs) {
      items.add(toPostItem(doc, likedPostIds.contains(doc.getId()), favoritedPostIds.contains(doc.getId()), false));
    }

    ForumDtos.PostListView view = new ForumDtos.PostListView();
    view.setTotal(total);
    view.setPage(safePage);
    view.setPageSize(safePageSize);
    view.setItems(items);
    return view;
  }

  @Override
  public ForumDtos.PostItem getPostDetail(Long userId, String postId) {
    ForumPostDoc post = requirePublishedPost(postId);
    boolean liked = false;
    boolean favorited = false;
    if (isStudent(userId)) {
      liked = forumReactionRepository.findByUserIdAndPostIdAndActionType(userId, postId, ACTION_LIKE).isPresent();
      favorited = forumReactionRepository.findByUserIdAndPostIdAndActionType(userId, postId, ACTION_FAVORITE).isPresent();
    }
    return toPostItem(post, liked, favorited, true);
  }

  @Override
  public ForumDtos.InteractionStateView likePost(Long userId, String postId) {
    requireStudentRole(userId);
    ForumPostDoc post = requirePublishedPost(postId);

    boolean created = forumReactionRepository.findByUserIdAndPostIdAndActionType(userId, postId, ACTION_LIKE).isEmpty();
    if (created) {
      ForumReactionDoc reaction = new ForumReactionDoc();
      reaction.setUserId(userId);
      reaction.setPostId(postId);
      reaction.setActionType(ACTION_LIKE);
      reaction.setCreatedAt(Instant.now());
      forumReactionRepository.save(reaction);
      increasePostCounter(postId, "likeCount", 1);
      createNotificationIfNeeded(post.getAuthorUserId(), userId, postId, ACTION_LIKE);
    }

    return buildInteractionState(userId, postId);
  }

  @Override
  public ForumDtos.InteractionStateView unlikePost(Long userId, String postId) {
    requireStudentRole(userId);
    requirePublishedPost(postId);

    forumReactionRepository.findByUserIdAndPostIdAndActionType(userId, postId, ACTION_LIKE)
      .ifPresent(reaction -> {
        forumReactionRepository.deleteById(reaction.getId());
        increasePostCounter(postId, "likeCount", -1);
      });

    clampCounterNotNegative(postId, "likeCount");
    return buildInteractionState(userId, postId);
  }

  @Override
  public ForumDtos.InteractionStateView favoritePost(Long userId, String postId) {
    requireStudentRole(userId);
    ForumPostDoc post = requirePublishedPost(postId);

    boolean created = forumReactionRepository.findByUserIdAndPostIdAndActionType(userId, postId, ACTION_FAVORITE).isEmpty();
    if (created) {
      ForumReactionDoc reaction = new ForumReactionDoc();
      reaction.setUserId(userId);
      reaction.setPostId(postId);
      reaction.setActionType(ACTION_FAVORITE);
      reaction.setCreatedAt(Instant.now());
      forumReactionRepository.save(reaction);
      increasePostCounter(postId, "favoriteCount", 1);
      createNotificationIfNeeded(post.getAuthorUserId(), userId, postId, ACTION_FAVORITE);
    }

    return buildInteractionState(userId, postId);
  }

  @Override
  public ForumDtos.InteractionStateView unfavoritePost(Long userId, String postId) {
    requireStudentRole(userId);
    requirePublishedPost(postId);

    forumReactionRepository.findByUserIdAndPostIdAndActionType(userId, postId, ACTION_FAVORITE)
      .ifPresent(reaction -> {
        forumReactionRepository.deleteById(reaction.getId());
        increasePostCounter(postId, "favoriteCount", -1);
      });

    clampCounterNotNegative(postId, "favoriteCount");
    return buildInteractionState(userId, postId);
  }

  @Override
  public ForumDtos.CommentItem addComment(Long userId, String postId, ForumDtos.CreateCommentRequest request) {
    requireStudentRole(userId);
    ForumPostDoc post = requirePublishedPost(postId);
    String content = normalizeRequiredText(request.getContent(), 5000, "评论内容不能为空");

    ForumCommentDoc doc = new ForumCommentDoc();
    doc.setPostId(postId);
    doc.setAuthorUserId(userId);
    doc.setContent(content);
    doc.setStatus(STATUS_PUBLISHED);
    doc.setCreatedAt(Instant.now());
    ForumCommentDoc saved = forumCommentRepository.save(doc);

    increasePostCounter(postId, "commentCount", 1);
    createNotificationIfNeeded(post.getAuthorUserId(), userId, postId, "COMMENT");
    return toCommentItem(saved);
  }

  @Override
  public ForumDtos.CommentListView listComments(Long userId, String postId) {
    requirePublishedPost(postId);
    List<ForumCommentDoc> docs = forumCommentRepository.findByPostIdAndStatus(postId, STATUS_PUBLISHED, Sort.by(Sort.Direction.ASC, "createdAt"));

    ForumDtos.CommentListView view = new ForumDtos.CommentListView();
    view.setItems(docs.stream().map(this::toCommentItem).toList());
    return view;
  }

  @Override
  public ForumDtos.ShareView sharePost(Long userId, String postId, String origin) {
    requirePublishedPost(postId);
    increasePostCounter(postId, "shareCount", 1);
    ForumPostDoc post = requirePublishedPost(postId);

    String base = StringUtils.hasText(origin) ? origin : "";
    ForumDtos.ShareView view = new ForumDtos.ShareView();
    view.setShareUrl(base + "/forum?postId=" + postId);
    view.setShareCount(post.getShareCount());
    return view;
  }

  @Override
  public ForumDtos.NotificationListView listNotifications(Long userId, Integer page, Integer pageSize) {
    int safePage = normalizePage(page);
    int safePageSize = normalizePageSize(pageSize);

    Pageable pageable = PageRequest.of(safePage - 1, safePageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    List<ForumNotificationDoc> docs = forumNotificationRepository.findByReceiverUserId(userId, pageable);

    ForumDtos.NotificationListView view = new ForumDtos.NotificationListView();
    view.setUnreadCount(forumNotificationRepository.countByReceiverUserIdAndRead(userId, false));
    view.setPage(safePage);
    view.setPageSize(safePageSize);
    view.setItems(docs.stream().map(this::toNotificationItem).toList());
    return view;
  }

  @Override
  public ForumDtos.NotificationReadResult markNotificationsRead(Long userId, ForumDtos.MarkNotificationsReadRequest request) {
    boolean markAll = request != null && Boolean.TRUE.equals(request.getMarkAll());
    List<String> ids = request == null || request.getNotificationIds() == null ? List.of() : request.getNotificationIds();

    Query query = new Query();
    query.addCriteria(Criteria.where("receiverUserId").is(userId).and("read").is(false));
    if (!markAll && !ids.isEmpty()) {
      query.addCriteria(Criteria.where("_id").in(ids));
    } else if (!markAll) {
      ForumDtos.NotificationReadResult result = new ForumDtos.NotificationReadResult();
      result.setUpdatedCount(0);
      return result;
    }

    Update update = new Update();
    update.set("read", true);
    var updateResult = mongoTemplate.updateMulti(query, update, ForumNotificationDoc.class);

    ForumDtos.NotificationReadResult result = new ForumDtos.NotificationReadResult();
    result.setUpdatedCount(updateResult.getModifiedCount());
    return result;
  }

  private Query buildPostListQuery(String channel, String keyword) {
    Query query = new Query();
    query.addCriteria(Criteria.where("status").is(STATUS_PUBLISHED));

    if (StringUtils.hasText(channel)) {
      query.addCriteria(Criteria.where("channel").is(normalizeChannel(channel)));
    }

    if (StringUtils.hasText(keyword)) {
      query.addCriteria(TextCriteria.forDefaultLanguage().matching(keyword.trim()));
    }

    return query;
  }

  private ForumPostDoc requirePublishedPost(String postId) {
    ForumPostDoc post = forumPostRepository.findById(postId).orElse(null);
    if (post == null || !STATUS_PUBLISHED.equals(post.getStatus())) {
      throw new BizException("BIZ_NOT_FOUND", "帖子不存在或已删除");
    }
    return post;
  }

  private void increasePostCounter(String postId, String field, int delta) {
    Query query = new Query(Criteria.where("_id").is(postId));
    Update update = new Update();
    update.inc(field, delta);
    update.set("updatedAt", Instant.now());
    mongoTemplate.updateFirst(query, update, ForumPostDoc.class);
  }

  private void clampCounterNotNegative(String postId, String field) {
    Query query = new Query(Criteria.where("_id").is(postId).and(field).lt(0));
    Update update = new Update().set(field, 0L);
    mongoTemplate.updateFirst(query, update, ForumPostDoc.class);
  }

  private void createNotificationIfNeeded(Long receiverUserId, Long actorUserId, String postId, String type) {
    if (receiverUserId == null || actorUserId == null || receiverUserId.equals(actorUserId)) return;

    ForumNotificationDoc doc = new ForumNotificationDoc();
    doc.setReceiverUserId(receiverUserId);
    doc.setActorUserId(actorUserId);
    doc.setPostId(postId);
    doc.setType(type);
    doc.setRead(false);
    doc.setCreatedAt(Instant.now());
    forumNotificationRepository.save(doc);
  }

  private ForumDtos.InteractionStateView buildInteractionState(Long userId, String postId) {
    ForumPostDoc post = requirePublishedPost(postId);
    boolean liked = forumReactionRepository.findByUserIdAndPostIdAndActionType(userId, postId, ACTION_LIKE).isPresent();
    boolean favorited = forumReactionRepository.findByUserIdAndPostIdAndActionType(userId, postId, ACTION_FAVORITE).isPresent();

    ForumDtos.InteractionStateView view = new ForumDtos.InteractionStateView();
    view.setLiked(liked);
    view.setFavorited(favorited);
    view.setLikeCount(post.getLikeCount());
    view.setFavoriteCount(post.getFavoriteCount());
    return view;
  }

  private ForumDtos.PostItem toPostItem(ForumPostDoc doc, boolean liked, boolean favorited, boolean includeHtml) {
    ForumDtos.PostItem item = new ForumDtos.PostItem();
    item.setPostId(doc.getId());
    item.setAuthorUserId(doc.getAuthorUserId());
    item.setChannel(doc.getChannel());
    item.setTitle(doc.getTitle());
    item.setContentHtml(includeHtml ? doc.getContentHtml() : null);
    item.setSummary(buildSummary(doc.getContentText()));
    item.setTags(doc.getTags() == null ? List.of() : doc.getTags());
    item.setStatus(doc.getStatus());
    item.setLikeCount(doc.getLikeCount());
    item.setCommentCount(doc.getCommentCount());
    item.setFavoriteCount(doc.getFavoriteCount());
    item.setShareCount(doc.getShareCount());
    item.setViewerLiked(liked);
    item.setViewerFavorited(favorited);
    item.setCreatedAt(doc.getCreatedAt());
    item.setUpdatedAt(doc.getUpdatedAt());
    return item;
  }

  private ForumDtos.CommentItem toCommentItem(ForumCommentDoc doc) {
    ForumDtos.CommentItem item = new ForumDtos.CommentItem();
    item.setCommentId(doc.getId());
    item.setPostId(doc.getPostId());
    item.setAuthorUserId(doc.getAuthorUserId());
    item.setContent(doc.getContent());
    item.setStatus(doc.getStatus());
    item.setCreatedAt(doc.getCreatedAt());
    return item;
  }

  private ForumDtos.NotificationItem toNotificationItem(ForumNotificationDoc doc) {
    ForumDtos.NotificationItem item = new ForumDtos.NotificationItem();
    item.setNotificationId(doc.getId());
    item.setReceiverUserId(doc.getReceiverUserId());
    item.setActorUserId(doc.getActorUserId());
    item.setPostId(doc.getPostId());
    item.setType(doc.getType());
    item.setRead(doc.isRead());
    item.setCreatedAt(doc.getCreatedAt());
    return item;
  }

  private void requireStudentRole(Long userId) {
    UserAccount user = requireUser(userId);
    if (!ROLE_STUDENT.equals(user.getRole())) {
      throw new BizException("BIZ_FORBIDDEN", "仅学生用户可执行此操作");
    }
  }

  private boolean isStudent(Long userId) {
    UserAccount user = userAccountMapper.findById(userId);
    return user != null && ROLE_STUDENT.equals(user.getRole());
  }

  private UserAccount requireUser(Long userId) {
    UserAccount user = userAccountMapper.findById(userId);
    if (user == null) {
      throw new BizException("BIZ_UNAUTHORIZED", "用户不存在");
    }
    if (!"ACTIVE".equals(user.getStatus())) {
      throw new BizException("BIZ_ACCOUNT_DISABLED", "账号不可用");
    }
    return user;
  }

  private String normalizeChannel(String channel) {
    String value = channel == null ? "" : channel.trim().toUpperCase(Locale.ROOT);
    if (CHANNEL_EXPERIENCE.equals(value) || CHANNEL_OFFER_WALL.equals(value)) {
      return value;
    }
    throw new BizException("BIZ_BAD_REQUEST", "channel 参数不合法");
  }

  private List<String> normalizeTags(List<String> inputTags, String channel) {
    List<String> source = inputTags == null ? List.of() : inputTags;
    Set<String> dedup = new HashSet<>();
    List<String> result = new ArrayList<>();

    for (String tag : source) {
      String clean = normalizeOptionalText(tag, 24);
      if (!StringUtils.hasText(clean)) continue;
      String lower = clean.toLowerCase(Locale.ROOT);
      if (dedup.add(lower)) {
        result.add(clean);
      }
      if (result.size() >= 8) break;
    }

    String channelTag = CHANNEL_EXPERIENCE.equals(channel) ? "留学经验贴" : "offer墙";
    if (result.stream().noneMatch(item -> item.equalsIgnoreCase(channelTag))) {
      result.add(0, channelTag);
    }
    return result;
  }

  private int normalizePage(Integer page) {
    if (page == null || page < 1) return 1;
    return page;
  }

  private int normalizePageSize(Integer pageSize) {
    if (pageSize == null) return 10;
    if (pageSize < 1) return 10;
    return Math.min(pageSize, 30);
  }

  private String normalizeRequiredText(String input, int maxLen, String errMsg) {
    String value = normalizeOptionalText(input, maxLen);
    if (!StringUtils.hasText(value)) {
      throw new BizException("BIZ_BAD_REQUEST", errMsg);
    }
    return value;
  }

  private String normalizeOptionalText(String input, int maxLen) {
    if (input == null) return "";
    String value = input.trim();
    if (value.length() > maxLen) {
      value = value.substring(0, maxLen);
    }
    return value;
  }

  private String stripHtml(String html) {
    if (html == null) return "";
    return html
      .replaceAll("<script[^>]*>([\\S\\s]*?)</script>", "")
      .replaceAll("<style[^>]*>([\\S\\s]*?)</style>", "")
      .replaceAll("<[^>]+>", " ")
      .replaceAll("\\s+", " ")
      .trim();
  }

  private String buildSummary(String text) {
    if (!StringUtils.hasText(text)) return "";
    String trimmed = text.trim();
    if (trimmed.length() <= 120) return trimmed;
    return trimmed.substring(0, 120) + "...";
  }
}
