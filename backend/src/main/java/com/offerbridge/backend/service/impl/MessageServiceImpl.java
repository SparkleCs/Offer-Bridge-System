package com.offerbridge.backend.service.impl;

import com.offerbridge.backend.dto.MessageDtos;
import com.offerbridge.backend.entity.AgencyMemberProfile;
import com.offerbridge.backend.entity.AgencyOrg;
import com.offerbridge.backend.entity.AgencyTeam;
import com.offerbridge.backend.entity.ServiceOrder;
import com.offerbridge.backend.entity.StudentProfile;
import com.offerbridge.backend.entity.UserAccount;
import com.offerbridge.backend.entity.VerificationRecord;
import com.offerbridge.backend.entity.chat.ChatConversationDoc;
import com.offerbridge.backend.entity.chat.ChatMessageDoc;
import com.offerbridge.backend.exception.BizException;
import com.offerbridge.backend.mapper.AgencyMemberProfileMapper;
import com.offerbridge.backend.mapper.AgencyMemberPermissionRelMapper;
import com.offerbridge.backend.mapper.AgencyOrgMapper;
import com.offerbridge.backend.mapper.AgencyTeamMapper;
import com.offerbridge.backend.mapper.ServiceOrderMapper;
import com.offerbridge.backend.mapper.StudentProfileMapper;
import com.offerbridge.backend.mapper.SystemNotificationMapper;
import com.offerbridge.backend.mapper.UserAccountMapper;
import com.offerbridge.backend.mapper.VerificationRecordMapper;
import com.offerbridge.backend.repository.chat.ChatConversationRepository;
import com.offerbridge.backend.repository.chat.ChatMessageRepository;
import com.offerbridge.backend.service.MessageService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MessageServiceImpl implements MessageService {
  private static final String ROLE_STUDENT = "STUDENT";
  private static final String ROLE_AGENT_MEMBER = "AGENT_MEMBER";
  private static final String MESSAGE_TEXT = "TEXT";
  private static final String MESSAGE_IMAGE = "IMAGE";
  private static final String MESSAGE_FILE = "FILE";
  private static final String STATUS_UNREAD = "UNREAD";
  private static final String STATUS_READ = "READ";
  private static final String DEFAULT_GREETING = "您好，想咨询一下留学申请相关问题。";
  private static final String DEFAULT_AGENT_GREETING = "你好，我是留学顾问，看到你的申请方向比较匹配，想进一步了解你的需求。";
  private static final String FILTER_ALL = "all";
  private static final String FILTER_NEW = "new";
  private static final String FILTER_UNREAD = "unread";
  private static final String FILTER_ACTIVE = "active";
  private static final String FILTER_SERVICING = "servicing";
  private static final String FILTER_ENDED = "ended";
  private static final String FILTER_STARRED = "starred";

  private final SystemNotificationMapper systemNotificationMapper;
  private final ChatConversationRepository chatConversationRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final MongoTemplate mongoTemplate;
  private final StringRedisTemplate redisTemplate;
  private final SimpMessagingTemplate messagingTemplate;
  private final UserAccountMapper userAccountMapper;
  private final StudentProfileMapper studentProfileMapper;
  private final VerificationRecordMapper verificationRecordMapper;
  private final AgencyTeamMapper agencyTeamMapper;
  private final AgencyOrgMapper agencyOrgMapper;
  private final AgencyMemberProfileMapper agencyMemberProfileMapper;
  private final AgencyMemberPermissionRelMapper agencyMemberPermissionRelMapper;
  private final ServiceOrderMapper serviceOrderMapper;

  public MessageServiceImpl(SystemNotificationMapper systemNotificationMapper,
                            ChatConversationRepository chatConversationRepository,
                            ChatMessageRepository chatMessageRepository,
                            MongoTemplate mongoTemplate,
                            StringRedisTemplate redisTemplate,
                            SimpMessagingTemplate messagingTemplate,
                            UserAccountMapper userAccountMapper,
                            StudentProfileMapper studentProfileMapper,
                            VerificationRecordMapper verificationRecordMapper,
                            AgencyTeamMapper agencyTeamMapper,
                            AgencyOrgMapper agencyOrgMapper,
                            AgencyMemberProfileMapper agencyMemberProfileMapper,
                            AgencyMemberPermissionRelMapper agencyMemberPermissionRelMapper,
                            ServiceOrderMapper serviceOrderMapper) {
    this.systemNotificationMapper = systemNotificationMapper;
    this.chatConversationRepository = chatConversationRepository;
    this.chatMessageRepository = chatMessageRepository;
    this.mongoTemplate = mongoTemplate;
    this.redisTemplate = redisTemplate;
    this.messagingTemplate = messagingTemplate;
    this.userAccountMapper = userAccountMapper;
    this.studentProfileMapper = studentProfileMapper;
    this.verificationRecordMapper = verificationRecordMapper;
    this.agencyTeamMapper = agencyTeamMapper;
    this.agencyOrgMapper = agencyOrgMapper;
    this.agencyMemberProfileMapper = agencyMemberProfileMapper;
    this.agencyMemberPermissionRelMapper = agencyMemberPermissionRelMapper;
    this.serviceOrderMapper = serviceOrderMapper;
  }

  @Override
  public MessageDtos.PagedResult<MessageDtos.SystemNotificationItem> listSystemNotifications(Long userId, int page, int pageSize) {
    List<MessageDtos.SystemNotificationItem> all = systemNotificationMapper.listByUserId(userId);
    return toPaged(all, page, pageSize, systemNotificationMapper.countUnreadByUserId(userId));
  }

  @Override
  public MessageDtos.MarkReadResult markSystemNotificationsRead(Long userId, MessageDtos.MarkReadRequest request) {
    int updated;
    if (Boolean.TRUE.equals(request.getMarkAll())) {
      updated = systemNotificationMapper.markAllRead(userId);
    } else {
      List<Long> ids = request.getIds() == null ? List.of() : request.getIds().stream().distinct().toList();
      if (ids.isEmpty()) {
        updated = 0;
      } else {
        updated = systemNotificationMapper.markReadByIds(userId, ids);
      }
    }
    MessageDtos.MarkReadResult result = new MessageDtos.MarkReadResult();
    result.setUpdatedCount(updated);
    return result;
  }

  @Override
  public MessageDtos.ChatStartResult startChat(Long userId, MessageDtos.StartChatRequest request) {
    requireStudentRole(userId);
    requireStudentVerified(userId);
    if (request.getTeamId() == null) {
      throw new BizException("BIZ_BAD_REQUEST", "请选择要沟通的套餐");
    }

    AgencyTeam team = agencyTeamMapper.findById(request.getTeamId());
    if (team == null) {
      throw new BizException("BIZ_NOT_FOUND", "套餐不存在或不可沟通");
    }
    if (!"PUBLISHED".equals(team.getPublishStatus())) {
      throw new BizException("BIZ_BAD_REQUEST", "套餐尚未发布，暂不可沟通");
    }
    if (team.getPublishedBy() == null) {
      throw new BizException("BIZ_BAD_REQUEST", "该套餐暂无可沟通负责人，请稍后再试");
    }

    AgencyMemberProfile member = agencyMemberProfileMapper.findById(team.getPublishedBy());
    if (member == null || !"ACTIVE".equals(member.getStatus()) || member.getUserId() == null) {
      throw new BizException("BIZ_BAD_REQUEST", "该套餐暂无可沟通负责人，请稍后再试");
    }
    UserAccount agentUser = userAccountMapper.findById(member.getUserId());
    if (agentUser == null || !ROLE_AGENT_MEMBER.equals(agentUser.getRole()) || !"ACTIVE".equals(agentUser.getStatus())) {
      throw new BizException("BIZ_BAD_REQUEST", "该套餐暂无可沟通负责人，请稍后再试");
    }
    AgencyOrg org = agencyOrgMapper.findById(team.getOrgId());

    ChatConversationDoc conversation = chatConversationRepository
      .findByStudentUserIdAndTeamIdAndAgentMemberId(userId, team.getId(), member.getId())
      .orElseGet(() -> createConversation(userId, team, org, member));

    String content = normalizeMessage(request.getGreeting(), DEFAULT_GREETING);
    ChatMessageDoc message = appendMessage(conversation, userId, MESSAGE_TEXT, content);

    MessageDtos.ChatStartResult result = new MessageDtos.ChatStartResult();
    result.setConversation(toConversationItem(conversation, userId));
    result.setFirstMessage(toMessageItem(message, userId));
    return result;
  }

  @Override
  public MessageDtos.ChatStartResult agentStartChat(Long userId, MessageDtos.AgentStartChatRequest request) {
    AgencyMemberProfile member = requireAgentChatAccess(userId);
    if (request.getStudentUserId() == null) {
      throw new BizException("BIZ_BAD_REQUEST", "请选择学生");
    }
    if (request.getTeamId() == null) {
      throw new BizException("BIZ_BAD_REQUEST", "请选择团队产品");
    }
    requireStudentVerified(request.getStudentUserId());
    AgencyTeam team = agencyTeamMapper.findById(request.getTeamId());
    if (team == null || !member.getOrgId().equals(team.getOrgId()) || !"PUBLISHED".equals(team.getPublishStatus())) {
      throw new BizException("BIZ_BAD_REQUEST", "团队产品不存在或尚未发布");
    }
    AgencyOrg org = agencyOrgMapper.findById(team.getOrgId());
    ChatConversationDoc conversation = chatConversationRepository
      .findByStudentUserIdAndTeamIdAndAgentMemberId(request.getStudentUserId(), team.getId(), member.getId())
      .orElseGet(() -> createConversation(request.getStudentUserId(), team, org, member));
    String content = normalizeMessage(request.getGreeting(), DEFAULT_AGENT_GREETING);
    ChatMessageDoc message = appendMessage(conversation, userId, MESSAGE_TEXT, content);

    MessageDtos.ChatStartResult result = new MessageDtos.ChatStartResult();
    result.setConversation(toConversationItem(conversation, userId));
    result.setFirstMessage(toMessageItem(message, userId));
    return result;
  }

  @Override
  public MessageDtos.PagedResult<MessageDtos.ChatConversationItem> listChatConversations(Long userId, int page, int pageSize, String filter) {
    UserAccount user = requireUser(userId);
    int safePage = Math.max(1, page);
    int safePageSize = Math.max(1, Math.min(pageSize, 100));
    String safeFilter = normalizeConversationFilter(filter);
    Criteria criteria;
    if (ROLE_STUDENT.equals(user.getRole())) {
      criteria = Criteria.where("studentUserId").is(userId);
    } else if (ROLE_AGENT_MEMBER.equals(user.getRole())) {
      criteria = Criteria.where("agentUserId").is(userId);
    } else {
      throw new BizException("BIZ_FORBIDDEN", "当前账号不可查看私聊");
    }
    Query query = Query.query(criteria)
      .with(Sort.by(Sort.Direction.DESC, "updatedAt"));
    List<MessageDtos.ChatConversationItem> filtered = mongoTemplate.find(query, ChatConversationDoc.class)
      .stream()
      .map(item -> toConversationItem(item, userId))
      .filter(item -> matchesConversationFilter(item, safeFilter, user.getRole()))
      .toList();
    int from = (safePage - 1) * safePageSize;
    int to = Math.min(from + safePageSize, filtered.size());
    List<MessageDtos.ChatConversationItem> records = from >= filtered.size() ? List.of() : filtered.subList(from, to);

    MessageDtos.PagedResult<MessageDtos.ChatConversationItem> result = new MessageDtos.PagedResult<>();
    result.setRecords(records);
    result.setTotal(filtered.size());
    result.setPage(safePage);
    result.setPageSize(safePageSize);
    result.setUnreadCount(getChatUnreadSummary(userId).getUnreadCount());
    return result;
  }

  @Override
  public MessageDtos.PagedResult<MessageDtos.ChatMessageItem> listChatMessages(Long userId, String conversationId, int page, int pageSize) {
    requireParticipant(userId, conversationId);
    int safePage = Math.max(1, page);
    int safePageSize = Math.max(1, Math.min(pageSize, 100));
    Query countQuery = Query.query(Criteria.where("conversationId").is(conversationId));
    long total = mongoTemplate.count(countQuery, ChatMessageDoc.class);
    Query query = Query.query(Criteria.where("conversationId").is(conversationId))
      .with(Sort.by(Sort.Direction.ASC, "createdAt"))
      .skip((long) (safePage - 1) * safePageSize)
      .limit(safePageSize);
    List<MessageDtos.ChatMessageItem> records = mongoTemplate.find(query, ChatMessageDoc.class)
      .stream()
      .map(item -> toMessageItem(item, userId))
      .toList();
    setActiveConversation(userId, conversationId);

    MessageDtos.PagedResult<MessageDtos.ChatMessageItem> result = new MessageDtos.PagedResult<>();
    result.setRecords(records);
    result.setTotal(total);
    result.setPage(safePage);
    result.setPageSize(safePageSize);
    result.setUnreadCount(getConversationUnread(requireParticipant(userId, conversationId), userId));
    return result;
  }

  @Override
  public MessageDtos.ChatMessageItem sendChatMessage(Long userId, String conversationId, MessageDtos.SendChatMessageRequest request) {
    ChatConversationDoc conversation = requireParticipant(userId, conversationId);
    String contentType = normalizeContentType(request.getContentType());
    ChatMessageDoc message = appendMessage(conversation, userId, contentType, normalizeMessage(request.getContent(), null, contentType));
    return toMessageItem(message, userId);
  }

  @Override
  public MessageDtos.MarkReadResult markChatRead(Long userId, String conversationId) {
    ChatConversationDoc conversation = requireParticipant(userId, conversationId);
    Instant now = Instant.now();
    Query msgQuery = Query.query(Criteria.where("conversationId").is(conversationId)
      .and("receiverUserId").is(userId)
      .and("status").is(STATUS_UNREAD));
    var updated = mongoTemplate.updateMulti(msgQuery, new Update().set("status", STATUS_READ).set("readAt", now), ChatMessageDoc.class);
    Query convQuery = Query.query(Criteria.where("_id").is(conversationId));
    Update convUpdate = new Update().set(isStudentParticipant(conversation, userId) ? "unreadByStudent" : "unreadByAgent", 0);
    mongoTemplate.updateFirst(convQuery, convUpdate, ChatConversationDoc.class);
    setActiveConversation(userId, conversationId);

    MessageDtos.MarkReadResult result = new MessageDtos.MarkReadResult();
    result.setUpdatedCount(updated.getModifiedCount());
    return result;
  }

  @Override
  public MessageDtos.ChatConversationItem starChatConversation(Long userId, String conversationId, boolean starred) {
    ChatConversationDoc conversation = requireParticipant(userId, conversationId);
    boolean studentViewer = isStudentParticipant(conversation, userId);
    Query query = Query.query(Criteria.where("_id").is(conversationId));
    mongoTemplate.updateFirst(query, new Update().set(studentViewer ? "starredByStudent" : "starredByAgent", starred), ChatConversationDoc.class);
    if (studentViewer) {
      conversation.setStarredByStudent(starred);
    } else {
      conversation.setStarredByAgent(starred);
    }
    return toConversationItem(conversation, userId);
  }

  @Override
  public MessageDtos.ChatUnreadSummary getChatUnreadSummary(Long userId) {
    UserAccount user = requireUser(userId);
    Criteria criteria;
    String unreadField;
    if (ROLE_STUDENT.equals(user.getRole())) {
      criteria = Criteria.where("studentUserId").is(userId);
      unreadField = "unreadByStudent";
    } else if (ROLE_AGENT_MEMBER.equals(user.getRole())) {
      criteria = Criteria.where("agentUserId").is(userId);
      unreadField = "unreadByAgent";
    } else {
      MessageDtos.ChatUnreadSummary empty = new MessageDtos.ChatUnreadSummary();
      empty.setUnreadCount(0);
      return empty;
    }
    Query query = Query.query(criteria.and(unreadField).gt(0));
    List<ChatConversationDoc> conversations = mongoTemplate.find(query, ChatConversationDoc.class);
    long unread = conversations.stream()
      .mapToLong(item -> ROLE_STUDENT.equals(user.getRole()) ? safeInt(item.getUnreadByStudent()) : safeInt(item.getUnreadByAgent()))
      .sum();
    MessageDtos.ChatUnreadSummary result = new MessageDtos.ChatUnreadSummary();
    result.setUnreadCount(unread);
    return result;
  }

  private MessageDtos.PagedResult<MessageDtos.SystemNotificationItem> toPaged(List<MessageDtos.SystemNotificationItem> all,
                                                                               int page,
                                                                               int pageSize,
                                                                               long unreadCount) {
    int safePage = Math.max(1, page);
    int safePageSize = Math.max(1, Math.min(pageSize, 100));
    int from = (safePage - 1) * safePageSize;
    int to = Math.min(from + safePageSize, all.size());
    List<MessageDtos.SystemNotificationItem> records = from >= all.size() ? new ArrayList<>() : all.subList(from, to);
    MessageDtos.PagedResult<MessageDtos.SystemNotificationItem> result = new MessageDtos.PagedResult<>();
    result.setRecords(records);
    result.setTotal(all.size());
    result.setPage(safePage);
    result.setPageSize(safePageSize);
    result.setUnreadCount(unreadCount);
    return result;
  }

  private ChatConversationDoc createConversation(Long studentUserId, AgencyTeam team, AgencyOrg org, AgencyMemberProfile member) {
    Instant now = Instant.now();
    ChatConversationDoc doc = new ChatConversationDoc();
    doc.setStudentUserId(studentUserId);
    doc.setAgentUserId(member.getUserId());
    doc.setAgentMemberId(member.getId());
    doc.setTeamId(team.getId());
    doc.setOrgId(team.getOrgId());
    doc.setTeamName(team.getTeamName());
    doc.setOrgName(org == null ? "" : org.getOrgName());
    doc.setAgentName(member.getDisplayName());
    doc.setAgentAvatarUrl(member.getAvatarUrl());
    doc.setAgentJobTitle(member.getJobTitle());
    doc.setLastMessage("");
    doc.setLastSenderRole("");
    doc.setStudentMessageCount(0);
    doc.setAgentMessageCount(0);
    doc.setUnreadByStudent(0);
    doc.setUnreadByAgent(0);
    doc.setStarredByStudent(false);
    doc.setStarredByAgent(false);
    doc.setCreatedAt(now);
    doc.setUpdatedAt(now);
    return chatConversationRepository.save(doc);
  }

  private ChatMessageDoc appendMessage(ChatConversationDoc conversation, Long senderUserId, String contentType, String content) {
    boolean studentSender = Objects.equals(senderUserId, conversation.getStudentUserId());
    Long receiverUserId = studentSender ? conversation.getAgentUserId() : conversation.getStudentUserId();
    Instant now = Instant.now();
    ChatMessageDoc message = new ChatMessageDoc();
    message.setConversationId(conversation.getId());
    message.setSenderUserId(senderUserId);
    message.setReceiverUserId(receiverUserId);
    message.setSenderRole(studentSender ? ROLE_STUDENT : ROLE_AGENT_MEMBER);
    message.setContentType(contentType);
    message.setContent(content);
    message.setStatus(isReceiverActive(receiverUserId, conversation.getId()) ? STATUS_READ : STATUS_UNREAD);
    message.setCreatedAt(now);
    message.setReadAt(STATUS_READ.equals(message.getStatus()) ? now : null);
    ChatMessageDoc saved = chatMessageRepository.save(message);

    int studentUnread = safeInt(conversation.getUnreadByStudent());
    int agentUnread = safeInt(conversation.getUnreadByAgent());
    if (!STATUS_READ.equals(saved.getStatus())) {
      if (studentSender) {
        agentUnread += 1;
      } else {
        studentUnread += 1;
      }
    }
    conversation.setLastMessage(buildLastMessage(contentType, content));
    conversation.setLastSenderRole(message.getSenderRole());
    conversation.setStudentMessageCount(safeInt(conversation.getStudentMessageCount()) + (studentSender ? 1 : 0));
    conversation.setAgentMessageCount(safeInt(conversation.getAgentMessageCount()) + (studentSender ? 0 : 1));
    conversation.setUnreadByStudent(studentUnread);
    conversation.setUnreadByAgent(agentUnread);
    conversation.setUpdatedAt(now);
    chatConversationRepository.save(conversation);

    MessageDtos.ChatMessageItem payload = toMessageItem(saved, receiverUserId);
    messagingTemplate.convertAndSendToUser(String.valueOf(receiverUserId), "/queue/chats", payload);
    messagingTemplate.convertAndSendToUser(String.valueOf(senderUserId), "/queue/chats", toMessageItem(saved, senderUserId));
    return saved;
  }

  private ChatConversationDoc requireParticipant(Long userId, String conversationId) {
    if (!StringUtils.hasText(conversationId)) {
      throw new BizException("BIZ_BAD_REQUEST", "会话不存在");
    }
    ChatConversationDoc conversation = chatConversationRepository.findById(conversationId)
      .orElseThrow(() -> new BizException("BIZ_NOT_FOUND", "会话不存在"));
    if (!Objects.equals(userId, conversation.getStudentUserId()) && !Objects.equals(userId, conversation.getAgentUserId())) {
      throw new BizException("BIZ_FORBIDDEN", "无权查看该会话");
    }
    return conversation;
  }

  private MessageDtos.ChatConversationItem toConversationItem(ChatConversationDoc doc, Long viewerUserId) {
    boolean studentViewer = isStudentParticipant(doc, viewerUserId);
    StudentProfile student = studentProfileMapper.findByUserId(doc.getStudentUserId());
    MessageDtos.ChatConversationItem item = new MessageDtos.ChatConversationItem();
    item.setConversationId(doc.getId());
    item.setStudentUserId(doc.getStudentUserId());
    item.setAgentUserId(doc.getAgentUserId());
    item.setAgentMemberId(doc.getAgentMemberId());
    item.setTeamId(doc.getTeamId());
    item.setOrgId(doc.getOrgId());
    item.setTeamName(doc.getTeamName());
    item.setOrgName(doc.getOrgName());
    item.setAgentName(doc.getAgentName());
    item.setAgentAvatarUrl(doc.getAgentAvatarUrl());
    item.setAgentJobTitle(doc.getAgentJobTitle());
    item.setStudentName(student == null ? null : student.getName());
    item.setStudentSchoolName(student == null ? null : student.getSchoolName());
    item.setStudentMajor(student == null ? null : student.getMajor());
    item.setStudentEducationLevel(student == null ? null : student.getEducationLevel());
    item.setStudentTargetMajorText(student == null ? null : student.getTargetMajorText());
    item.setPeerName(studentViewer ? defaultStr(doc.getAgentName(), "中介顾问") : defaultStr(student == null ? null : student.getName(), "学生" + doc.getStudentUserId()));
    item.setPeerSubtitle(studentViewer ? buildAgentSubtitle(doc) : buildStudentSubtitle(student));
    item.setPeerAvatarUrl(studentViewer ? doc.getAgentAvatarUrl() : "");
    item.setLastMessage(doc.getLastMessage());
    item.setLastSenderRole(defaultStr(doc.getLastSenderRole(), ""));
    item.setStudentMessageCount(resolveMessageCount(doc, ROLE_STUDENT));
    item.setAgentMessageCount(resolveMessageCount(doc, ROLE_AGENT_MEMBER));
    item.setViewerStarred(Boolean.TRUE.equals(studentViewer ? doc.getStarredByStudent() : doc.getStarredByAgent()));
    ServiceOrder order = findRelatedOrder(doc);
    item.setRelatedOrderId(order == null ? null : order.getId());
    item.setRelatedOrderStatus(order == null ? null : order.getOrderStatus());
    item.setUnreadCount((int) getConversationUnread(doc, viewerUserId));
    item.setCreatedAt(doc.getCreatedAt());
    item.setUpdatedAt(doc.getUpdatedAt());
    return item;
  }

  private String buildAgentSubtitle(ChatConversationDoc doc) {
    String orgName = defaultStr(doc.getOrgName(), "机构待完善");
    String teamName = defaultStr(doc.getTeamName(), "套餐待完善");
    return orgName + " · " + teamName;
  }

  private String buildStudentSubtitle(StudentProfile student) {
    if (student == null) {
      return "学校待完善 · 专业待完善";
    }
    String schoolName = defaultStr(student.getSchoolName(), "学校待完善");
    String major = defaultStr(student.getMajor(), defaultStr(student.getTargetMajorText(), "专业待完善"));
    return schoolName + " · " + major;
  }

  private MessageDtos.ChatMessageItem toMessageItem(ChatMessageDoc doc, Long viewerUserId) {
    MessageDtos.ChatMessageItem item = new MessageDtos.ChatMessageItem();
    item.setId(doc.getId());
    item.setConversationId(doc.getConversationId());
    item.setSenderUserId(doc.getSenderUserId());
    item.setReceiverUserId(doc.getReceiverUserId());
    item.setSenderRole(doc.getSenderRole());
    item.setContentType(doc.getContentType());
    item.setContent(doc.getContent());
    item.setStatus(doc.getStatus());
    item.setMine(Objects.equals(doc.getSenderUserId(), viewerUserId));
    item.setCreatedAt(doc.getCreatedAt());
    item.setReadAt(doc.getReadAt());
    return item;
  }

  private long getConversationUnread(ChatConversationDoc doc, Long userId) {
    return isStudentParticipant(doc, userId) ? safeInt(doc.getUnreadByStudent()) : safeInt(doc.getUnreadByAgent());
  }

  private String normalizeConversationFilter(String value) {
    if (!StringUtils.hasText(value)) return FILTER_ALL;
    String filter = value.trim().toLowerCase();
    if (FILTER_NEW.equals(filter) ||
      FILTER_UNREAD.equals(filter) ||
      FILTER_ACTIVE.equals(filter) ||
      FILTER_SERVICING.equals(filter) ||
      FILTER_ENDED.equals(filter) ||
      FILTER_STARRED.equals(filter)) {
      return filter;
    }
    return FILTER_ALL;
  }

  private boolean matchesConversationFilter(MessageDtos.ChatConversationItem item, String filter, String viewerRole) {
    if (FILTER_ALL.equals(filter)) return true;
    if (FILTER_UNREAD.equals(filter)) return item.getUnreadCount() > 0;
    if (FILTER_STARRED.equals(filter)) return item.isViewerStarred();
    if (FILTER_NEW.equals(filter)) {
      return ROLE_AGENT_MEMBER.equals(viewerRole) && item.getStudentMessageCount() > 0 && item.getAgentMessageCount() == 0;
    }
    String relationshipStage = resolveRelationshipStage(item);
    if (FILTER_ACTIVE.equals(filter)) return FILTER_ACTIVE.equals(relationshipStage);
    if (FILTER_SERVICING.equals(filter)) return FILTER_SERVICING.equals(relationshipStage);
    if (FILTER_ENDED.equals(filter)) return FILTER_ENDED.equals(relationshipStage);
    return true;
  }

  private String resolveRelationshipStage(MessageDtos.ChatConversationItem item) {
    String status = item.getRelatedOrderStatus();
    if ("PAID".equals(status) || "IN_SERVICE".equals(status)) return FILTER_SERVICING;
    if ("COMPLETED".equals(status) || "CLOSED".equals(status) || "REFUND_REQUESTED".equals(status)) return FILTER_ENDED;
    return FILTER_ACTIVE;
  }

  private ServiceOrder findRelatedOrder(ChatConversationDoc doc) {
    if (doc.getStudentUserId() == null || doc.getOrgId() == null || doc.getTeamId() == null) return null;
    return serviceOrderMapper.findLatestByConversationKeys(doc.getStudentUserId(), doc.getOrgId(), doc.getTeamId());
  }

  private int resolveMessageCount(ChatConversationDoc doc, String senderRole) {
    Integer savedCount = ROLE_STUDENT.equals(senderRole) ? doc.getStudentMessageCount() : doc.getAgentMessageCount();
    if (savedCount != null) return savedCount;
    Query query = Query.query(Criteria.where("conversationId").is(doc.getId()).and("senderRole").is(senderRole));
    return (int) mongoTemplate.count(query, ChatMessageDoc.class);
  }

  private boolean isStudentParticipant(ChatConversationDoc doc, Long userId) {
    return Objects.equals(doc.getStudentUserId(), userId);
  }

  private boolean isReceiverActive(Long receiverUserId, String conversationId) {
    String active = redisTemplate.opsForValue().get("chat:active:user:" + receiverUserId);
    return conversationId.equals(active);
  }

  private void setActiveConversation(Long userId, String conversationId) {
    redisTemplate.opsForValue().set("chat:online:user:" + userId, "1", Duration.ofMinutes(5));
    redisTemplate.opsForValue().set("chat:active:user:" + userId, conversationId, Duration.ofMinutes(5));
  }

  private String normalizeMessage(String content, String fallback) {
    return normalizeMessage(content, fallback, MESSAGE_TEXT);
  }

  private String normalizeMessage(String content, String fallback, String contentType) {
    String value = StringUtils.hasText(content) ? content.trim() : fallback;
    if (!StringUtils.hasText(value)) {
      throw new BizException("BIZ_BAD_REQUEST", "消息内容不能为空");
    }
    if (MESSAGE_TEXT.equals(contentType) && value.length() > 1000) {
      throw new BizException("BIZ_BAD_REQUEST", "消息内容不能超过1000字");
    }
    if (!MESSAGE_TEXT.equals(contentType) && !isUploadUrl(value)) {
      throw new BizException("BIZ_BAD_REQUEST", "附件地址不合法");
    }
    return value;
  }

  private String normalizeContentType(String value) {
    if (!StringUtils.hasText(value)) return MESSAGE_TEXT;
    String type = value.trim().toUpperCase();
    if (MESSAGE_TEXT.equals(type) || MESSAGE_IMAGE.equals(type) || MESSAGE_FILE.equals(type)) {
      return type;
    }
    throw new BizException("BIZ_BAD_REQUEST", "消息类型不支持");
  }

  private boolean isUploadUrl(String value) {
    return value.startsWith("/uploads/") || value.contains("/uploads/");
  }

  private String buildLastMessage(String contentType, String content) {
    if (MESSAGE_IMAGE.equals(contentType)) return "[图片]";
    if (MESSAGE_FILE.equals(contentType)) return "[文件] " + fileNameFromUrl(content);
    return content;
  }

  private String fileNameFromUrl(String value) {
    int slash = value.lastIndexOf('/');
    String name = slash >= 0 ? value.substring(slash + 1) : value;
    int query = name.indexOf('?');
    return query >= 0 ? name.substring(0, query) : name;
  }

  private AgencyMemberProfile requireAgentChatAccess(Long userId) {
    UserAccount user = requireUser(userId);
    if (!ROLE_AGENT_MEMBER.equals(user.getRole())) {
      throw new BizException("BIZ_FORBIDDEN", "仅中介顾问可发起学生沟通");
    }
    AgencyMemberProfile member = agencyMemberProfileMapper.findByUserId(userId);
    if (member == null || !"ACTIVE".equals(member.getStatus())) {
      throw new BizException("BIZ_NOT_FOUND", "成员档案不存在");
    }
    AgencyOrg org = agencyOrgMapper.findById(member.getOrgId());
    if (org == null || !"APPROVED".equals(org.getVerificationStatus())) {
      throw new BizException("BIZ_FORBIDDEN", "当前机构尚未通过认证");
    }
    VerificationRecord cert = verificationRecordMapper.findOne(userId, "AGENT_MEMBER_CERT");
    if (cert == null || !"APPROVED".equals(cert.getStatus())) {
      throw new BizException("BIZ_FORBIDDEN", "员工认证未通过，暂不可执行该操作");
    }
    if (!agencyMemberPermissionRelMapper.listPermissionCodesByMemberId(member.getId()).contains("CAN_CHAT_STUDENT")) {
      throw new BizException("BIZ_FORBIDDEN", "你当前没有沟通学生权限，请联系机构管理员开通");
    }
    return member;
  }

  private UserAccount requireUser(Long userId) {
    UserAccount user = userAccountMapper.findById(userId);
    if (user == null || !"ACTIVE".equals(user.getStatus())) {
      throw new BizException("BIZ_UNAUTHORIZED", "未登录或登录已过期");
    }
    return user;
  }

  private void requireStudentRole(Long userId) {
    UserAccount user = requireUser(userId);
    if (!ROLE_STUDENT.equals(user.getRole())) {
      throw new BizException("BIZ_FORBIDDEN", "仅学生用户可发起沟通");
    }
  }

  private void requireStudentVerified(Long userId) {
    VerificationRecord realName = verificationRecordMapper.findOne(userId, "REAL_NAME");
    VerificationRecord education = verificationRecordMapper.findOne(userId, "EDUCATION");
    boolean completed = realName != null && education != null
      && "APPROVED".equals(realName.getStatus())
      && "APPROVED".equals(education.getStatus());
    if (!completed) {
      throw new BizException("BIZ_FORBIDDEN", "当前账号尚未完成认证，暂不可执行该操作");
    }
  }

  private int safeInt(Integer value) {
    return value == null ? 0 : value;
  }

  private String defaultStr(String value, String fallback) {
    return StringUtils.hasText(value) ? value : fallback;
  }
}
