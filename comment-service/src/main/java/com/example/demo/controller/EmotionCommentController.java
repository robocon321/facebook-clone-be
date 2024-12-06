package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.demo.config.UserSessionManager;
import com.example.demo.config.WebSocketEventDispatcher;
import com.example.demo.config.WebSocketSubscriber;
import com.example.demo.request.EmotionCommentRequest;
import com.example.demo.response.EmotionCommentResponse;
import com.example.demo.service.EmotionCommentService;
import com.example.demo.type.EmotionType;

@Controller
public class EmotionCommentController implements WebSocketSubscriber {
	private final ConcurrentHashMap<Integer, List<String>> userSessions;

	private EmotionCommentService emotionCommentService;

	private SimpMessagingTemplate simpMessagingTemplate;

	private UserSessionManager userSessionManager;

	public EmotionCommentController(
			EmotionCommentService emotionCommentService,
			SimpMessagingTemplate simpMessagingTemplate,
			UserSessionManager userSessionManager,
			WebSocketEventDispatcher dispatcher) {
		this.emotionCommentService = emotionCommentService;
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.userSessionManager = userSessionManager;
		userSessions = new ConcurrentHashMap<>();
		dispatcher.add(this);
	}

	@MessageMapping("/emotion/create/{articleId}")
	public void createEmotionComment(@Payload EmotionCommentRequest request, @DestinationVariable Integer articleId,
			SimpMessageHeaderAccessor headerAccessor) {
		EmotionType emotionType = EmotionType.valueOf(request.getType());
		Integer accountId = userSessionManager.getUserId(headerAccessor.getSessionId());
		emotionCommentService.saveEmotionComment(emotionType, accountId, request.getCommentId());
		List<EmotionCommentResponse> emotions = emotionCommentService.getListEmotionByCommentId(request.getCommentId());
		Map<String, Object> response = new HashMap<>();
		response.put("commentId", request.getCommentId());
		response.put("data", emotions);

		this.simpMessagingTemplate.convertAndSend("/comment-topic/emotion/" + articleId, response);
	}

	@MessageMapping("/emotion/delete/{articleId}")
	public void deleteEmotionComment(@Payload String commentIdStr, @DestinationVariable Integer articleId,
			SimpMessageHeaderAccessor headerAccessor) {
		Integer commentId = Integer.parseInt(commentIdStr);
		Integer accountId = userSessionManager.getUserId(headerAccessor.getSessionId());
		emotionCommentService.deleteEmotion(accountId, commentId);
		List<EmotionCommentResponse> emotions = emotionCommentService.getListEmotionByCommentId(commentId);
		Map<String, Object> response = new HashMap<>();
		response.put("commentId", commentId);
		response.put("data", emotions);
		this.simpMessagingTemplate.convertAndSend("/comment-topic/emotion/" + articleId, response);
	}

	@Override
	public String getDestination() {
		return "/comment-topic/emotion/";
	}

	@Override
	public void handleWebSocketSubcribe(String[] suffix, String sessionId) {
		if (suffix == null || suffix.length == 0 || sessionId == null)
			return;
		Integer articleId = Integer.parseInt(suffix[suffix.length - 1]);
		if (!userSessions.contains(articleId)) {
			userSessions.put(articleId, new ArrayList<>());
		}
		if (userSessionManager.hasSession(sessionId)) {
			userSessions.get(articleId).add(sessionId);
		}
	}

	@Override
	public void handleWebSocketUnSubcribe(String[] suffix, String sessionId) {
		if (suffix == null || suffix.length == 0 || sessionId == null)
			return;
		Integer articleId = Integer.parseInt(suffix[suffix.length - 1]);

		userSessions.get(articleId).remove(sessionId);

		if (userSessions.get(articleId).size() == 0)
			userSessions.remove(articleId);

	}

}
