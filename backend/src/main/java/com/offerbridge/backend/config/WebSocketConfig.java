package com.offerbridge.backend.config;

import com.offerbridge.backend.security.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  private final JwtService jwtService;

  public WebSocketConfig(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic", "/queue");
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
      .setAllowedOriginPatterns("*")
      .addInterceptors(new JwtHandshakeInterceptor(jwtService))
      .setHandshakeHandler(new UserHandshakeHandler());
  }

  private static class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtService jwtService;

    JwtHandshakeInterceptor(JwtService jwtService) {
      this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
      String token = null;
      if (request instanceof ServletServerHttpRequest servletRequest) {
        token = servletRequest.getServletRequest().getParameter("token");
      }
      if (token == null || token.isBlank()) {
        return false;
      }
      try {
        Long userId = jwtService.parseUserId(token);
        attributes.put("userId", String.valueOf(userId));
        return true;
      } catch (Exception ex) {
        return false;
      }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
  }

  private static class UserHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
      String userId = String.valueOf(attributes.get("userId"));
      return () -> userId;
    }
  }
}
