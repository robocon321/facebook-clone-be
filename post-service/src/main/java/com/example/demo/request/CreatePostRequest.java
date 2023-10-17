package com.example.demo.request;

import java.util.List;

import com.example.demo.type.PostScopeType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePostRequest {
	private String text;
	private List<ImageRequest> images;
	private List<VideoRequest> videos;
	private List<Integer> tags;
	private Integer emotion;
	private Integer checkin;

	@NotNull(message = "Scope not null")
	private PostScopeType scope;
}
