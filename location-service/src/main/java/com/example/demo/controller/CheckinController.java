package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.CheckinRequest;
import com.example.demo.response.CustomPageResponse;
import com.example.demo.service.CheckinService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/checkin")
public class CheckinController {
	private CheckinService checkinService;

	public CheckinController(CheckinService checkinService) {
		this.checkinService = checkinService;
	}

	@GetMapping
	public CustomPageResponse getListFriendshipStatus(@ModelAttribute @Valid CheckinRequest request) {
		return checkinService.search(request);
	}
}
