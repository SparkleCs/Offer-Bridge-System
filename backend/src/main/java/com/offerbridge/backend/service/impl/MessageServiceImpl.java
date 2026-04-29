package com.offerbridge.backend.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.offerbridge.backend.dto.MessageDtos;
import com.offerbridge.backend.entity.AgencyMemberProfile;
import com.offerbridge.backend.entity.AgencyOrg;
import com.offerbridge.backend.entity.AgencyTeam;
import com.offerbridge.backend.entity.ServiceOrder;
import com.offerbridge.backend.entity.StudentCompetitionExperience;
import com.offerbridge.backend.entity.StudentExchangeExperience;
import com.offerbridge.backend.entity.StudentLanguageScore;
import com.offerbridge.backend.entity.StudentPublication;
import com.offerbridge.backend.entity.StudentProfile;
import com.offerbridge.backend.entity.StudentResearchExperience;
import com.offerbridge.backend.entity.StudentTargetCountry;
import com.offerbridge.backend.entity.StudentWorkExperience;
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
import com.offerbridge.backend.mapper.StudentCompetitionExperienceMapper;
import com.offerbridge.backend.mapper.StudentExchangeExperienceMapper;
import com.offerbridge.backend.mapper.StudentLanguageScoreMapper;
import com.offerbridge.backend.mapper.StudentProfileMapper;
import com.offerbridge.backend.mapper.StudentPublicationMapper;
import com.offerbridge.backend.mapper.StudentResearchExperienceMapper;
import com.offerbridge.backend.mapper.StudentTargetCountryMapper;
import com.offerbridge.backend.mapper.StudentWorkExperienceMapper;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {
  private static final String ROLE_STUDENT = "STUDENT";
  private static final String ROLE_AGENT_MEMBER = "AGENT_MEMBER";
  private static final String MESSAGE_TEXT = "TEXT";
  private static final String MESSAGE_IMAGE = "IMAGE";
  private static final String MESSAGE_FILE = "FILE";
  private static final String MESSAGE_ACTION_CARD = "ACTION_CARD";
  private static final String STATUS_UNREAD = "UNREAD";
  private static final String STATUS_READ = "READ";
  private static final String ACTION_RESUME_ACCESS = "RESUME_ACCESS";
  private static final String ACTION_PHONE_EXCHANGE = "PHONE_EXCHANGE";
  private static final String ACTION_WECHAT_EXCHANGE = "WECHAT_EXCHANGE";
  private static final String ACTION_PENDING = "PENDING";
  private static final String ACTION_APPROVED = "APPROVED";
  private static final String ACTION_REJECTED = "REJECTED";
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
  private final StudentLanguageScoreMapper studentLanguageScoreMapper;
  private final StudentTargetCountryMapper studentTargetCountryMapper;
  private final StudentResearchExperienceMapper studentResearchExperienceMapper;
  private final StudentPublicationMapper studentPublicationMapper;
  private final StudentCompetitionExperienceMapper studentCompetitionExperienceMapper;
  private final StudentWorkExperienceMapper studentWorkExperienceMapper;
  private final StudentExchangeExperienceMapper studentExchangeExperienceMapper;
  private final ObjectMapper objectMapper;

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
                            ServiceOrderMapper serviceOrderMapper,
                            StudentLanguageScoreMapper studentLanguageScoreMapper,
                            StudentTargetCountryMapper studentTargetCountryMapper,
                            StudentResearchExperienceMapper studentResearchExperienceMapper,
                            StudentPublicationMapper studentPublicationMapper,
                            StudentCompetitionExperienceMapper studentCompetitionExperienceMapper,
                            StudentWorkExperienceMapper studentWorkExperienceMapper,
                            StudentExchangeExperienceMapper studentExchangeExperienceMapper,
                            ObjectMapper objectMapper) {
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
    this.studentLanguageScoreMapper = studentLanguageScoreMapper;
    this.studentTargetCountryMapper = studentTargetCountryMapper;
    this.studentResearchExperienceMapper = studentResearchExperienceMapper;
    this.studentPublicationMapper = studentPublicationMapper;
    this.studentCompetitionExperienceMapper = studentCompetitionExperienceMapper;
    this.studentWorkExperienceMapper = studentWorkExperienceMapper;
    this.studentExchangeExperienceMapper = studentExchangeExperienceMapper;
    this.objectMapper = objectMapper;
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
  public MessageDtos.ChatMessageItem startChatAction(Long userId, String conversationId, MessageDtos.ChatActionRequest request) {
    ChatConversationDoc conversation = requireParticipant(userId, conversationId);
    String actionType = normalizeActionType(request.getActionType());
    boolean studentRequester = isStudentParticipant(conversation, userId);
    Instant now = Instant.now();
    String actionStatus = ACTION_PENDING;

    if (ACTION_RESUME_ACCESS.equals(actionType) && studentRequester) {
      grantConversationAction(conversation, actionType, now);
      actionStatus = ACTION_APPROVED;
    } else if (ACTION_PHONE_EXCHANGE.equals(actionType) && Boolean.TRUE.equals(conversation.getPhoneExchangeGranted())) {
      actionStatus = ACTION_APPROVED;
    } else if (ACTION_WECHAT_EXCHANGE.equals(actionType)) {
      requireWechatForUser(conversation, userId);
      if (Boolean.TRUE.equals(conversation.getWechatExchangeGranted())) {
        actionStatus = ACTION_APPROVED;
      }
    }

    String content = serializeActionPayload(actionType, actionStatus, userId, studentRequester ? ROLE_STUDENT : ROLE_AGENT_MEMBER, null, now, null);
    ChatMessageDoc message = appendMessage(conversation, userId, MESSAGE_ACTION_CARD, content);
    return toMessageItem(message, userId);
  }

  @Override
  public MessageDtos.ChatMessageItem respondChatAction(Long userId, String conversationId, String actionId, MessageDtos.ChatActionRespondRequest request) {
    ChatConversationDoc conversation = requireParticipant(userId, conversationId);
    ChatMessageDoc message = chatMessageRepository.findById(actionId)
      .orElseThrow(() -> new BizException("BIZ_NOT_FOUND", "请求卡片不存在"));
    if (!conversationId.equals(message.getConversationId()) || !MESSAGE_ACTION_CARD.equals(message.getContentType())) {
      throw new BizException("BIZ_BAD_REQUEST", "请求卡片不存在");
    }
    Map<String, Object> payload = parseActionPayload(message.getContent());
    String actionType = String.valueOf(payload.getOrDefault("actionType", ""));
    String actionStatus = String.valueOf(payload.getOrDefault("status", ""));
    Long requesterUserId = toLong(payload.get("requesterUserId"));
    if (!ACTION_PENDING.equals(actionStatus)) {
      throw new BizException("BIZ_BAD_REQUEST", "该请求已处理");
    }
    if (Objects.equals(requesterUserId, userId)) {
      throw new BizException("BIZ_BAD_REQUEST", "不能处理自己发起的请求");
    }
    if (ACTION_RESUME_ACCESS.equals(actionType) && !isStudentParticipant(conversation, userId)) {
      throw new BizException("BIZ_FORBIDDEN", "仅学生可授权查看简历");
    }

    boolean approved = Boolean.TRUE.equals(request.getApproved());
    Instant now = Instant.now();
    String nextStatus = approved ? ACTION_APPROVED : ACTION_REJECTED;
    if (approved) {
      if (ACTION_WECHAT_EXCHANGE.equals(actionType)) {
        requireWechatForUser(conversation, conversation.getStudentUserId());
        requireWechatForUser(conversation, conversation.getAgentUserId());
      }
      grantConversationAction(conversation, actionType, now);
    }
    message.setContent(serializeActionPayload(
      actionType,
      nextStatus,
      requesterUserId,
      String.valueOf(payload.getOrDefault("requesterRole", "")),
      userId,
      toInstant(payload.get("requestedAt")),
      now
    ));
    ChatMessageDoc saved = chatMessageRepository.save(message);
    MessageDtos.ChatMessageItem studentPayload = toMessageItem(saved, conversation.getStudentUserId());
    MessageDtos.ChatMessageItem agentPayload = toMessageItem(saved, conversation.getAgentUserId());
    messagingTemplate.convertAndSendToUser(String.valueOf(conversation.getStudentUserId()), "/queue/chats", studentPayload);
    messagingTemplate.convertAndSendToUser(String.valueOf(conversation.getAgentUserId()), "/queue/chats", agentPayload);
    return toMessageItem(saved, userId);
  }

  @Override
  public MessageDtos.StudentAcademicResumeView getStudentResume(Long userId, String conversationId) {
    ChatConversationDoc conversation = requireParticipant(userId, conversationId);
    if (!isStudentParticipant(conversation, userId) && !Boolean.TRUE.equals(conversation.getResumeAccessGranted())) {
      throw new BizException("BIZ_FORBIDDEN", "学生尚未授权查看简历");
    }
    StudentProfile profile = studentProfileMapper.findByUserId(conversation.getStudentUserId());
    if (profile == null) {
      throw new BizException("BIZ_NOT_FOUND", "学生资料不存在");
    }
    MessageDtos.StudentAcademicResumeView view = new MessageDtos.StudentAcademicResumeView();
    view.setStudentUserId(profile.getUserId());
    view.setDisplayName(maskStudentName(profile.getName(), profile.getUserId()));
    view.setEducationLevel(profile.getEducationLevel());
    view.setSchoolName(profile.getSchoolName());
    view.setMajor(profile.getMajor());
    view.setGpaValue(profile.getGpaValue());
    view.setGpaScale(profile.getGpaScale());
    view.setRankValue(profile.getRankValue());
    view.setTargetMajorText(profile.getTargetMajorText());
    view.setIntakeTerm(profile.getIntakeTerm());
    view.setLanguageScores(studentLanguageScoreMapper.listByUserId(profile.getUserId()).stream().map(this::toResumeLanguageScore).toList());
    view.setTargetCountries(studentTargetCountryMapper.listByUserId(profile.getUserId()).stream().map(this::toResumeTargetCountry).toList());
    view.setExchangeExperience(toResumeExchange(studentExchangeExperienceMapper.findByUserId(profile.getUserId())));
    view.setResearchExperiences(buildResumeResearch(profile.getUserId()));
    view.setCompetitionExperiences(studentCompetitionExperienceMapper.listByUserId(profile.getUserId()).stream().map(this::toResumeCompetition).toList());
    view.setWorkExperiences(studentWorkExperienceMapper.listByUserId(profile.getUserId()).stream().map(this::toResumeWork).toList());
    return view;
  }

  @Override
  public MessageDtos.ContactExchangeView getExchangedContact(Long userId, String conversationId, String contactType) {
    ChatConversationDoc conversation = requireParticipant(userId, conversationId);
    String type = normalizeContactType(contactType);
    if (ACTION_PHONE_EXCHANGE.equals(type) && !Boolean.TRUE.equals(conversation.getPhoneExchangeGranted())) {
      throw new BizException("BIZ_FORBIDDEN", "双方尚未同意交换电话");
    }
    if (ACTION_WECHAT_EXCHANGE.equals(type) && !Boolean.TRUE.equals(conversation.getWechatExchangeGranted())) {
      throw new BizException("BIZ_FORBIDDEN", "双方尚未同意交换微信");
    }
    boolean studentViewer = isStudentParticipant(conversation, userId);
    Long ownUserId = studentViewer ? conversation.getStudentUserId() : conversation.getAgentUserId();
    Long peerUserId = studentViewer ? conversation.getAgentUserId() : conversation.getStudentUserId();

    MessageDtos.ContactExchangeView view = new MessageDtos.ContactExchangeView();
    view.setContactType(type);
    view.setOwnContact(ACTION_PHONE_EXCHANGE.equals(type) ? phoneForUser(ownUserId) : wechatForUser(conversation, ownUserId));
    view.setPeerContact(ACTION_PHONE_EXCHANGE.equals(type) ? phoneForUser(peerUserId) : wechatForUser(conversation, peerUserId));
    return view;
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
    doc.setResumeAccessGranted(false);
    doc.setPhoneExchangeGranted(false);
    doc.setWechatExchangeGranted(false);
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
    item.setResumeAccessGranted(Boolean.TRUE.equals(doc.getResumeAccessGranted()));
    item.setPhoneExchangeGranted(Boolean.TRUE.equals(doc.getPhoneExchangeGranted()));
    item.setWechatExchangeGranted(Boolean.TRUE.equals(doc.getWechatExchangeGranted()));
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
    if (!MESSAGE_TEXT.equals(contentType) && !MESSAGE_ACTION_CARD.equals(contentType) && !isUploadUrl(value)) {
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
    if (MESSAGE_ACTION_CARD.equals(contentType)) return buildActionLastMessage(content);
    return content;
  }

  private String buildActionLastMessage(String content) {
    Map<String, Object> payload = parseActionPayload(content);
    String actionType = String.valueOf(payload.getOrDefault("actionType", ""));
    if (ACTION_RESUME_ACCESS.equals(actionType)) return "[简历请求]";
    if (ACTION_PHONE_EXCHANGE.equals(actionType)) return "[电话互换]";
    if (ACTION_WECHAT_EXCHANGE.equals(actionType)) return "[微信互换]";
    return "[互动请求]";
  }

  private String normalizeActionType(String value) {
    if (!StringUtils.hasText(value)) {
      throw new BizException("BIZ_BAD_REQUEST", "请选择请求类型");
    }
    String type = value.trim().toUpperCase();
    if (ACTION_RESUME_ACCESS.equals(type) || ACTION_PHONE_EXCHANGE.equals(type) || ACTION_WECHAT_EXCHANGE.equals(type)) {
      return type;
    }
    throw new BizException("BIZ_BAD_REQUEST", "请求类型不支持");
  }

  private String normalizeContactType(String value) {
    String type = normalizeActionType(value);
    if (ACTION_PHONE_EXCHANGE.equals(type) || ACTION_WECHAT_EXCHANGE.equals(type)) {
      return type;
    }
    throw new BizException("BIZ_BAD_REQUEST", "联系方式类型不支持");
  }

  private String serializeActionPayload(String actionType,
                                        String status,
                                        Long requesterUserId,
                                        String requesterRole,
                                        Long responderUserId,
                                        Instant requestedAt,
                                        Instant respondedAt) {
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("actionType", actionType);
    payload.put("status", status);
    payload.put("requesterUserId", requesterUserId);
    payload.put("requesterRole", requesterRole);
    payload.put("responderUserId", responderUserId);
    payload.put("requestedAt", requestedAt == null ? Instant.now().toString() : requestedAt.toString());
    payload.put("respondedAt", respondedAt == null ? null : respondedAt.toString());
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (Exception ex) {
      throw new BizException("BIZ_INTERNAL_ERROR", "系统异常");
    }
  }

  private Map<String, Object> parseActionPayload(String content) {
    try {
      return objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {});
    } catch (Exception ex) {
      throw new BizException("BIZ_BAD_REQUEST", "请求卡片内容异常");
    }
  }

  private void grantConversationAction(ChatConversationDoc conversation, String actionType, Instant now) {
    if (ACTION_RESUME_ACCESS.equals(actionType)) {
      conversation.setResumeAccessGranted(true);
      conversation.setResumeAccessUpdatedAt(now);
    } else if (ACTION_PHONE_EXCHANGE.equals(actionType)) {
      conversation.setPhoneExchangeGranted(true);
      conversation.setPhoneExchangeUpdatedAt(now);
    } else if (ACTION_WECHAT_EXCHANGE.equals(actionType)) {
      conversation.setWechatExchangeGranted(true);
      conversation.setWechatExchangeUpdatedAt(now);
    }
    chatConversationRepository.save(conversation);
  }

  private Long toLong(Object value) {
    if (value == null) return null;
    if (value instanceof Number number) return number.longValue();
    try {
      return Long.valueOf(String.valueOf(value));
    } catch (Exception ex) {
      return null;
    }
  }

  private Instant toInstant(Object value) {
    if (value == null) return Instant.now();
    try {
      return Instant.parse(String.valueOf(value));
    } catch (Exception ex) {
      return Instant.now();
    }
  }

  private String phoneForUser(Long userId) {
    UserAccount user = userAccountMapper.findById(userId);
    if (user == null || !StringUtils.hasText(user.getPhone())) {
      throw new BizException("BIZ_NOT_FOUND", "联系方式不存在");
    }
    return user.getPhone();
  }

  private String wechatForUser(ChatConversationDoc conversation, Long userId) {
    String wechatId;
    if (Objects.equals(userId, conversation.getStudentUserId())) {
      StudentProfile profile = studentProfileMapper.findByUserId(userId);
      wechatId = profile == null ? null : profile.getWechatId();
    } else if (Objects.equals(userId, conversation.getAgentUserId())) {
      AgencyMemberProfile member = agencyMemberProfileMapper.findByUserId(userId);
      wechatId = member == null ? null : member.getWechatId();
    } else {
      throw new BizException("BIZ_FORBIDDEN", "无权查看该联系方式");
    }
    if (!StringUtils.hasText(wechatId)) {
      throw new BizException("BIZ_BAD_REQUEST", "请先完善微信号");
    }
    return wechatId.trim();
  }

  private void requireWechatForUser(ChatConversationDoc conversation, Long userId) {
    wechatForUser(conversation, userId);
  }

  private String maskStudentName(String name, Long userId) {
    if (StringUtils.hasText(name)) {
      return name.trim().substring(0, 1) + "同学";
    }
    return "学生" + userId;
  }

  private MessageDtos.LanguageScoreItem toResumeLanguageScore(StudentLanguageScore score) {
    MessageDtos.LanguageScoreItem item = new MessageDtos.LanguageScoreItem();
    item.setLanguageType(score.getLanguageType());
    item.setScore(score.getScore());
    return item;
  }

  private MessageDtos.TargetCountryItem toResumeTargetCountry(StudentTargetCountry country) {
    MessageDtos.TargetCountryItem item = new MessageDtos.TargetCountryItem();
    item.setCountryName(country.getCountryName());
    return item;
  }

  private MessageDtos.ExchangeExperienceItem toResumeExchange(StudentExchangeExperience entity) {
    if (entity == null) return null;
    MessageDtos.ExchangeExperienceItem item = new MessageDtos.ExchangeExperienceItem();
    item.setCountryName(entity.getCountryName());
    item.setUniversityName(entity.getUniversityName());
    item.setGpaValue(entity.getGpaValue());
    item.setMajorCourses(entity.getMajorCourses());
    item.setStartDate(entity.getStartDate());
    item.setEndDate(entity.getEndDate());
    return item;
  }

  private List<MessageDtos.ResearchItem> buildResumeResearch(Long studentUserId) {
    List<StudentPublication> publications = studentPublicationMapper.listByUserId(studentUserId);
    Map<Long, List<MessageDtos.PublicationItem>> publicationMap = publications.stream()
      .collect(Collectors.groupingBy(StudentPublication::getResearchId, LinkedHashMap::new, Collectors.mapping(this::toResumePublication, Collectors.toList())));
    return studentResearchExperienceMapper.listByUserId(studentUserId).stream().map(item -> {
      MessageDtos.ResearchItem view = new MessageDtos.ResearchItem();
      view.setId(item.getId());
      view.setProjectName(item.getProjectName());
      view.setStartDate(item.getStartDate());
      view.setEndDate(item.getEndDate());
      view.setContentSummary(item.getContentSummary());
      view.setHasPublication(item.getHasPublication());
      view.setPublications(publicationMap.getOrDefault(item.getId(), List.of()));
      return view;
    }).toList();
  }

  private MessageDtos.PublicationItem toResumePublication(StudentPublication publication) {
    MessageDtos.PublicationItem item = new MessageDtos.PublicationItem();
    item.setTitle(publication.getTitle());
    item.setAuthorRole(publication.getAuthorRole());
    item.setJournalName(publication.getJournalName());
    item.setPublishedYear(publication.getPublishedYear());
    return item;
  }

  private MessageDtos.CompetitionItem toResumeCompetition(StudentCompetitionExperience entity) {
    MessageDtos.CompetitionItem item = new MessageDtos.CompetitionItem();
    item.setId(entity.getId());
    item.setCompetitionName(entity.getCompetitionName());
    item.setCompetitionLevel(entity.getCompetitionLevel());
    item.setAward(entity.getAward());
    item.setRoleDesc(entity.getRoleDesc());
    item.setEventDate(entity.getEventDate());
    return item;
  }

  private MessageDtos.WorkItem toResumeWork(StudentWorkExperience entity) {
    MessageDtos.WorkItem item = new MessageDtos.WorkItem();
    item.setId(entity.getId());
    item.setCompanyName(entity.getCompanyName());
    item.setPositionName(entity.getPositionName());
    item.setStartDate(entity.getStartDate());
    item.setEndDate(entity.getEndDate());
    item.setKeywords(entity.getKeywords());
    item.setContentSummary(entity.getContentSummary());
    return item;
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
