package com.offerbridge.backend.service;

import com.offerbridge.backend.dto.OrderDtos;

import java.util.List;
import java.util.Map;

public interface OrderService {
  OrderDtos.OrderSummary createStudentOrder(Long userId, OrderDtos.CreateOrderRequest request);
  List<OrderDtos.OrderSummary> listStudentOrders(Long userId);
  OrderDtos.OrderDetail getStudentOrderDetail(Long userId, Long orderId);
  OrderDtos.PayResult createPayment(Long userId, Long orderId);
  OrderDtos.OrderDetail mockPaySuccess(Long userId, Long orderId);
  boolean handleAlipayNotify(Map<String, String> params);
  void closeStudentOrder(Long userId, Long orderId);
  OrderDtos.OrderDetail requestRefund(Long userId, Long orderId, OrderDtos.RefundRequest request);
  OrderDtos.OrderDetail confirmStage(Long userId, Long orderId, Long stageId);
  OrderDtos.OrderDetail rejectStage(Long userId, Long orderId, Long stageId, OrderDtos.RejectStageRequest request);
  OrderDtos.OrderDetail completeTodo(Long userId, Long orderId, Long todoId);

  List<OrderDtos.AgentOrderSummary> listAgentOrders(Long userId);
  OrderDtos.OrderDetail getAgentOrderDetail(Long userId, Long orderId);
  OrderDtos.OrderDetail quoteOrder(Long userId, Long orderId, OrderDtos.QuoteOrderRequest request);
  OrderDtos.OrderDetail submitStage(Long userId, Long orderId, Long stageId, OrderDtos.SubmitStageRequest request);
  OrderDtos.OrderDetail createTodo(Long userId, Long orderId, OrderDtos.CreateTodoRequest request);
  OrderDtos.OrderDetail confirmTodo(Long userId, Long orderId, Long todoId);
}
