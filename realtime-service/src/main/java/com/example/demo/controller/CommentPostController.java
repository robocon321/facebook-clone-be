package com.example.demo.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import com.example.demo.provider.JwtProvider;
import com.example.demo.request.CommentPostRequest;
import com.example.demo.response.CommentPostResponse;
import com.example.demo.service.CommentPostService;

@Controller
public class CommentPostController {
	@Autowired
	private JwtProvider jwtProvider;

	private final HashMap<Integer, Map<String, Integer>> userCommentPostSessions = new HashMap<>();

	private Set<Integer> setFocus = new HashSet<>();

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private CommentPostService commentPostService;

	@GetMapping("/comment-post/create")
	@ResponseBody
	public String sayHi() {
		return "Hello world";
	}

	@PostMapping("/comment-post/create")
	public void createComment(@ModelAttribute CommentPostRequest request, @RequestHeader HttpHeaders headers)
			throws Exception {
		String authorizationHeader = headers.getFirst("Authorization");
		String token = authorizationHeader.substring(7);
		Integer accountId = jwtProvider.getAccountIdFromJWT(token);
		commentPostService.createComment(request, accountId);
		List<CommentPostResponse> commentPostResponses = commentPostService.getAllCommentByPost(request.getPostId());
		simpMessagingTemplate.convertAndSend("/topic/comment-post/create/" + request.getPostId(), commentPostResponses);
	}

	@MessageMapping("/comment-post/check-focus/{postId}")
	public void focusComment(@Payload Boolean isFocusing, @DestinationVariable Integer postId,
			SimpMessageHeaderAccessor headerAccessor) throws Exception {
		String senderSession = headerAccessor.getUser().getName();
		Integer senderId = userCommentPostSessions.get(postId).get(senderSession);
		if (isFocusing) {
			setFocus.add(senderId);
		} else {
			setFocus.remove(senderId);
		}
		for (Entry<String, Integer> entry : userCommentPostSessions.get(postId).entrySet()) {
			if (!entry.getKey().equals(senderSession)) {
				this.simpMessagingTemplate.convertAndSendToUser(entry.getKey(),
						"/topic/comment-post/check-focus/" + postId, setFocus.size());
			}
		}
	}

	@EventListener
	public void handleWebSocketUnSubcribe(SessionUnsubscribeEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String destination = headerAccessor.getNativeHeader("destination").get(0);
		if (destination.startsWith("/user/topic/comment-post/check-focus")) {
			String[] destinationSplit = destination.split("/");
			Integer postId = Integer.parseInt(destinationSplit[destinationSplit.length - 1]);

			String senderSession = headerAccessor.getSessionId();
			userCommentPostSessions.get(postId).remove(senderSession);

			if (userCommentPostSessions.get(postId).size() == 0)
				userCommentPostSessions.remove(postId);
		}
	}

	@EventListener
	public void handleWebSocketSubcribe(SessionSubscribeEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String destination = headerAccessor.getDestination();
		if (destination.startsWith("/user/topic/comment-post/check-focus")) {
			String senderSession = headerAccessor.getSessionId();
			String token = headerAccessor.getNativeHeader("token").get(0);
			Integer senderId = jwtProvider.getAccountIdFromJWT(token);

			String[] destinationSplit = destination.split("/");
			Integer postId = Integer.parseInt(destinationSplit[destinationSplit.length - 1]);

			if (!userCommentPostSessions.containsKey(postId)) {
				userCommentPostSessions.put(postId, new HashMap<>());
			}
			userCommentPostSessions.get(postId).put(senderSession, senderId);
		}
	}
}
