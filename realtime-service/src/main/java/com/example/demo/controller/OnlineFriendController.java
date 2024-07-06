package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import com.example.demo.provider.JwtProvider;
import com.example.demo.response.OnlineFriendResponse;
import com.example.demo.service.OnlineFriendService;

@Controller
public class OnlineFriendController {
	@Autowired
	private JwtProvider jwtProvider;

	private final HashMap<String, Integer> userCommentPostSessions = new HashMap<>();

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private OnlineFriendService onlineFriendService;

	@MessageMapping("/online-friend/create")
	public void createOnlineFriend(SimpMessageHeaderAccessor headerAccessor) throws Exception {
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
	public void deleteOnlineFriend(SimpMessageHeaderAccessor headerAccessor) throws Exception {
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
	public void handleWebSocketUnSubcribe(SessionUnsubscribeEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String destination = headerAccessor.getNativeHeader("destination").get(0);
		if (destination.startsWith("/user/topic/online-friend")) {
			String senderSession = headerAccessor.getSessionId();
			userCommentPostSessions.remove(senderSession);
		}
	}

	@EventListener
	public void handleWebSocketSubcribe(SessionSubscribeEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String destination = headerAccessor.getDestination();
		if (destination.startsWith("/user/topic/online-friend")) {
			String senderSession = headerAccessor.getSessionId();
			String token = headerAccessor.getNativeHeader("token").get(0);
			Integer senderId = jwtProvider.getAccountIdFromJWT(token);
			userCommentPostSessions.put(senderSession, senderId);
		}
	}

	@GetMapping("/online-friend")
	@ResponseBody
	public String sayHi() {
		return "Hello world";
	}
}
