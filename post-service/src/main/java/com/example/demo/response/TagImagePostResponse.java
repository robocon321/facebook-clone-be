package com.example.demo.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TagImagePostResponse {
	private Integer tagId;
	private Double xPos;
	private Double yPos;
	private AccountResponse account;
}
