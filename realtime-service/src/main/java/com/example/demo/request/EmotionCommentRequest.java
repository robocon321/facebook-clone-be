package com.example.demo.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmotionCommentRequest {
	private String type;
	private Integer commentId;
}
