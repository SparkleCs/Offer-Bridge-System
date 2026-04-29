package com.offerbridge.backend.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.offerbridge.backend.config.AppProperties;
import com.offerbridge.backend.dto.OrderDtos;
import com.offerbridge.backend.entity.PaymentRecord;
import com.offerbridge.backend.entity.AgencyMemberProfile;
import com.offerbridge.backend.entity.AgencyOrg;
import com.offerbridge.backend.entity.AgencyTeam;
import com.offerbridge.backend.entity.ServiceOrder;
import com.offerbridge.backend.entity.UserAccount;
import com.offerbridge.backend.exception.BizException;
import com.offerbridge.backend.mapper.AgencyMemberProfileMapper;
import com.offerbridge.backend.mapper.AgencyOrgMapper;
import com.offerbridge.backend.mapper.AgencyTeamMapper;
import com.offerbridge.backend.mapper.PaymentRecordMapper;
import com.offerbridge.backend.mapper.RefundRequestMapper;
import com.offerbridge.backend.mapper.ServiceStageAttachmentMapper;
import com.offerbridge.backend.mapper.ServiceCaseMapper;
import com.offerbridge.backend.mapper.ServiceOrderMapper;
import com.offerbridge.backend.mapper.ServiceStageMapper;
import com.offerbridge.backend.mapper.ServiceTodoMapper;
import com.offerbridge.backend.mapper.UserAccountMapper;
import com.offerbridge.backend.service.OrderService;
import com.offerbridge.backend.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
  private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

  private static final String[][] DEFAULT_STAGES = {
    {"CONSULT_CONFIRM", "咨询确认"},
    {"MATERIAL_PREP", "材料准备"},
    {"SCHOOL_PLAN", "选校定校"},
    {"ESSAY_PREP", "文书准备"},
    {"ONLINE_SUBMIT", "网申递交"},
    {"INTERVIEW_SUPPLEMENT", "面试/补件"},
    {"OFFER_RESULT", "录取结果"},
    {"VISA_PREP", "签证准备"},
    {"SERVICE_DONE", "服务完成"}
  };

  private final ServiceOrderMapper serviceOrderMapper;
  private final PaymentRecordMapper paymentRecordMapper;
  private final ServiceCaseMapper serviceCaseMapper;
  private final ServiceStageMapper serviceStageMapper;
  private final ServiceStageAttachmentMapper serviceStageAttachmentMapper;
  private final ServiceTodoMapper serviceTodoMapper;
  private final RefundRequestMapper refundRequestMapper;
  private final AgencyTeamMapper agencyTeamMapper;
  private final AgencyOrgMapper agencyOrgMapper;
  private final AgencyMemberProfileMapper agencyMemberProfileMapper;
  private final UserAccountMapper userAccountMapper;
  private final AppProperties appProperties;
  private final ReviewService reviewService;

  public OrderServiceImpl(ServiceOrderMapper serviceOrderMapper,
                          PaymentRecordMapper paymentRecordMapper,
                          ServiceCaseMapper serviceCaseMapper,
                          ServiceStageMapper serviceStageMapper,
                          ServiceStageAttachmentMapper serviceStageAttachmentMapper,
                          ServiceTodoMapper serviceTodoMapper,
                          RefundRequestMapper refundRequestMapper,
                          AgencyTeamMapper agencyTeamMapper,
                          AgencyOrgMapper agencyOrgMapper,
                          AgencyMemberProfileMapper agencyMemberProfileMapper,
                          UserAccountMapper userAccountMapper,
                          AppProperties appProperties,
                          ReviewService reviewService) {
    this.serviceOrderMapper = serviceOrderMapper;
    this.paymentRecordMapper = paymentRecordMapper;
    this.serviceCaseMapper = serviceCaseMapper;
    this.serviceStageMapper = serviceStageMapper;
    this.serviceStageAttachmentMapper = serviceStageAttachmentMapper;
    this.serviceTodoMapper = serviceTodoMapper;
    this.refundRequestMapper = refundRequestMapper;
    this.agencyTeamMapper = agencyTeamMapper;
    this.agencyOrgMapper = agencyOrgMapper;
    this.agencyMemberProfileMapper = agencyMemberProfileMapper;
    this.userAccountMapper = userAccountMapper;
    this.appProperties = appProperties;
    this.reviewService = reviewService;
  }

  @Override
  @Transactional
  public OrderDtos.OrderSummary createStudentOrder(Long userId, OrderDtos.CreateOrderRequest request) {
    requireRole(userId, "STUDENT");
    AgencyTeam team = agencyTeamMapper.findById(request.getTeamId());
    if (team == null || !"PUBLISHED".equals(team.getPublishStatus())) {
      throw new BizException("BIZ_BAD_REQUEST", "套餐不存在或尚未发布");
    }
    AgencyOrg org = agencyOrgMapper.findById(team.getOrgId());
    if (org == null || !"APPROVED".equals(org.getVerificationStatus())) {
      throw new BizException("BIZ_BAD_REQUEST", "机构尚未通过认证");
    }
    ServiceOrder order = new ServiceOrder();
    order.setOrderNo(newOrderNo("SO"));
    order.setStudentUserId(userId);
    order.setOrgId(org.getId());
    order.setTeamId(team.getId());
    order.setTeamNameSnapshot(team.getTeamName());
    order.setOrgNameSnapshot(org.getOrgName());
    order.setServiceTitle(team.getTeamName());
    order.setQuoteDesc(blankToNull(request.getRemark()));
    order.setCurrency("CNY");
    order.setOrderStatus("PENDING_QUOTE");
    order.setPaymentStatus("UNPAID");
    serviceOrderMapper.insertOne(order);
    reviewService.snapshotOrderMembers(order.getId(), team.getId());
    return serviceOrderMapper.findStudentSummary(order.getId(), userId);
  }

  @Override
  public List<OrderDtos.OrderSummary> listStudentOrders(Long userId) {
    requireRole(userId, "STUDENT");
    return serviceOrderMapper.listByStudent(userId);
  }

  @Override
  public OrderDtos.OrderDetail getStudentOrderDetail(Long userId, Long orderId) {
    requireRole(userId, "STUDENT");
    OrderDtos.OrderSummary summary = serviceOrderMapper.findStudentSummary(orderId, userId);
    if (summary == null) throw new BizException("BIZ_NOT_FOUND", "订单不存在");
    return buildDetail(summary);
  }

  @Override
  @Transactional
  public OrderDtos.PayResult createPayment(Long userId, Long orderId) {
    ServiceOrder order = requireStudentOrderForUpdate(userId, orderId);
    if (!"WAITING_PAYMENT".equals(order.getOrderStatus()) || order.getFinalAmount() == null) {
      throw new BizException("BIZ_BAD_REQUEST", "订单尚未报价，暂不可支付");
    }
    String paymentNo = newOrderNo("PAY");
    paymentRecordMapper.insertCreated(order.getId(), paymentNo, order.getFinalAmount());
    OrderDtos.PayResult result = new OrderDtos.PayResult();
    result.setPaymentNo(paymentNo);
    result.setChannel("ALIPAY_SANDBOX");
    if (isAlipayEnabled()) {
      result.setPaymentFormHtml(buildAlipayPagePayForm(order, paymentNo));
      result.setMessage("支付宝沙箱支付已创建，请在新打开的支付宝页面完成付款。");
    } else {
      result.setPaymentUrl("/orders?mockPaymentNo=" + paymentNo);
      result.setMessage("支付宝未启用，已创建模拟支付记录。");
    }
    return result;
  }

  @Override
  @Transactional
  public OrderDtos.OrderDetail mockPaySuccess(Long userId, Long orderId) {
    ServiceOrder order = requireStudentOrderForUpdate(userId, orderId);
    if (!"WAITING_PAYMENT".equals(order.getOrderStatus()) && !"PAID".equals(order.getOrderStatus()) && !"IN_SERVICE".equals(order.getOrderStatus())) {
      throw new BizException("BIZ_BAD_REQUEST", "当前订单不可支付");
    }
    String paymentNo = newOrderNo("MOCK");
    if (paymentRecordMapper.countPaidByOrderId(order.getId()) == 0) {
      paymentRecordMapper.insertCreated(order.getId(), paymentNo, order.getFinalAmount());
      paymentRecordMapper.markPaid(paymentNo, "MOCK-" + UUID.randomUUID(), "{\"trade_status\":\"TRADE_SUCCESS\"}");
    }
    openCaseIfNeeded(order);
    return getStudentOrderDetail(userId, orderId);
  }

  @Override
  @Transactional
  public boolean handleAlipayNotify(Map<String, String> params) {
    if (!isAlipayEnabled()) {
      return false;
    }
    try {
      AppProperties.Alipay alipay = appProperties.getAlipay();
      boolean verified = AlipaySignature.rsaCheckV1(params, alipay.getAlipayPublicKey(), alipay.getCharset(), alipay.getSignType());
      if (!verified) {
        log.warn("Alipay notify signature verification failed, out_trade_no={}", params.get("out_trade_no"));
        return false;
      }
      String tradeStatus = params.get("trade_status");
      if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
        return true;
      }
      String paymentNo = params.get("out_trade_no");
      String tradeNo = params.get("trade_no");
      String totalAmountText = params.get("total_amount");
      if (paymentNo == null || paymentNo.isBlank() || totalAmountText == null || totalAmountText.isBlank()) {
        log.warn("Alipay notify missing payment number or amount, out_trade_no={}", paymentNo);
        return false;
      }
      PaymentRecord payment = paymentRecordMapper.findByPaymentNo(paymentNo);
      if (payment == null) {
        log.warn("Alipay notify payment record not found, paymentNo={}", paymentNo);
        return false;
      }
      ServiceOrder order = serviceOrderMapper.findByIdForUpdate(payment.getOrderId());
      if (order == null) {
        log.warn("Alipay notify service order not found, paymentNo={}, orderId={}", paymentNo, payment.getOrderId());
        return false;
      }
      BigDecimal notifyAmount = new BigDecimal(totalAmountText).setScale(2, RoundingMode.HALF_UP);
      BigDecimal expectedAmount = payment.getAmount().setScale(2, RoundingMode.HALF_UP);
      if (notifyAmount.compareTo(expectedAmount) != 0) {
        log.warn("Alipay notify amount mismatch, paymentNo={}, notifyAmount={}, expectedAmount={}", paymentNo, notifyAmount, expectedAmount);
        return false;
      }
      paymentRecordMapper.markPaid(paymentNo, tradeNo, params.toString());
      openCaseIfNeeded(order);
      return true;
    } catch (Exception ex) {
      log.warn("Alipay notify handling failed, out_trade_no={}", params.get("out_trade_no"), ex);
      return false;
    }
  }

  @Override
  @Transactional
  public void closeStudentOrder(Long userId, Long orderId) {
    requireRole(userId, "STUDENT");
    int updated = serviceOrderMapper.closeStudentOrder(orderId, userId);
    if (updated == 0) throw new BizException("BIZ_BAD_REQUEST", "当前订单不可取消");
  }

  @Override
  @Transactional
  public OrderDtos.OrderDetail requestRefund(Long userId, Long orderId, OrderDtos.RefundRequest request) {
    ServiceOrder order = requireStudentOrderForUpdate(userId, orderId);
    if (!"IN_SERVICE".equals(order.getOrderStatus()) && !"COMPLETED".equals(order.getOrderStatus())) {
      throw new BizException("BIZ_BAD_REQUEST", "当前订单不可申请退款");
    }
    refundRequestMapper.insertOne(orderId, userId, order.getFinalAmount(), request.getReason().trim());
    serviceOrderMapper.requestRefund(orderId, userId);
    return getStudentOrderDetail(userId, orderId);
  }

  @Override
  @Transactional
  public OrderDtos.OrderDetail confirmStage(Long userId, Long orderId, Long stageId) {
    ServiceOrder order = requireStudentOrderForUpdate(userId, orderId);
    Long caseId = requireCaseId(orderId);
    OrderDtos.StageItem stage = requireStage(stageId, caseId);
    int updated = serviceStageMapper.completeStage(stageId, caseId);
    if (updated == 0) throw new BizException("BIZ_BAD_REQUEST", "当前阶段不可确认");
    if ("SERVICE_DONE".equals(stage.getStageKey())) {
      serviceOrderMapper.markCompleted(order.getId());
    } else {
      serviceStageMapper.startNextStage(caseId, stage.getStageOrder() + 1);
    }
    return getStudentOrderDetail(userId, orderId);
  }

  @Override
  @Transactional
  public OrderDtos.OrderDetail rejectStage(Long userId, Long orderId, Long stageId, OrderDtos.RejectStageRequest request) {
    ServiceOrder order = requireStudentOrderForUpdate(userId, orderId);
    Long caseId = requireCaseId(order.getId());
    requireStage(stageId, caseId);
    int updated = serviceStageMapper.rejectStage(stageId, caseId, request.getFeedback().trim());
    if (updated == 0) throw new BizException("BIZ_BAD_REQUEST", "当前阶段不可退回");
    return getStudentOrderDetail(userId, orderId);
  }

  @Override
  @Transactional
  public OrderDtos.OrderDetail completeTodo(Long userId, Long orderId, Long todoId) {
    requireStudentOrderForUpdate(userId, orderId);
    Long caseId = requireCaseId(orderId);
    int updated = serviceTodoMapper.markCompleted(todoId, caseId);
    if (updated == 0) throw new BizException("BIZ_BAD_REQUEST", "当前待办不可提交");
    return getStudentOrderDetail(userId, orderId);
  }

  @Override
  public List<OrderDtos.AgentOrderSummary> listAgentOrders(Long userId) {
    AgencyOrg org = requireAgentOrg(userId);
    return serviceOrderMapper.listByOrg(org.getId());
  }

  @Override
  public OrderDtos.OrderDetail getAgentOrderDetail(Long userId, Long orderId) {
    AgencyOrg org = requireAgentOrg(userId);
    ServiceOrder order = serviceOrderMapper.findById(orderId);
    if (order == null || !org.getId().equals(order.getOrgId())) {
      throw new BizException("BIZ_NOT_FOUND", "订单不存在");
    }
    return buildDetail(serviceOrderMapper.findSummaryById(orderId));
  }

  @Override
  @Transactional
  public OrderDtos.OrderDetail quoteOrder(Long userId, Long orderId, OrderDtos.QuoteOrderRequest request) {
    AgencyOrg org = requireAgentOrg(userId);
    Long assignedMemberId = request.getAssignedMemberId();
    if (assignedMemberId == null) {
      AgencyMemberProfile me = agencyMemberProfileMapper.findByUserId(userId);
      assignedMemberId = me == null ? null : me.getId();
    } else if (agencyMemberProfileMapper.findByOrgAndMemberId(org.getId(), assignedMemberId) == null) {
      throw new BizException("BIZ_BAD_REQUEST", "负责成员不属于当前机构");
    }
    int updated = serviceOrderMapper.quoteOrder(
      orderId, org.getId(), request.getServiceTitle().trim(), request.getQuoteDesc().trim(), request.getFinalAmount(), assignedMemberId
    );
    if (updated == 0) throw new BizException("BIZ_BAD_REQUEST", "当前订单不可报价");
    return getAgentOrderDetail(userId, orderId);
  }

  @Override
  @Transactional
  public OrderDtos.OrderDetail submitStage(Long userId, Long orderId, Long stageId, OrderDtos.SubmitStageRequest request) {
    AgencyOrg org = requireAgentOrg(userId);
    ServiceOrder order = requireOrgOrder(orderId, org.getId());
    Long caseId = requireCaseId(order.getId());
    requireStage(stageId, caseId);
    int updated = serviceStageMapper.submitStage(stageId, caseId, request.getDeliverableText().trim(), blankToNull(request.getDeliverableUrl()));
    if (updated == 0) throw new BizException("BIZ_BAD_REQUEST", "当前阶段不可提交成果");
    replaceStageAttachments(userId, stageId, request.getAttachments());
    return getAgentOrderDetail(userId, orderId);
  }

  @Override
  @Transactional
  public OrderDtos.OrderDetail createTodo(Long userId, Long orderId, OrderDtos.CreateTodoRequest request) {
    AgencyOrg org = requireAgentOrg(userId);
    ServiceOrder order = requireOrgOrder(orderId, org.getId());
    Long caseId = requireCaseId(order.getId());
    serviceTodoMapper.insertTodo(caseId, request.getStageId(), request.getTitle().trim(), blankToNull(request.getDescription()), request.getOwnerRole().trim());
    return getAgentOrderDetail(userId, orderId);
  }

  @Override
  @Transactional
  public OrderDtos.OrderDetail confirmTodo(Long userId, Long orderId, Long todoId) {
    AgencyOrg org = requireAgentOrg(userId);
    ServiceOrder order = requireOrgOrder(orderId, org.getId());
    Long caseId = requireCaseId(order.getId());
    int updated = serviceTodoMapper.confirmCompleted(todoId, caseId);
    if (updated == 0) throw new BizException("BIZ_BAD_REQUEST", "当前待办不可确认");
    return getAgentOrderDetail(userId, orderId);
  }

  private void openCaseIfNeeded(ServiceOrder order) {
    serviceOrderMapper.markPaid(order.getId());
    reviewService.snapshotOrderMembers(order.getId(), order.getTeamId());
    if (serviceCaseMapper.findIdByOrderId(order.getId()) != null) {
      serviceOrderMapper.markInService(order.getId());
      return;
    }
    serviceCaseMapper.insertOne(order.getId(), order.getStudentUserId(), order.getOrgId(), order.getTeamId(), order.getAssignedMemberId());
    Long caseId = requireCaseId(order.getId());
    for (int i = 0; i < DEFAULT_STAGES.length; i++) {
      String status = i == 0 ? "IN_PROGRESS" : "NOT_STARTED";
      serviceStageMapper.insertStage(caseId, DEFAULT_STAGES[i][0], DEFAULT_STAGES[i][1], i + 1, status, order.getAssignedMemberId());
    }
    serviceTodoMapper.insertTodo(caseId, null, "补充个人背景", "请完善基础信息、学术背景和目标方向，方便顾问定位方案。", "STUDENT");
    serviceTodoMapper.insertTodo(caseId, null, "上传成绩单", "请在资料沟通中提供成绩单或可访问的材料链接。", "STUDENT");
    serviceTodoMapper.insertTodo(caseId, null, "确认选校方案", "材料准备后与顾问共同确认选校范围。", "STUDENT");
    serviceOrderMapper.markInService(order.getId());
  }

  private String buildAlipayPagePayForm(ServiceOrder order, String paymentNo) {
    try {
      AppProperties.Alipay alipay = appProperties.getAlipay();
      DefaultAlipayClient client = new DefaultAlipayClient(
        alipay.getGatewayUrl(),
        alipay.getAppId(),
        normalizeKey(alipay.getAppPrivateKey()),
        alipay.getFormat(),
        alipay.getCharset(),
        normalizeKey(alipay.getAlipayPublicKey()),
        alipay.getSignType()
      );
      AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
      request.setNotifyUrl(alipay.getNotifyUrl());
      request.setReturnUrl(alipay.getReturnUrl());
      request.setBizContent("{"
        + "\"out_trade_no\":\"" + escapeJson(paymentNo) + "\","
        + "\"total_amount\":\"" + order.getFinalAmount().setScale(2, RoundingMode.HALF_UP).toPlainString() + "\","
        + "\"subject\":\"" + escapeJson(defaultStr(order.getServiceTitle(), order.getTeamNameSnapshot())) + "\","
        + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\""
        + "}");
      return client.pageExecute(request, "POST").getBody();
    } catch (AlipayApiException ex) {
      throw new BizException("BIZ_PAYMENT_ERROR", "支付宝沙箱支付创建失败");
    }
  }

  private boolean isAlipayEnabled() {
    AppProperties.Alipay alipay = appProperties.getAlipay();
    return alipay.isEnabled()
      && hasText(alipay.getAppId())
      && hasText(alipay.getGatewayUrl())
      && hasText(alipay.getAppPrivateKey())
      && hasText(alipay.getAlipayPublicKey())
      && hasText(alipay.getNotifyUrl());
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  private String normalizeKey(String value) {
    return value == null ? "" : value.replace("\r", "").replace("\n", "").trim();
  }

  private String escapeJson(String value) {
    if (value == null) return "";
    return value.replace("\\", "\\\\").replace("\"", "\\\"");
  }

  private String defaultStr(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }

  private OrderDtos.OrderDetail buildDetail(OrderDtos.OrderSummary summary) {
    OrderDtos.OrderDetail detail = new OrderDtos.OrderDetail();
    detail.setOrder(summary);
    if (summary.getCaseId() == null) {
      detail.setStages(List.of());
    } else {
      List<OrderDtos.StageItem> stages = serviceStageMapper.listByOrderId(summary.getId());
      Map<Long, List<OrderDtos.StageAttachment>> attachmentsByStage = serviceStageAttachmentMapper.listByOrderId(summary.getId())
        .stream()
        .collect(Collectors.groupingBy(OrderDtos.StageAttachment::getStageId));
      for (OrderDtos.StageItem stage : stages) {
        stage.setAttachments(attachmentsByStage.getOrDefault(stage.getId(), List.of()));
      }
      detail.setStages(stages);
    }
    detail.setTodos(summary.getCaseId() == null ? List.of() : serviceTodoMapper.listByOrderId(summary.getId()));
    return detail;
  }

  private void replaceStageAttachments(Long userId, Long stageId, List<OrderDtos.StageAttachmentRequest> attachments) {
    serviceStageAttachmentMapper.deleteByStageId(stageId);
    if (attachments == null || attachments.isEmpty()) {
      return;
    }
    for (int i = 0; i < attachments.size(); i++) {
      OrderDtos.StageAttachmentRequest item = attachments.get(i);
      if (item == null || !hasText(item.getFileName()) || !hasText(item.getFileUrl())) {
        throw new BizException("BIZ_BAD_REQUEST", "附件信息不完整");
      }
      String contentType = normalizeAttachmentType(item.getContentType());
      serviceStageAttachmentMapper.insertOne(
        stageId,
        item.getFileName().trim(),
        item.getFileUrl().trim(),
        contentType,
        blankToNull(item.getMimeType()),
        item.getSizeBytes(),
        userId,
        i
      );
    }
  }

  private String normalizeAttachmentType(String contentType) {
    if (contentType == null || contentType.isBlank()) return "FILE";
    String value = contentType.trim().toUpperCase();
    return "IMAGE".equals(value) ? "IMAGE" : "FILE";
  }

  private ServiceOrder requireStudentOrderForUpdate(Long userId, Long orderId) {
    requireRole(userId, "STUDENT");
    ServiceOrder order = serviceOrderMapper.findByIdForUpdate(orderId);
    if (order == null || !userId.equals(order.getStudentUserId())) {
      throw new BizException("BIZ_NOT_FOUND", "订单不存在");
    }
    return order;
  }

  private ServiceOrder requireOrgOrder(Long orderId, Long orgId) {
    ServiceOrder order = serviceOrderMapper.findById(orderId);
    if (order == null || !orgId.equals(order.getOrgId())) {
      throw new BizException("BIZ_NOT_FOUND", "订单不存在");
    }
    return order;
  }

  private Long requireCaseId(Long orderId) {
    Long caseId = serviceCaseMapper.findIdByOrderId(orderId);
    if (caseId == null) throw new BizException("BIZ_BAD_REQUEST", "服务流程尚未开启");
    return caseId;
  }

  private OrderDtos.StageItem requireStage(Long stageId, Long caseId) {
    OrderDtos.StageItem stage = serviceStageMapper.findById(stageId);
    if (stage == null || !caseId.equals(stage.getCaseId())) {
      throw new BizException("BIZ_NOT_FOUND", "阶段不存在");
    }
    return stage;
  }

  private AgencyOrg requireAgentOrg(Long userId) {
    requireRole(userId, "AGENT_MEMBER");
    AgencyOrg org = agencyOrgMapper.findByMemberUserId(userId);
    if (org == null) throw new BizException("BIZ_NOT_FOUND", "机构不存在");
    return org;
  }

  private void requireRole(Long userId, String role) {
    UserAccount user = userAccountMapper.findById(userId);
    if (user == null || !role.equals(user.getRole())) {
      throw new BizException("BIZ_FORBIDDEN", "账号角色无权执行该操作");
    }
  }

  private String newOrderNo(String prefix) {
    return prefix + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
      + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
  }

  private String blankToNull(String value) {
    return value == null || value.isBlank() ? null : value.trim();
  }
}
