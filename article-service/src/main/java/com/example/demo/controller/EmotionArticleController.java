package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
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
import com.example.demo.response.EmotionArticleResponse;
import com.example.demo.service.EmotionArticleService;
import com.example.demo.type.EmotionType;

@Controller
public class EmotionArticleController implements WebSocketSubscriber {
	private final ConcurrentHashMap<Integer, List<Integer>> userSessions;

	private EmotionArticleService emotionArticleService;

	private SimpMessagingTemplate simpMessagingTemplate;

	private UserSessionManager userSessionManager;

	public EmotionArticleController(
			EmotionArticleService emotionArticleService,
			UserSessionManager userSessionManager,
			SimpMessagingTemplate simpMessagingTemplate,
			WebSocketEventDispatcher dispatcher) {
		this.emotionArticleService = emotionArticleService;
		this.userSessionManager = userSessionManager;
		this.simpMessagingTemplate = simpMessagingTemplate;
		userSessions = new ConcurrentHashMap<>();
		dispatcher.add(this);
	}

	@MessageMapping("/emotion/create/{articleId}")
	public void createEmotionArticle(@Payload String type, @DestinationVariable Integer articleId,
			SimpMessageHeaderAccessor headerAccessor) {
		EmotionType emotionType = EmotionType.valueOf(type);
		String sessionId = headerAccessor.getSessionId();
		if (userSessions.containsKey(articleId) && userSessionManager.hasSession(sessionId)) {
			Integer userId = userSessionManager.getUserId(sessionId);
			emotionArticleService.saveEmotionArticle(emotionType, userId, articleId);
			List<EmotionArticleResponse> responses = emotionArticleService.getListEmotionByArticleId(articleId);
			this.simpMessagingTemplate.convertAndSend("/article-topic/emotion/" + articleId, responses);
		}
	}

	@MessageMapping("/emotion/delete/{articleId}")
	public void deleteEmotionArticle(@DestinationVariable Integer articleId, SimpMessageHeaderAccessor headerAccessor) {
		String sessionId = headerAccessor.getSessionId();
		if (userSessions.containsKey(articleId) && userSessionManager.hasSession(sessionId)) {
			Integer userId = userSessionManager.getUserId(sessionId);
			emotionArticleService.deleteEmotion(userId, articleId);
			List<EmotionArticleResponse> responses = emotionArticleService.getListEmotionByArticleId(articleId);
			this.simpMessagingTemplate.convertAndSend("/article-topic/emotion/" + articleId, responses);
		}
	}

	@Override
	public String getDestination() {
		return "/article-topic/emotion/";
	}

	@Override
	public void handleWebSocketSubcribe(String[] suffix, String sessionId) {
		if (suffix == null || suffix.length == 0 || sessionId == null)
			return;

		Integer articleId = Integer.parseInt(suffix[suffix.length - 1]);
		if (!userSessions.containsKey(articleId)) {
			userSessions.put(articleId, new ArrayList<>());
		}

		if (userSessionManager.hasSession(sessionId)) {
			userSessions.get(articleId).add(userSessionManager.getUserId(sessionId));
		}
	}

	@Override
	public void handleWebSocketUnSubcribe(String[] suffix, String sessionId) {
		if (suffix == null || suffix.length == 0 || sessionId == null)
			return;

		Integer articleId = Integer.parseInt(suffix[suffix.length - 1]);

		userSessions.get(articleId).remove(userSessionManager.getUserId(sessionId));

		if (userSessions.get(articleId).size() == 0)
			userSessions.remove(articleId);
	}

}
