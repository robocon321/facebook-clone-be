package com.example.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.BadRequestException;
import com.example.demo.provider.JwtProvider;
import com.example.demo.request.CreateArticleRequest;
import com.example.demo.request.CustomPageRequest;
import com.example.demo.response.CustomPageResponse;
import com.example.demo.service.ArticleService;
import com.example.demo.type.ErrorCodeType;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/article")
public class ArticleController {
	private JwtProvider jwtProvider;
	private ArticleService articleService;

	public ArticleController(JwtProvider jwtProvider, ArticleService articleService) {
		this.jwtProvider = jwtProvider;
		this.articleService = articleService;
	}

	@GetMapping("/recommend")
	public ResponseEntity<CustomPageResponse> recommendArticle(@ModelAttribute @Valid CustomPageRequest request,
			@RequestHeader HttpHeaders headers) {
		String authorizationHeader = headers.getFirst("Authorization");
		if (authorizationHeader == null) {
			throw new BadRequestException(ErrorCodeType.ERROR_REQUIRE_LOG);
		} else {
			String token = authorizationHeader.substring(7);
			Integer accountId = jwtProvider.getAccountIdFromJWT(token);
			CustomPageResponse response = articleService.recommendArticle(request, accountId);
			return ResponseEntity.ok(response);
		}
	}

	@PostMapping
	public ResponseEntity<Boolean> createArticle(@ModelAttribute @Valid CreateArticleRequest request,
			@RequestHeader HttpHeaders headers) {
		String authorizationHeader = headers.getFirst("Authorization");
		if (authorizationHeader == null) {
			throw new BadRequestException(ErrorCodeType.ERROR_REQUIRE_LOG);
		} else {
			String token = authorizationHeader.substring(7);
			Integer accountId = jwtProvider.getAccountIdFromJWT(token);
			boolean response = articleService.createArticle(request, accountId);
			return ResponseEntity.ok(response);
		}
	}

	@GetMapping
	public String sayHi() {
		return "Hello world";
	}
}
