package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;

import com.example.demo.provider.JwtProvider;
import com.example.demo.request.EmotionCommentRequest;
import com.example.demo.response.EmotionCommentResponse;
import com.example.demo.service.EmotionCommentService;
import com.example.demo.type.EmotionType;

@Controller
public class EmotionCommentController {
	private final ConcurrentHashMap<Integer, Map<String, Integer>> userSessions;

	private EmotionCommentService emotionCommentService;

	private JwtProvider jwtProvider;

	private SimpMessagingTemplate simpMessagingTemplate;

	public EmotionCommentController(EmotionCommentService emotionCommentService, JwtProvider jwtProvider,
			SimpMessagingTemplate simpMessagingTemplate) {
		this.emotionCommentService = emotionCommentService;
		this.jwtProvider = jwtProvider;
		this.simpMessagingTemplate = simpMessagingTemplate;
		userSessions = new ConcurrentHashMap<>();
	}

	@MessageMapping("/emotion-comment/create/{postId}")
	public void createEmotionComment(@Payload EmotionCommentRequest request, @DestinationVariable Integer postId,
			SimpMessageHeaderAccessor headerAccessor) {
		EmotionType emotionType = EmotionType.valueOf(request.getType());
		Integer accountId = userSessions.get(postId).get(headerAccessor.getSessionId());
		emotionCommentService.saveEmotionComment(emotionType, accountId, request.getCommentId());

		List<EmotionCommentResponse> emotions = emotionCommentService.getListEmotionByCommentId(request.getCommentId());
		Map<String, Object> response = new HashMap<>();
		response.put("commentId", request.getCommentId());
		response.put("data", emotions);

		this.simpMessagingTemplate.convertAndSend("/topic/emotion-comment/" + postId, response);
	}

	@MessageMapping("/emotion-comment/delete/{postId}")
	public void deleteEmotionCommment(@Payload String commentIdStr, @DestinationVariable Integer postId,
			SimpMessageHeaderAccessor headerAccessor) {
		Integer commentId = Integer.parseInt(commentIdStr);
		Integer accountId = userSessions.get(postId).get(headerAccessor.getSessionId());
		emotionCommentService.deleteEmotion(accountId, commentId);
		List<EmotionCommentResponse> emotions = emotionCommentService.getListEmotionByCommentId(commentId);
		Map<String, Object> response = new HashMap<>();
		response.put("commentId", commentId);
		response.put("data", emotions);
		this.simpMessagingTemplate.convertAndSend("/topic/emotion-comment/" + postId, response);
	}

	@EventListener
	public void handleWebSocketUnSubcribe(AbstractSubProtocolEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		List<String> destinations = headerAccessor.getNativeHeader("destination");
		if (destinations != null) {
			String destination = destinations.get(0);
			if (destination.startsWith("/topic/emotion-comment/")) {
				String[] destinationSplit = destination.split("/");
				Integer postId = Integer.parseInt(destinationSplit[destinationSplit.length - 1]);

				String senderSession = headerAccessor.getSessionId();
				userSessions.get(postId).remove(senderSession);

				if (userSessions.get(postId).size() == 0)
					userSessions.remove(postId);
			}
		}
	}

	@EventListener
	public void handleWebSocketSubcribe(AbstractSubProtocolEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String destination = headerAccessor.getDestination();
		if (destination != null && destination.startsWith("/topic/emotion-comment/")) {
			String senderSession = headerAccessor.getSessionId();
			List<String> tokens = headerAccessor.getNativeHeader("token");
			if (tokens != null) {
				String token = tokens.get(0);
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

}
