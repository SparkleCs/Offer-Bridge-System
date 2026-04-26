package com.offerbridge.backend.service.impl;

import com.offerbridge.backend.dto.OrderDtos;
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
import com.offerbridge.backend.mapper.ServiceCaseMapper;
import com.offerbridge.backend.mapper.ServiceOrderMapper;
import com.offerbridge.backend.mapper.ServiceStageMapper;
import com.offerbridge.backend.mapper.ServiceTodoMapper;
import com.offerbridge.backend.mapper.UserAccountMapper;
import com.offerbridge.backend.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
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
  private final ServiceTodoMapper serviceTodoMapper;
  private final RefundRequestMapper refundRequestMapper;
  private final AgencyTeamMapper agencyTeamMapper;
  private final AgencyOrgMapper agencyOrgMapper;
  private final AgencyMemberProfileMapper agencyMemberProfileMapper;
  private final UserAccountMapper userAccountMapper;

  public OrderServiceImpl(ServiceOrderMapper serviceOrderMapper,
                          PaymentRecordMapper paymentRecordMapper,
                          ServiceCaseMapper serviceCaseMapper,
                          ServiceStageMapper serviceStageMapper,
                          ServiceTodoMapper serviceTodoMapper,
                          RefundRequestMapper refundRequestMapper,
                          AgencyTeamMapper agencyTeamMapper,
                          AgencyOrgMapper agencyOrgMapper,
                          AgencyMemberProfileMapper agencyMemberProfileMapper,
                          UserAccountMapper userAccountMapper) {
    this.serviceOrderMapper = serviceOrderMapper;
    this.paymentRecordMapper = paymentRecordMapper;
    this.serviceCaseMapper = serviceCaseMapper;
    this.serviceStageMapper = serviceStageMapper;
    this.serviceTodoMapper = serviceTodoMapper;
    this.refundRequestMapper = refundRequestMapper;
    this.agencyTeamMapper = agencyTeamMapper;
    this.agencyOrgMapper = agencyOrgMapper;
    this.agencyMemberProfileMapper = agencyMemberProfileMapper;
    this.userAccountMapper = userAccountMapper;
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
    result.setPaymentUrl("/orders?mockPaymentNo=" + paymentNo);
    result.setMessage("沙箱支付已创建，请点击确认沙箱支付完成闭环。");
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
    serviceStageMapper.startNextStage(caseId, stage.getStageOrder() + 1);
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

  private OrderDtos.OrderDetail buildDetail(OrderDtos.OrderSummary summary) {
    OrderDtos.OrderDetail detail = new OrderDtos.OrderDetail();
    detail.setOrder(summary);
    detail.setStages(summary.getCaseId() == null ? List.of() : serviceStageMapper.listByOrderId(summary.getId()));
    detail.setTodos(summary.getCaseId() == null ? List.of() : serviceTodoMapper.listByOrderId(summary.getId()));
    return detail;
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
