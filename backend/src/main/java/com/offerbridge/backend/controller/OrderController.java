package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.dto.OrderDtos;
import com.offerbridge.backend.security.AuthContext;
import com.offerbridge.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Validated
public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping("/orders")
  public ApiResponse<OrderDtos.OrderSummary> createOrder(@Valid @RequestBody OrderDtos.CreateOrderRequest request) {
    return ApiResponse.ok(orderService.createStudentOrder(AuthContext.getUserId(), request));
  }

  @GetMapping("/orders")
  public ApiResponse<List<OrderDtos.OrderSummary>> listMyOrders() {
    return ApiResponse.ok(orderService.listStudentOrders(AuthContext.getUserId()));
  }

  @GetMapping("/orders/{orderId}")
  public ApiResponse<OrderDtos.OrderDetail> getMyOrder(@PathVariable Long orderId) {
    return ApiResponse.ok(orderService.getStudentOrderDetail(AuthContext.getUserId(), orderId));
  }

  @PostMapping("/orders/{orderId}/pay")
  public ApiResponse<OrderDtos.PayResult> pay(@PathVariable Long orderId) {
    return ApiResponse.ok(orderService.createPayment(AuthContext.getUserId(), orderId));
  }

  @PostMapping("/orders/{orderId}/pay/mock-success")
  public ApiResponse<OrderDtos.OrderDetail> mockPaySuccess(@PathVariable Long orderId) {
    return ApiResponse.ok(orderService.mockPaySuccess(AuthContext.getUserId(), orderId));
  }

  @PutMapping("/orders/{orderId}/close")
  public ApiResponse<Void> closeOrder(@PathVariable Long orderId) {
    orderService.closeStudentOrder(AuthContext.getUserId(), orderId);
    return ApiResponse.ok();
  }

  @PostMapping("/orders/{orderId}/refund")
  public ApiResponse<OrderDtos.OrderDetail> refund(@PathVariable Long orderId, @Valid @RequestBody OrderDtos.RefundRequest request) {
    return ApiResponse.ok(orderService.requestRefund(AuthContext.getUserId(), orderId, request));
  }

  @PostMapping("/orders/{orderId}/stages/{stageId}/confirm")
  public ApiResponse<OrderDtos.OrderDetail> confirmStage(@PathVariable Long orderId, @PathVariable Long stageId) {
    return ApiResponse.ok(orderService.confirmStage(AuthContext.getUserId(), orderId, stageId));
  }

  @PostMapping("/orders/{orderId}/stages/{stageId}/reject")
  public ApiResponse<OrderDtos.OrderDetail> rejectStage(
    @PathVariable Long orderId,
    @PathVariable Long stageId,
    @Valid @RequestBody OrderDtos.RejectStageRequest request
  ) {
    return ApiResponse.ok(orderService.rejectStage(AuthContext.getUserId(), orderId, stageId, request));
  }

  @PostMapping("/orders/{orderId}/todos/{todoId}/complete")
  public ApiResponse<OrderDtos.OrderDetail> completeTodo(@PathVariable Long orderId, @PathVariable Long todoId) {
    return ApiResponse.ok(orderService.completeTodo(AuthContext.getUserId(), orderId, todoId));
  }

  @GetMapping("/agency/service-orders")
  public ApiResponse<List<OrderDtos.AgentOrderSummary>> listAgentOrders() {
    return ApiResponse.ok(orderService.listAgentOrders(AuthContext.getUserId()));
  }

  @GetMapping("/agency/service-orders/{orderId}")
  public ApiResponse<OrderDtos.OrderDetail> getAgentOrder(@PathVariable Long orderId) {
    return ApiResponse.ok(orderService.getAgentOrderDetail(AuthContext.getUserId(), orderId));
  }

  @PostMapping("/agency/service-orders/{orderId}/quote")
  public ApiResponse<OrderDtos.OrderDetail> quoteOrder(@PathVariable Long orderId, @Valid @RequestBody OrderDtos.QuoteOrderRequest request) {
    return ApiResponse.ok(orderService.quoteOrder(AuthContext.getUserId(), orderId, request));
  }

  @PostMapping("/agency/service-orders/{orderId}/stages/{stageId}/submit")
  public ApiResponse<OrderDtos.OrderDetail> submitStage(
    @PathVariable Long orderId,
    @PathVariable Long stageId,
    @Valid @RequestBody OrderDtos.SubmitStageRequest request
  ) {
    return ApiResponse.ok(orderService.submitStage(AuthContext.getUserId(), orderId, stageId, request));
  }

  @PostMapping("/agency/service-orders/{orderId}/todos")
  public ApiResponse<OrderDtos.OrderDetail> createTodo(@PathVariable Long orderId, @Valid @RequestBody OrderDtos.CreateTodoRequest request) {
    return ApiResponse.ok(orderService.createTodo(AuthContext.getUserId(), orderId, request));
  }

  @PostMapping("/agency/service-orders/{orderId}/todos/{todoId}/confirm")
  public ApiResponse<OrderDtos.OrderDetail> confirmTodo(@PathVariable Long orderId, @PathVariable Long todoId) {
    return ApiResponse.ok(orderService.confirmTodo(AuthContext.getUserId(), orderId, todoId));
  }
}
