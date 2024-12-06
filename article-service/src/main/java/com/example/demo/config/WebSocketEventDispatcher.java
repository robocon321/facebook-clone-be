package com.example.demo.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
public class WebSocketEventDispatcher {
    private List<WebSocketSubscriber> subscribers = new ArrayList<>();
    private final UserSessionManager userSessionManager;

    public WebSocketEventDispatcher(UserSessionManager userSessionManager) {
        this.userSessionManager = userSessionManager;
    }

    @EventListener
    public void handleWebSocketSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String[] suffix = getSuffix(headerAccessor);
        String sessionId = getSessionId(headerAccessor);

        if (destination != null) {
            subscribers.stream()
                    .filter(item -> destination.startsWith(item.getDestination()))
                    .forEach(item -> item.handleWebSocketSubcribe(suffix, sessionId));
        }
    }

    @EventListener
    public void handleWebSocketUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String[] suffix = getSuffix(headerAccessor);
        String sessionId = getSessionId(headerAccessor);

        if (destination != null) {
            subscribers.stream()
                    .filter(item -> destination.startsWith(item.getDestination()))
                    .forEach(item -> item.handleWebSocketUnSubcribe(suffix, sessionId));
        }
    }

    @SuppressWarnings("null")
    private String[] getSuffix(StompHeaderAccessor headerAccessor) {
        String destination = headerAccessor.getDestination();
        return destination.split("/");
    }

    private String getSessionId(StompHeaderAccessor headerAccessor) {
        return headerAccessor.getSessionId();
    }

    public void add(WebSocketSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();
        if (attributes != null && attributes.containsKey("userId")) {
            Integer userId = Integer.parseInt(attributes.get("userId").toString());
            userSessionManager.addSession(sessionId, userId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        userSessionManager.removeSession(sessionId);
    }
}
