package com.example.demo.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VideoRequest {
	@NotNull
	private MultipartFile file;
	
	private String note;

	@NotNull
	private Long createTime;
}
