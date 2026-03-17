package com.cateringmarketplace.config;

import com.cateringmarketplace.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

/**
 * WebSocket configuration for real-time chat functionality.
 * Configures STOMP messaging over WebSocket.
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. Raw WebSocket Endpoint
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Allow all for debugging
                .addInterceptors(new JwtHandshakeInterceptor());

        // 2. SockJS Endpoint
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Allow all for debugging
                .addInterceptors(new JwtHandshakeInterceptor())
                .withSockJS();

        // Legacy endpoints for backward compatibility
        // 3. Raw WebSocket Endpoint for chat
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new JwtHandshakeInterceptor());

        // 4. SockJS Endpoint for chat
        registry.addEndpoint("/ws/sockjs/chat")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new JwtHandshakeInterceptor())
                .withSockJS();

        // 5. Raw WebSocket Endpoint for notifications
        registry.addEndpoint("/ws/notifications")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new JwtHandshakeInterceptor());

        // 6. SockJS Endpoint for notifications
        registry.addEndpoint("/ws/sockjs/notifications")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new JwtHandshakeInterceptor())
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = null;

                    // 1. Try to get token from Session Attributes (set by HandshakeInterceptor)
                    Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                    if (sessionAttributes != null && sessionAttributes.containsKey("jwt_token")) {
                        token = (String) sessionAttributes.get("jwt_token");
                    }

                    // 2. If not found, try STOMP headers
                    if (token == null) {
                        List<String> authHeader = accessor.getNativeHeader("Authorization");
                        if (authHeader != null && !authHeader.isEmpty()) {
                            String bearerToken = authHeader.get(0);
                            if (bearerToken.startsWith("Bearer ")) {
                                token = bearerToken.substring(7);
                            }
                        }
                    }

                    // 3. Authenticate
                    if (token != null && jwtUtil.validateToken(token)) {
                        String username = jwtUtil.extractUsername(token);
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        accessor.setUser(auth);
                        log.info("WebSocket Authenticated User: {}", username);
                    } else {
                        log.warn("WebSocket connection attempt without valid token");
                    }
                }
                return message;
            }
        });
    }

    /**
     * Interceptor to handle JWT token from query parameter during WebSocket handshake.
     */
    private class JwtHandshakeInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            log.info("WebSocket Handshake initiated: {}", request.getURI());
            if (request instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                String token = servletRequest.getServletRequest().getParameter("token");
                if (token != null) {
                    attributes.put("jwt_token", token);
                    log.debug("Token found in query param");
                } else {
                    log.debug("No token in query param");
                }
            }
            return true;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Exception exception) {
            if (exception != null) {
                log.error("WebSocket Handshake failed", exception);
            } else {
                log.debug("WebSocket Handshake successful");
            }
        }
    }
}
