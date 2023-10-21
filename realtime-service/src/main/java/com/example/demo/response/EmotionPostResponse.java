package com.example.demo.response;

import java.sql.Timestamp;

import com.example.demo.type.EmotionType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmotionPostResponse {
	private Integer emotionId;
	private AccountResponse account;
	private Timestamp createTime;
	private Timestamp modTime;
	private EmotionType type;
}	
