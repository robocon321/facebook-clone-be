package com.example.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.BadRequestException;
import com.example.demo.request.CreateFriendshipRequest;
import com.example.demo.response.FriendshipResponse;
import com.example.demo.service.FriendshipService;
import com.example.demo.type.ErrorCodeType;
import com.example.demo.utils.Const;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/friendship")
public class FriendshipController {
	private FriendshipService friendshipService;

	public FriendshipController(FriendshipService friendshipService) {
		this.friendshipService = friendshipService;
	}

	@PostMapping("/create")
	public ResponseEntity<FriendshipResponse> createFriendship(@Valid @RequestBody CreateFriendshipRequest request,
			@RequestHeader HttpHeaders headers) {
		String headerUserId = headers.getFirst(Const.X_USER_ID_HEADER);
		if (headerUserId == null) {
			throw new BadRequestException(ErrorCodeType.ERROR_REQUIRE_LOGIN);
		} else {
			Integer senderId = Integer.parseInt(headerUserId);
			FriendshipResponse response = friendshipService.createFriendship(request.getReceiverId(), senderId,
					request.getStatus());
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}
	}

	@PostMapping("/check-sender")
	public boolean checkSenderBlock(@Valid @RequestBody CreateFriendshipRequest request,
			@RequestHeader HttpHeaders headers) {
		String headerUserId = headers.getFirst(Const.X_USER_ID_HEADER);
		if (headerUserId == null) {
			throw new BadRequestException(ErrorCodeType.ERROR_REQUIRE_LOGIN);
		} else {
			Integer senderId = Integer.parseInt(headerUserId);
			return friendshipService.checkBlockFromSender(request.getReceiverId(), senderId);
		}
	}

	@GetMapping
	public String sayHi() {
		return "Hello world";
	}
}
