package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;

import com.example.demo.provider.JwtProvider;
import com.example.demo.response.OnlineFriendResponse;
import com.example.demo.service.OnlineFriendService;

@Controller
public class OnlineFriendController {
	private JwtProvider jwtProvider;

	private SimpMessagingTemplate simpMessagingTemplate;

	private OnlineFriendService onlineFriendService;

	private final HashMap<String, Integer> userCommentPostSessions;

	public OnlineFriendController(JwtProvider jwtProvider, SimpMessagingTemplate simpMessagingTemplate,
			OnlineFriendService onlineFriendService) {
		this.jwtProvider = jwtProvider;
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.onlineFriendService = onlineFriendService;
		userCommentPostSessions = new HashMap<>();
	}

	@MessageMapping("/online-friend/create")
	public void createOnlineFriend(SimpMessageHeaderAccessor headerAccessor) {
		String senderSession = headerAccessor.getSessionId();
		Integer senderId = userCommentPostSessions.get(senderSession);
		List<Integer> friends = onlineFriendService.getAllFriends(senderId);
		friends.forEach(item -> {
			for (Entry<String, Integer> entry : userCommentPostSessions.entrySet()) {
				if (entry.getValue().equals(item)) {
					OnlineFriendResponse response = new OnlineFriendResponse(true, senderId);
					simpMessagingTemplate.convertAndSendToUser(entry.getKey(), "/topic/online-friend", response);
				}
			}
		});
	}

	@MessageMapping("/online-friend/delete")
	public void deleteOnlineFriend(SimpMessageHeaderAccessor headerAccessor) {
		String senderSession = headerAccessor.getSessionId();
		Integer senderId = userCommentPostSessions.get(senderSession);
		List<Integer> friends = onlineFriendService.getAllFriends(senderId);
		friends.forEach(item -> {
			for (Entry<String, Integer> entry : userCommentPostSessions.entrySet()) {
				if (entry.getValue().equals(item)) {
					OnlineFriendResponse response = new OnlineFriendResponse(false, senderId);
					simpMessagingTemplate.convertAndSendToUser(entry.getKey(), "/topic/online-friend", response);
				}
			}
		});
	}

	@EventListener
	public void handleWebSocketUnSubcribe(AbstractSubProtocolEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		List<String> destinations = headerAccessor.getNativeHeader("destination");
		if (destinations != null) {
			String destination = destinations.get(0);
			if (destination.startsWith("/user/topic/online-friend")) {
				String senderSession = headerAccessor.getSessionId();
				userCommentPostSessions.remove(senderSession);
			}
		}
	}

	@EventListener
	public void handleWebSocketSubcribe(AbstractSubProtocolEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String destination = headerAccessor.getDestination();
		if (destination != null && destination.startsWith("/user/topic/online-friend")) {
			String senderSession = headerAccessor.getSessionId();
			List<String> tokens = headerAccessor.getNativeHeader("token");
			if (tokens != null) {
				String token = tokens.get(0);
				Integer senderId = jwtProvider.getAccountIdFromJWT(token);
				userCommentPostSessions.put(senderSession, senderId);
			}
		}
	}
}
