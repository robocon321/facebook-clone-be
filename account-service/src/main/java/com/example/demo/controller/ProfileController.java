package com.example.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.response.ProfileResponse;
import com.example.demo.service.ProfileService;
import com.example.demo.utils.Const;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
	private ProfileService profileService;

	public ProfileController(ProfileService profileService) {
		this.profileService = profileService;
	}

	@GetMapping
	public ProfileResponse getSummaryInfo(@RequestParam("profileId") Integer profileId,
			@RequestHeader HttpHeaders headers) {
		String headerUserId = headers.getFirst(Const.X_USER_ID_HEADER);
		if (headerUserId == null)
			return profileService.getProfileInfoByAnonymous(profileId);
		return profileService.getProfileInfoByAccount(profileId, headerUserId);
	}
}
