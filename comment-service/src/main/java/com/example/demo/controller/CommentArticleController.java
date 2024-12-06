package com.example.demo.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.config.UserSessionManager;
import com.example.demo.config.WebSocketEventDispatcher;
import com.example.demo.config.WebSocketSubscriber;
import com.example.demo.request.CommentArticleRequest;
import com.example.demo.response.CommentArticleResponse;
import com.example.demo.service.CommentArticleService;
import com.example.demo.utils.Const;

@Controller
@RequestMapping("/api/v1/comment")
public class CommentArticleController implements WebSocketSubscriber {
	// ConcurrentHashMap<articleId, List<userSessionId>>
	private final ConcurrentHashMap<Integer, List<String>> userCommentArticleSessions;

	private Set<Integer> focuses;

	private SimpMessagingTemplate simpMessagingTemplate;

	private CommentArticleService commentArticleService;

	private UserSessionManager userSessionManager;

	public CommentArticleController(
			SimpMessagingTemplate simpMessagingTemplate,
			CommentArticleService commentArticleService,
			UserSessionManager userSessionManager,
			WebSocketEventDispatcher dispatcher) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.commentArticleService = commentArticleService;
		this.userSessionManager = userSessionManager;
		this.userCommentArticleSessions = new ConcurrentHashMap<>();
		focuses = new HashSet<>();
		dispatcher.add(this);
	}

	@GetMapping
	@ResponseBody
	public String sayHi() {
		return "Hello world";
	}

	@PostMapping
	public ResponseEntity<BodyBuilder> createComment(@ModelAttribute CommentArticleRequest request,
			@RequestHeader HttpHeaders headers) {
		String headerUserId = headers.getFirst(Const.X_USER_ID_HEADER);
		if (headerUserId != null) {
			Integer userId = Integer.parseInt(headerUserId);
			commentArticleService.createComment(request, userId);
			List<CommentArticleResponse> commentArticleResponses = commentArticleService
					.getAllCommentByArticle(request.getArticleId());
			simpMessagingTemplate.convertAndSend("/comment-topic/article/create/" + request.getArticleId(),
					commentArticleResponses);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.badRequest().build();
	}

	@MessageMapping("/check-focus/{articleId}")
	public void focusComment(@Payload boolean isFocusing, @DestinationVariable Integer articleId,
			SimpMessageHeaderAccessor headerAccessor) {
		Principal user = headerAccessor.getUser();
		if (user != null) {
			String currentSession = user.getName();
			Integer senderId = userSessionManager.getUserId(currentSession);
			if (isFocusing) {
				focuses.add(senderId);
			} else {
				focuses.remove(senderId);
			}

			if (userCommentArticleSessions.containsKey(articleId)) {
				List<String> sessionIds = userCommentArticleSessions.get(articleId);
				for (String item : sessionIds) {
					if (!item.equals(currentSession)) {
						this.simpMessagingTemplate.convertAndSendToUser(item,
								"/comment-topic/article/check-focus/" + articleId, focuses.size());
					}
				}
			}
		}
	}

	@Override
	public void handleWebSocketUnSubcribe(String[] suffix, String sessionId) {
		if (suffix == null || suffix.length == 0 || sessionId == null)
			return;

		Integer articleId = Integer.parseInt(suffix[suffix.length - 1]);

		userCommentArticleSessions.get(articleId).remove(sessionId);

		if (userCommentArticleSessions.get(articleId).size() == 0)
			userCommentArticleSessions.remove(articleId);
	}

	@Override
	public void handleWebSocketSubcribe(String[] suffix, String sessionId) {
		if (suffix == null || suffix.length == 0 || sessionId == null)
			return;
		Integer articleId = Integer.parseInt(suffix[suffix.length - 1]);

		if (!userCommentArticleSessions.containsKey(articleId)) {
			userCommentArticleSessions.put(articleId, new ArrayList<>());
		}
		userCommentArticleSessions.get(articleId).add(sessionId);
	}

	@Override
	public String getDestination() {
		return "/user/comment-topic/article/check-focus";
	}
}
