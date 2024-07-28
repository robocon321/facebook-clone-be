package com.example.demo.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;

import com.example.demo.provider.JwtProvider;
import com.example.demo.request.CommentArticleRequest;
import com.example.demo.response.CommentArticleResponse;
import com.example.demo.service.CommentArticleService;

@Controller
public class CommentArticleController {
	private JwtProvider jwtProvider;

	private final HashMap<Integer, Map<String, Integer>> userCommentArticleSessions;

	private Set<Integer> setFocus;

	private SimpMessagingTemplate simpMessagingTemplate;

	private CommentArticleService commentArticleService;

	public CommentArticleController(JwtProvider jwtProvider, SimpMessagingTemplate simpMessagingTemplate,
			CommentArticleService commentArticleService) {
		this.jwtProvider = jwtProvider;
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.commentArticleService = commentArticleService;
		userCommentArticleSessions = new HashMap<>();
		setFocus = new HashSet<>();
	}

	@GetMapping("/comment-article/create")
	@ResponseBody
	public String sayHi() {
		return "Hello world";
	}

	@PostMapping("/comment-article/create")
	public void createComment(@ModelAttribute CommentArticleRequest request, @RequestHeader HttpHeaders headers) {
		String authorizationHeader = headers.getFirst("Authorization");
		if (authorizationHeader != null) {
			String token = authorizationHeader.substring(7);
			Integer accountId = jwtProvider.getAccountIdFromJWT(token);
			commentArticleService.createComment(request, accountId);
			List<CommentArticleResponse> commentArticleResponses = commentArticleService
					.getAllCommentByArticle(request.getArticleId());
			simpMessagingTemplate.convertAndSend("/topic/comment-article/create/" + request.getArticleId(),
					commentArticleResponses);
		}
	}

	@MessageMapping("/comment-article/check-focus/{articleId}")
	public void focusComment(@Payload boolean isFocusing, @DestinationVariable Integer articleId,
			SimpMessageHeaderAccessor headerAccessor) {
		Principal user = headerAccessor.getUser();
		if (user != null) {
			String senderSession = user.getName();
			Integer senderId = userCommentArticleSessions.get(articleId).get(senderSession);
			if (isFocusing) {
				setFocus.add(senderId);
			} else {
				setFocus.remove(senderId);
			}
			for (Entry<String, Integer> entry : userCommentArticleSessions.get(articleId).entrySet()) {
				if (!entry.getKey().equals(senderSession)) {
					this.simpMessagingTemplate.convertAndSendToUser(entry.getKey(),
							"/topic/comment-article/check-focus/" + articleId, setFocus.size());
				}
			}

		}
	}

	@EventListener
	public void handleWebSocketUnSubcribe(AbstractSubProtocolEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		List<String> destinations = headerAccessor.getNativeHeader("destination");
		if (destinations != null) {
			String destination = destinations.get(0);
			if (destination.startsWith("/user/topic/comment-article/check-focus")) {
				String[] destinationSplit = destination.split("/");
				Integer articleId = Integer.parseInt(destinationSplit[destinationSplit.length - 1]);

				String senderSession = headerAccessor.getSessionId();
				userCommentArticleSessions.get(articleId).remove(senderSession);

				if (userCommentArticleSessions.get(articleId).size() == 0)
					userCommentArticleSessions.remove(articleId);
			}
		}
	}

	@EventListener
	public void handleWebSocketSubcribe(AbstractSubProtocolEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String destination = headerAccessor.getDestination();
		if (destination != null && destination.startsWith("/user/topic/comment-article/check-focus")) {
			String senderSession = headerAccessor.getSessionId();
			List<String> tokens = headerAccessor.getNativeHeader("token");
			if (tokens != null) {
				String token = tokens.get(0);
				Integer senderId = jwtProvider.getAccountIdFromJWT(token);

				String[] destinationSplit = destination.split("/");
				Integer articleId = Integer.parseInt(destinationSplit[destinationSplit.length - 1]);

				if (!userCommentArticleSessions.containsKey(articleId)) {
					userCommentArticleSessions.put(articleId, new HashMap<>());
				}
				userCommentArticleSessions.get(articleId).put(senderSession, senderId);
			}
		}
	}
}
