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
import com.example.demo.response.EmotionArticleResponse;
import com.example.demo.service.EmotionArticleService;
import com.example.demo.type.EmotionType;

@Controller
public class EmotionArticleController {
	private final ConcurrentHashMap<Integer, Map<String, Integer>> userSessions;

	private EmotionArticleService emotionArticleService;

	private JwtProvider jwtProvider;

	private SimpMessagingTemplate simpMessagingTemplate;

	public EmotionArticleController(EmotionArticleService emotionArticleService, JwtProvider jwtProvider,
			SimpMessagingTemplate simpMessagingTemplate) {
		this.emotionArticleService = emotionArticleService;
		this.jwtProvider = jwtProvider;
		this.simpMessagingTemplate = simpMessagingTemplate;
		userSessions = new ConcurrentHashMap<>();
	}

	/**
	 * @param type
	 * @param articleId
	 * @param headerAccessor
	 * @throws Exception
	 */
	@MessageMapping("/emotion-article/create/{articleId}")
	public void createEmotionArticle(@Payload String type, @DestinationVariable Integer articleId,
			SimpMessageHeaderAccessor headerAccessor) {
		EmotionType emotionType = EmotionType.valueOf(type);
		Integer accountId = userSessions.get(articleId).get(headerAccessor.getSessionId());
		emotionArticleService.saveEmotionArticle(emotionType, accountId, articleId);
		List<EmotionArticleResponse> responses = emotionArticleService.getListEmotionByArticleId(articleId);
		this.simpMessagingTemplate.convertAndSend("/topic/emotion-article/" + articleId, responses);
	}

	@MessageMapping("/emotion-article/delete/{articleId}")
	public void deleteEmotionArticle(@DestinationVariable Integer articleId, SimpMessageHeaderAccessor headerAccessor) {
		Integer accountId = userSessions.get(articleId).get(headerAccessor.getSessionId());
		emotionArticleService.deleteEmotion(accountId, articleId);
		List<EmotionArticleResponse> responses = emotionArticleService.getListEmotionByArticleId(articleId);
		this.simpMessagingTemplate.convertAndSend("/topic/emotion-article/" + articleId, responses);
	}

	@EventListener
	public void handleWebSocketUnSubcribe(AbstractSubProtocolEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		List<String> destinations = headerAccessor.getNativeHeader("destination");
		if (destinations != null) {
			String destination = destinations.get(0);
			if (destination.startsWith("/topic/emotion-article/")) {
				String[] destinationSplit = destination.split("/");
				Integer articleId = Integer.parseInt(destinationSplit[destinationSplit.length - 1]);

				String senderSession = headerAccessor.getSessionId();
				userSessions.get(articleId).remove(senderSession);

				if (userSessions.get(articleId).size() == 0)
					userSessions.remove(articleId);
			}

		}
	}

	@EventListener
	public void handleWebSocketSubcribe(AbstractSubProtocolEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String destination = headerAccessor.getDestination();
		if (destination != null && destination.startsWith("/topic/emotion-article/")) {
			String senderSession = headerAccessor.getSessionId();
			List<String> tokens = headerAccessor.getNativeHeader("token");
			if (tokens != null) {
				String token = tokens.get(0);
				Integer senderId = jwtProvider.getAccountIdFromJWT(token);

				String[] destinationSplit = destination.split("/");
				Integer articleId = Integer.parseInt(destinationSplit[destinationSplit.length - 1]);

				if (!userSessions.containsKey(articleId)) {
					userSessions.put(articleId, new HashMap<>());
				}
				userSessions.get(articleId).put(senderSession, senderId);
			}
		}
	}

}
