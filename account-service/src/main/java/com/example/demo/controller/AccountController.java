package com.example.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.request.AccountFriendshipRequest;
import com.example.demo.request.FriendHistoryRequest;
import com.example.demo.request.RecommendFriendshipRequest;
import com.example.demo.response.AccountResponse;
import com.example.demo.response.ActionHistoryResponse;
import com.example.demo.response.CustomPageResponse;
import com.example.demo.service.AccountService;
import com.example.demo.type.ActionHistoryStatusType;
import com.example.demo.utils.Const;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
	private AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping("/summary-info")
	public AccountResponse getSummaryInfo(@RequestHeader HttpHeaders headers) {
		String headerUserId = headers.getFirst(Const.X_USER_ID_HEADER);
		if (headerUserId != null) {
			return accountService.getSummaryInfo(headerUserId);
		}
		return null;
	}

	@PostMapping("/action-history")
	public ActionHistoryResponse updateHistory(@RequestBody ActionHistoryStatusType type,
			@RequestHeader HttpHeaders headers) {
		String headerUserId = headers.getFirst(Const.X_USER_ID_HEADER);
		if (headerUserId != null) {
			return accountService.updateHistory(type, headerUserId);
		}
		return null;
	}

	@GetMapping("/friend-history")
	public CustomPageResponse friendHistory(@ModelAttribute @Valid FriendHistoryRequest request,
			@RequestHeader HttpHeaders headers) {
		String headerUserId = headers.getFirst(Const.X_USER_ID_HEADER);
		if (headerUserId != null) {
			return accountService.getFriendHistory(request, headerUserId);
		}
		return null;
	}

	@GetMapping("/account-friendship")
	public CustomPageResponse getListFriendshipStatus(@ModelAttribute @Valid AccountFriendshipRequest request,
			@RequestHeader HttpHeaders headers) {
		String headerUserId = headers.getFirst(Const.X_USER_ID_HEADER);
		if (headerUserId != null) {
			return accountService.getListAccountByFriendshipStatus(request, headerUserId);
		}
		return null;
	}

	@GetMapping("/receiver-account-friendship")
	public CustomPageResponse getReceiverFriendship(@ModelAttribute @Valid AccountFriendshipRequest request,
			@RequestHeader HttpHeaders headers) {
		String headerUserId = headers.getFirst(Const.X_USER_ID_HEADER);
		if (headerUserId != null) {
			return accountService.getReceiverByFriendshipStatus(request, headerUserId);
		}
		return null;
	}

	@GetMapping("/sender-account-friendship")
	public CustomPageResponse getSenderFriendship(@ModelAttribute @Valid AccountFriendshipRequest request,
			@RequestHeader HttpHeaders headers) {
		String headerUserId = headers.getFirst(Const.X_USER_ID_HEADER);
		if (headerUserId != null) {
			return accountService.getSenderByFriendshipStatus(request, headerUserId);
		}
		return null;
	}

	@GetMapping("/recommend-account-friendship")
	public CustomPageResponse recommendAccount(@ModelAttribute @Valid RecommendFriendshipRequest request,
			@RequestHeader HttpHeaders headers) {
		String headerUserId = headers.getFirst(Const.X_USER_ID_HEADER);
		if (headerUserId != null) {
			return accountService.recommendFriend(request, headerUserId);
		}
		return null;
	}

	@GetMapping
	public String sayHi() {
		return "Hello world";
	}
}
