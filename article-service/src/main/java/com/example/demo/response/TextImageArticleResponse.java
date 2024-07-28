package com.example.demo.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TextImageArticleResponse {
	private Integer textId;
	private String text;
	private Double xPos;
	private Double yPos;
	private String color;
	private Double size;
}
