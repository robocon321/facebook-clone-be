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

import com.example.demo.request.CommentArticleRequest;
import com.example.demo.response.CommentArticleResponse;
import com.example.demo.service.CommentArticleService;
import com.example.demo.utils.Const;

@Controller
public class CommentArticleController {
	private final HashMap<Integer, Map<String, Integer>> userCommentArticleSessions;

	private Set<Integer> setFocus;

	private SimpMessagingTemplate simpMessagingTemplate;

	private CommentArticleService commentArticleService;

	public CommentArticleController(SimpMessagingTemplate simpMessagingTemplate,
			CommentArticleService commentArticleService) {
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
		String headerUserId = headers.getFirst(Const.X_USER_ID_HEADER);
		if (headerUserId != null) {
			Integer userId = Integer.parseInt(headerUserId);
			commentArticleService.createComment(request, userId);
			List<CommentArticleResponse> commentArticleResponses = commentArticleService
					.getAllCommentByArticle(request.getArticleId());
			simpMessagingTemplate.convertAndSend("/comment-topic/article/create/" + request.getArticleId(),
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
							"/comment-topic/article/check-focus/" + articleId, setFocus.size());
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
			if (destination.startsWith("/user/comment-topic/article/check-focus")) {
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
		if (destination != null && destination.startsWith("/user/comment-topic/article/check-focus")) {
			String senderSession = headerAccessor.getSessionId();
			List<String> headerUserIds = headerAccessor.getNativeHeader(Const.X_USER_ID_HEADER);
			if (headerUserIds != null) {
				String headerUserId = headerUserIds.get(0);
				Integer senderId = Integer.parseInt(headerUserId);

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
