package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TextImageRequest {
	@NotNull(message = "Text of image text not null")
	@NotBlank(message = "Text of image text not blank")
	private String text;
	
	@NotNull(message = "Color of image text is not null")
	@NotBlank(message = "Color of image text is not blank")
	private String color;

	@NotNull(message = "x_pos of image text is not null")
	private Double xPos;
	
	@NotNull(message = "y_pos of image text is not null")
	private Double yPos;
	
	@NotNull(message = "Size of image text is not null")
	private Double size;
}
