package com.example.demo.response;

import java.util.List;

import lombok.Data;

@Data
public class RecommendAccountResponse {
	private Integer accountId;
	private String firstName;
	private String lastName;
	private String profilePictureUrl;
	private String location;
	private String website;
	private List<AccountResponse> manualFriends;
}
