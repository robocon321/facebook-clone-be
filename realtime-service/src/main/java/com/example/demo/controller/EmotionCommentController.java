package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import com.example.demo.provider.JwtProvider;
import com.example.demo.request.EmotionCommentRequest;
import com.example.demo.response.EmotionCommentResponse;
import com.example.demo.service.EmotionCommentService;
import com.example.demo.type.EmotionType;

@Controller
public class EmotionCommentController {
    private final ConcurrentHashMap<Integer, Map<String, Integer>> userSessions = new ConcurrentHashMap<>();
    
    @Autowired
    private EmotionCommentService emotionCommentService;
    
	@Autowired
	private JwtProvider jwtProvider;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
        
    @MessageMapping("/emotion-comment/create/{postId}")
    public void createEmotionComment(@Payload EmotionCommentRequest request, @DestinationVariable Integer postId, SimpMessageHeaderAccessor headerAccessor) throws Exception {    	
    	EmotionType emotionType = EmotionType.valueOf(request.getType());
		Integer accountId = userSessions.get(postId).get(headerAccessor.getSessionId());		
		emotionCommentService.saveEmotionComment(emotionType, accountId, request.getCommentId());

		List<EmotionCommentResponse> emotions = emotionCommentService.getListEmotionByCommentId(request.getCommentId());
    	Map<String, Object> response = new HashMap<>();
    	response.put("commentId", request.getCommentId());
    	response.put("data", emotions);

    	this.simpMessagingTemplate.convertAndSend("/topic/emotion-comment/"+postId, response);
    }
    
    @MessageMapping("/emotion-comment/delete/{postId}")
    public void deleteEmotionCommment(@Payload String commentIdStr, @DestinationVariable Integer postId, SimpMessageHeaderAccessor headerAccessor) throws Exception {
    	Integer commentId = Integer.parseInt(commentIdStr);
    	Integer accountId = userSessions.get(postId).get(headerAccessor.getSessionId());
		emotionCommentService.deleteEmotion(accountId, commentId);
    	List<EmotionCommentResponse> emotions = emotionCommentService.getListEmotionByCommentId(commentId);
    	Map<String, Object> response = new HashMap<>();
    	response.put("commentId", commentId);
    	response.put("data", emotions);
    	this.simpMessagingTemplate.convertAndSend("/topic/emotion-comment/"+postId, response);
    }

	@EventListener
	public void handleWebSocketUnSubcribe(SessionUnsubscribeEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String destination = headerAccessor.getNativeHeader("destination").get(0);
		if (destination.startsWith("/topic/emotion-comment/")) {
			String[] destinationSplit = destination.split("/");
			Integer postId = Integer.parseInt(destinationSplit[destinationSplit.length - 1]);

			String senderSession = headerAccessor.getSessionId();
			userSessions.get(postId).remove(senderSession);

			if (userSessions.get(postId).size() == 0)
				userSessions.remove(postId);
		}
	}

	@EventListener
	public void handleWebSocketSubcribe(SessionSubscribeEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String destination = headerAccessor.getDestination();
		if (destination.startsWith("/topic/emotion-comment/")) {
			String senderSession = headerAccessor.getSessionId();
			String token = headerAccessor.getNativeHeader("token").get(0);
			Integer senderId = jwtProvider.getAccountIdFromJWT(token);

			String[] destinationSplit = destination.split("/");
			Integer postId = Integer.parseInt(destinationSplit[destinationSplit.length - 1]);

			if (!userSessions.containsKey(postId)) {
				userSessions.put(postId, new HashMap<>());
			}
			userSessions.get(postId).put(senderSession, senderId);
		}
	}

}
