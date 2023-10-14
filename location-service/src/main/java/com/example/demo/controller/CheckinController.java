package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.CheckinRequest;
import com.example.demo.response.PageResponse;
import com.example.demo.service.CheckinService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/checkin")
public class CheckinController {
	@Autowired
	private CheckinService checkinService;
	
	@GetMapping
	public PageResponse getListFriendshipStatus(@ModelAttribute @Valid CheckinRequest request) {		
		return checkinService.search(request);		
	}
}
