package com.offerbridge.backend.controller;

import com.offerbridge.backend.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments/alipay")
public class AlipayNotifyController {
  private final OrderService orderService;

  public AlipayNotifyController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping("/notify")
  public String notify(HttpServletRequest request) {
    Map<String, String> params = new HashMap<>();
    request.getParameterMap().forEach((key, values) -> {
      if (values != null && values.length > 0) {
        params.put(key, values[0]);
      }
    });
    return orderService.handleAlipayNotify(params) ? "success" : "failure";
  }
}
