package com.example.demo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TagImageRequest {
	@NotNull(message = "AccountID tag image not null")
	private Integer accountId;
	
	@NotNull(message = "x_pos tag image not null")
	private Double xPos;
	
	@NotNull(message = "y_pos tag image not null")
	private Double yPos;
}
