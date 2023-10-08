package com.example.demo.response;

import lombok.Data;

@Data
public class AccountSummaryInfoResponse {
	private Integer accountId;
	private String firstName;
	private String lastName;
	private String profilePictureUrl;
	private String location;
	private String website;
}
