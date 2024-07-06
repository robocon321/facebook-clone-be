package com.example.demo.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ImageRequest {
	@NotNull(message = "Image file not null")
	private MultipartFile file;
	
	private List<TagImageRequest> tags;
	
	private String note;
	
	private List<TextImageRequest> texts;
	
	@NotNull(message = "Create time image not null")
	private Long createTime;
}
