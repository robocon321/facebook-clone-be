package com.example.demo.controller;

import java.util.List;
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
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.demo.provider.JwtProvider;
import com.example.demo.response.EmotionPostResponse;
import com.example.demo.service.EmotionPostService;
import com.example.demo.type.EmotionType;

@Controller
public class EmotionPostController {
    private final ConcurrentHashMap<String, String> userSessions = new ConcurrentHashMap<>();
    
    @Autowired
    private EmotionPostService emotionPostService;
    
	@Autowired
	private JwtProvider jwtProvider;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @MessageMapping("/emotion-post/create/{postId}")
    public void createEmotion(@Payload String type, @DestinationVariable Integer postId, SimpMessageHeaderAccessor headerAccessor) throws Exception {    	
    	EmotionType emotionType = EmotionType.valueOf(type);
		Integer accountId = jwtProvider.getAccountIdFromJWT(userSessions.get(headerAccessor.getSessionId()));
    	emotionPostService.saveEmotionPost(emotionType, accountId, postId);
    	List<EmotionPostResponse> responses = emotionPostService.getListEmotionByPostId(postId);
    	this.simpMessagingTemplate.convertAndSend("/topic/emotion-post/"+postId, responses);
    }
    
    @MessageMapping("/emotion-post/delete/{postId}")
    public void deleteEmotion(@DestinationVariable Integer postId, SimpMessageHeaderAccessor headerAccessor) throws Exception {
		Integer accountId = jwtProvider.getAccountIdFromJWT(userSessions.get(headerAccessor.getSessionId()));
		emotionPostService.deleteEmotion(accountId, postId);
    	List<EmotionPostResponse> responses = emotionPostService.getListEmotionByPostId(postId);
    	this.simpMessagingTemplate.convertAndSend("/topic/emotion-post/"+postId, responses);		
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
