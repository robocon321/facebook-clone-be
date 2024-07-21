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
import com.example.demo.response.EmotionPostResponse;
import com.example.demo.service.EmotionPostService;
import com.example.demo.type.EmotionType;

@Controller
public class EmotionPostController {
	private final ConcurrentHashMap<Integer, Map<String, Integer>> userSessions;

	private EmotionPostService emotionPostService;

	private JwtProvider jwtProvider;

	private SimpMessagingTemplate simpMessagingTemplate;

	public EmotionPostController(EmotionPostService emotionPostService, JwtProvider jwtProvider,
			SimpMessagingTemplate simpMessagingTemplate) {
		this.emotionPostService = emotionPostService;
		this.jwtProvider = jwtProvider;
		this.simpMessagingTemplate = simpMessagingTemplate;
		userSessions = new ConcurrentHashMap<>();
	}

	/**
	 * @param type
	 * @param postId
	 * @param headerAccessor
	 * @throws Exception
	 */
	@MessageMapping("/emotion-post/create/{postId}")
	public void createEmotionPost(@Payload String type, @DestinationVariable Integer postId,
			SimpMessageHeaderAccessor headerAccessor) {
		EmotionType emotionType = EmotionType.valueOf(type);
		Integer accountId = userSessions.get(postId).get(headerAccessor.getSessionId());
		emotionPostService.saveEmotionPost(emotionType, accountId, postId);
		List<EmotionPostResponse> responses = emotionPostService.getListEmotionByPostId(postId);
		this.simpMessagingTemplate.convertAndSend("/topic/emotion-post/" + postId, responses);
	}

	@MessageMapping("/emotion-post/delete/{postId}")
	public void deleteEmotionPost(@DestinationVariable Integer postId, SimpMessageHeaderAccessor headerAccessor) {
		Integer accountId = userSessions.get(postId).get(headerAccessor.getSessionId());
		emotionPostService.deleteEmotion(accountId, postId);
		List<EmotionPostResponse> responses = emotionPostService.getListEmotionByPostId(postId);
		this.simpMessagingTemplate.convertAndSend("/topic/emotion-post/" + postId, responses);
	}

	@EventListener
	public void handleWebSocketUnSubcribe(AbstractSubProtocolEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		List<String> destinations = headerAccessor.getNativeHeader("destination");
		if (destinations != null) {
			String destination = destinations.get(0);
			if (destination.startsWith("/topic/emotion-post/")) {
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
		if (destination != null && destination.startsWith("/topic/emotion-post/")) {
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
