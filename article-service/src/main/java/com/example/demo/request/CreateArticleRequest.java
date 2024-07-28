package com.example.demo.request;

import java.util.List;

import com.example.demo.type.ArticleScopeType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateArticleRequest {
	private String text;
	private List<ImageRequest> images;
	private List<VideoRequest> videos;
	private List<Integer> tags;
	private Integer emotion;
	private Integer checkin;

	@NotNull(message = "Scope not null")
	private ArticleScopeType scope;
}
