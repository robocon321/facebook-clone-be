package com.example.demo.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentArticleRequest {
	@NotBlank
	@NotNull
	private String text;

	private String mentionedAccounts;
	private Integer parentId;
	private MultipartFile file;

	@NotNull
	private Integer articleId;
}
