package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.response.AccountSummaryInfoResponse;
import com.example.demo.service.AccountService;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
	@Autowired
	private AccountService accountService;
	
	@GetMapping("/summary-info")
	public AccountSummaryInfoResponse getSummaryInfo(@RequestHeader HttpHeaders headers) {
		String bearerToken = headers.getFirst("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			String token = bearerToken.substring(7);
			return accountService.getSummaryInfo(token);
		}
		return null;
	}
	
	@GetMapping
	public String sayHi() {
		return "Hello world";
	}
}