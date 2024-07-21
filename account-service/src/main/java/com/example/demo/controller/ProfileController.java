package com.example.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.response.ProfileResponse;
import com.example.demo.service.ProfileService;

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
		String bearerToken = headers.getFirst("Authorization");
		if (bearerToken == null)
			return profileService.getProfileInfoByAnonymous(profileId);

		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			String token = bearerToken.substring(7);
			return profileService.getProfileInfoByAccount(profileId, token);
		}
		return null;
	}
}
