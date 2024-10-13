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

import com.example.demo.response.OnlineFriendResponse;
import com.example.demo.service.OnlineFriendService;
import com.example.demo.utils.Const;

@Controller
public class OnlineFriendController {
	private SimpMessagingTemplate simpMessagingTemplate;

	private OnlineFriendService onlineFriendService;

	private final HashMap<String, Integer> userCommentArticleSessions;

	public OnlineFriendController(SimpMessagingTemplate simpMessagingTemplate,
			OnlineFriendService onlineFriendService) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.onlineFriendService = onlineFriendService;
		userCommentArticleSessions = new HashMap<>();
	}

	@MessageMapping("/online-friend/create")
	public void createOnlineFriend(SimpMessageHeaderAccessor headerAccessor) {
		String senderSession = headerAccessor.getSessionId();
		Integer senderId = userCommentArticleSessions.get(senderSession);
		List<Integer> friends = onlineFriendService.getAllFriends(senderId);
		friends.forEach(item -> {
			for (Entry<String, Integer> entry : userCommentArticleSessions.entrySet()) {
				if (entry.getValue().equals(item)) {
					OnlineFriendResponse response = new OnlineFriendResponse(true, senderId);
					simpMessagingTemplate.convertAndSendToUser(entry.getKey(), "/friend-topic/online-friend", response);
				}
			}
		});
	}

	@MessageMapping("/online-friend/delete")
	public void deleteOnlineFriend(SimpMessageHeaderAccessor headerAccessor) {
		String senderSession = headerAccessor.getSessionId();
		Integer senderId = userCommentArticleSessions.get(senderSession);
		List<Integer> friends = onlineFriendService.getAllFriends(senderId);
		friends.forEach(item -> {
			for (Entry<String, Integer> entry : userCommentArticleSessions.entrySet()) {
				if (entry.getValue().equals(item)) {
					OnlineFriendResponse response = new OnlineFriendResponse(false, senderId);
					simpMessagingTemplate.convertAndSendToUser(entry.getKey(), "/friend-topic/online-friend", response);
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
			if (destination.startsWith("/user/friend-topic/online-friend")) {
				String senderSession = headerAccessor.getSessionId();
				userCommentArticleSessions.remove(senderSession);
			}
		}
	}

	@EventListener
	public void handleWebSocketSubcribe(AbstractSubProtocolEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String destination = headerAccessor.getDestination();
		if (destination != null && destination.startsWith("/user/friend-topic/online-friend")) {
			String senderSession = headerAccessor.getSessionId();
			List<String> headerUserIds = headerAccessor.getNativeHeader(Const.X_USER_ID_HEADER);
			if (headerUserIds != null) {
				String headerUserId = headerUserIds.get(0);
				Integer senderId = Integer.parseInt(headerUserId);
				userCommentArticleSessions.put(senderSession, senderId);
			}
		}
	}
}
