package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.request.AccountFriendshipRequest;
import com.example.demo.request.RecommendFriendshipRequest;
import com.example.demo.response.AccountResponse;
import com.example.demo.response.CustomPageResponse;
import com.example.demo.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
	@Autowired
	private AccountService accountService;
	
	@GetMapping("/summary-info")
	public AccountResponse getSummaryInfo(@RequestHeader HttpHeaders headers) {
		String bearerToken = headers.getFirst("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			String token = bearerToken.substring(7);
			return accountService.getSummaryInfo(token);
		}
		return null;
	}
	
	@GetMapping("/account-friendship")
	public CustomPageResponse getListFriendshipStatus(@ModelAttribute @Valid AccountFriendshipRequest request, @RequestHeader HttpHeaders headers) {
		String bearerToken = headers.getFirst("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			String token = bearerToken.substring(7);
			return accountService.getListAccountByFriendshipStatus(request, token);
		}
		return null;		
	}

	@GetMapping("/receiver-account-friendship")
	public CustomPageResponse getReceiverFriendship(@ModelAttribute @Valid AccountFriendshipRequest request, @RequestHeader HttpHeaders headers) {
		String bearerToken = headers.getFirst("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			String token = bearerToken.substring(7);
			return accountService.getReceiverByFriendshipStatus(request, token);
		}
		return null;		
	}

	@GetMapping("/sender-account-friendship")
	public CustomPageResponse getSenderFriendship(@ModelAttribute @Valid AccountFriendshipRequest request, @RequestHeader HttpHeaders headers) {
		String bearerToken = headers.getFirst("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			String token = bearerToken.substring(7);
			return accountService.getSenderByFriendshipStatus(request, token);
		}
		return null;		
	}
	
	@GetMapping("/recommend-account-friendship")
	public CustomPageResponse recommendAccount(@ModelAttribute @Valid RecommendFriendshipRequest request, @RequestHeader HttpHeaders headers) {
		String bearerToken = headers.getFirst("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			String token = bearerToken.substring(7);
			return accountService.recommendFriend(request, token);
		}
		return null;		
	}
	
	@GetMapping
	public String sayHi() {
		return "Hello world";
	}
}