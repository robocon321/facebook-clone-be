package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.provider.JwtProvider;
import com.example.demo.request.CreateFriendshipRequest;
import com.example.demo.response.FriendshipResponse;
import com.example.demo.service.FriendshipService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/friendship")
public class FriendshipController {
	@Autowired	
	private FriendshipService friendshipService;
	
	@Autowired
	private JwtProvider jwtProvider;

	@PostMapping("/create")
	public ResponseEntity<FriendshipResponse> createFriendship(@Valid @RequestBody CreateFriendshipRequest request,
			@RequestHeader HttpHeaders headers) {
		// Get senderId
		String authorizationHeader = headers.getFirst("Authorization");
		String token = authorizationHeader.substring(7);
		Integer senderId = jwtProvider.getAccountIdFromJWT(token);
		
		// Return response
		FriendshipResponse response = friendshipService.createFriendship(request.getReceiverId(), senderId, request.getStatus());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PostMapping("/check-sender")
	public boolean checkSenderBlock(@Valid @RequestBody CreateFriendshipRequest request,
			@RequestHeader HttpHeaders headers) {
		String authorizationHeader = headers.getFirst("Authorization");
		String token = authorizationHeader.substring(7);
		Integer senderId = jwtProvider.getAccountIdFromJWT(token);
		
		// Return response
		boolean response = friendshipService.checkBlockFromSender(request.getReceiverId(), senderId);
		return response;		
	}
	
	@GetMapping
	public String sayHi() {
		return "Hello world";
	}
}
