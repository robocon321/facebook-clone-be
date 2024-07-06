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
import com.example.demo.response.EmotionPostResponse;
import com.example.demo.service.EmotionPostService;
import com.example.demo.type.EmotionType;

@Controller
public class EmotionPostController {
	private final ConcurrentHashMap<Integer, Map<String, Integer>> userSessions = new ConcurrentHashMap<>();

	@Autowired
	private EmotionPostService emotionPostService;

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@MessageMapping("/emotion-post/create/{postId}")
	public void createEmotionPost(@Payload String type, @DestinationVariable Integer postId,
			SimpMessageHeaderAccessor headerAccessor) throws Exception {
		EmotionType emotionType = EmotionType.valueOf(type);
		Integer accountId = userSessions.get(postId).get(headerAccessor.getSessionId());
		emotionPostService.saveEmotionPost(emotionType, accountId, postId);
		List<EmotionPostResponse> responses = emotionPostService.getListEmotionByPostId(postId);
		this.simpMessagingTemplate.convertAndSend("/topic/emotion-post/" + postId, responses);
	}

	@MessageMapping("/emotion-post/delete/{postId}")
	public void deleteEmotionPost(@DestinationVariable Integer postId, SimpMessageHeaderAccessor headerAccessor)
			throws Exception {
		Integer accountId = userSessions.get(postId).get(headerAccessor.getSessionId());
		emotionPostService.deleteEmotion(accountId, postId);
		List<EmotionPostResponse> responses = emotionPostService.getListEmotionByPostId(postId);
		this.simpMessagingTemplate.convertAndSend("/topic/emotion-post/" + postId, responses);
	}

	@EventListener
	public void handleWebSocketUnSubcribe(SessionUnsubscribeEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String destination = headerAccessor.getNativeHeader("destination").get(0);
		if (destination.startsWith("/topic/emotion-post/")) {
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
		if (destination.startsWith("/topic/emotion-post/")) {
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
