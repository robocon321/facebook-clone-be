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

import com.example.demo.response.EmotionArticleResponse;
import com.example.demo.service.EmotionArticleService;
import com.example.demo.type.EmotionType;
import com.example.demo.utils.Const;

@Controller
public class EmotionArticleController {
	private final ConcurrentHashMap<Integer, Map<String, Integer>> userSessions;

	private EmotionArticleService emotionArticleService;

	private SimpMessagingTemplate simpMessagingTemplate;

	public EmotionArticleController(EmotionArticleService emotionArticleService,
			SimpMessagingTemplate simpMessagingTemplate) {
		this.emotionArticleService = emotionArticleService;
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
		this.simpMessagingTemplate.convertAndSend("/article-topic/emotion/" + articleId, responses);
	}

	@MessageMapping("/emotion-article/delete/{articleId}")
	public void deleteEmotionArticle(@DestinationVariable Integer articleId, SimpMessageHeaderAccessor headerAccessor) {
		Integer accountId = userSessions.get(articleId).get(headerAccessor.getSessionId());
		emotionArticleService.deleteEmotion(accountId, articleId);
		List<EmotionArticleResponse> responses = emotionArticleService.getListEmotionByArticleId(articleId);
		this.simpMessagingTemplate.convertAndSend("/article-topic/emotion/" + articleId, responses);
	}

	@EventListener
	public void handleWebSocketUnSubcribe(AbstractSubProtocolEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		List<String> destinations = headerAccessor.getNativeHeader("destination");
		if (destinations != null) {
			String destination = destinations.get(0);
			if (destination.startsWith("/article-topic/emotion/")) {
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
		if (destination != null && destination.startsWith("/article-topic/emotion/")) {
			String senderSession = headerAccessor.getSessionId();
			List<String> headerUserIds = headerAccessor.getNativeHeader(Const.X_USER_ID_HEADER);
			if (headerUserIds != null) {
				String headerUserId = headerUserIds.get(0);
				Integer senderId = Integer.parseInt(headerUserId);

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
