package com.example.demo.controller;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.demo.request.CommentPostRequest;

@Controller
public class CommentPostController {
    private final ConcurrentHashMap<String, String> userSessions = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @MessageMapping("/comment-post/{postId}")
    public void send(@Payload String message, @DestinationVariable Integer postId, SimpMessageHeaderAccessor headerAccessor) throws Exception {    	
    	this.simpMessagingTemplate.convertAndSend("/topic/comment-post/"+postId, message + " " + headerAccessor.getSessionId());
    }
    
    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String session = headerAccessor.getSessionId();
        userSessions.remove(session);
    }

    @EventListener
    public void handleWebSocketConnect(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String session = headerAccessor.getSessionId();
        String token = headerAccessor.getNativeHeader("token").get(0);
        userSessions.put(session, token);
    }
}
