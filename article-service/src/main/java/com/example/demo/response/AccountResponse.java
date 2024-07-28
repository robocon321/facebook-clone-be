package com.example.demo.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountResponse {	
	private Integer accountId;
	private String firstName;
	private String lastName;
	private String profilePictureUrl;
	private String website;
}
