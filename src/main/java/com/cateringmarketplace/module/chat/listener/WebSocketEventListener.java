package com.cateringmarketplace.module.chat.listener;

import com.cateringmarketplace.module.auth.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = headerAccessor.getUser();

        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
            if (auth.getPrincipal() instanceof CustomUserDetails) {
                String userId = ((CustomUserDetails) auth.getPrincipal()).getUserId();
                log.info("User Connected: {}", userId);

                // Broadcast Online Status
                messagingTemplate.convertAndSend("/topic/public.users", Map.of(
                        "userId", userId,
                        "status", "ONLINE"
                ));
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = headerAccessor.getUser();

        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
            if (auth.getPrincipal() instanceof CustomUserDetails) {
                String userId = ((CustomUserDetails) auth.getPrincipal()).getUserId();
                log.info("User Disconnected: {}", userId);

                // Broadcast Offline Status
                messagingTemplate.convertAndSend("/topic/public.users", Map.of(
                        "userId", userId,
                        "status", "OFFLINE"
                ));
            }
        }
    }
}
