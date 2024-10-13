package com.example.demo.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.demo.entity.AccountEntity;
import com.example.demo.entity.ArticleEntity;
import com.example.demo.entity.CommentArticleEntity;
import com.example.demo.entity.EmotionCommentEntity;
import com.example.demo.entity.FileEntity;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CommentArticleRepository;
import com.example.demo.repository.EmotionCommentRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.request.CommentArticleRequest;
import com.example.demo.response.AccountResponse;
import com.example.demo.response.CommentArticleResponse;
import com.example.demo.response.EmotionCommentResponse;
import com.example.demo.response.FileResponse;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.ErrorCodeType;

@Service
public class CommentArticleService {
	private AccountRepository accountRepository;

	private FileRepository fileRepository;

	private CommentArticleRepository commentArticleRepository;

	private ArticleRepository articleRepository;

	private EmotionCommentRepository emotionCommentRepository;

	private RestTemplate restTemplate;

	private static final String FILE_URL = "http://localhost:9090/file";

	public CommentArticleService(AccountRepository accountRepository,
			FileRepository fileRepository,
			CommentArticleRepository commentArticleRepository,
			ArticleRepository articleRepository,
			EmotionCommentRepository emotionCommentRepository,
			RestTemplate restTemplate) {
		this.accountRepository = accountRepository;
		this.fileRepository = fileRepository;
		this.commentArticleRepository = commentArticleRepository;
		this.articleRepository = articleRepository;
		this.emotionCommentRepository = emotionCommentRepository;
		this.restTemplate = restTemplate;
	}

	public CommentArticleResponse createComment(CommentArticleRequest request, Integer accountId) {

		Timestamp now = new Timestamp(System.currentTimeMillis());
		CommentArticleEntity entity = CommentArticleEntity.builder().createTime(now).modTime(now)
				.status(DeleteStatusType.ACTIVE).build();
		BeanUtils.copyProperties(request, entity);

		if (request.getFile() != null) {
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", request.getFile().getResource());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

			ResponseEntity<FileResponse> fileResponse = restTemplate.postForEntity(FILE_URL, requestEntity,
					FileResponse.class);
			FileResponse fileBodyResponse = fileResponse.getBody();
			if (fileBodyResponse != null && fileResponse.getStatusCode() == HttpStatus.OK) {
				Optional<FileEntity> fileOpt = fileRepository.findById(fileBodyResponse.getFileId());
				if (fileOpt.isEmpty())
					throw new BadRequestException(ErrorCodeType.ERROR_CANNOT_SAVE_FILE, request.getFile().getName());
				FileEntity file = fileOpt.get();
				entity.setFileId(file.getFileId());
			}
		}

		Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
		if (accountOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_NOT_FOUND, accountId);
		AccountEntity account = accountOpt.get();
		entity.setAccountId(account.getAccountId());

		Optional<ArticleEntity> articleOpt = articleRepository.findById(request.getArticleId());
		if (articleOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ARTICLE_SPECIFIC_NOT_FOUND, request.getArticleId());
		ArticleEntity article = articleOpt.get();
		entity.setArticleId(article.getArticleId());

		if (request.getParentId() != null) {
			Integer parentId = request.getParentId();
			Optional<CommentArticleEntity> commentOpt = commentArticleRepository.findById(parentId);
			if (commentOpt.isEmpty())
				throw new NotFoundException(ErrorCodeType.ERROR_COMMENT_SPECIFIC_NOT_FOUND, parentId);
			entity.setParentId(commentOpt.get().getParentId());
		}

		CommentArticleEntity newEntity = commentArticleRepository.save(entity);
		return mapCommentEntityToDTO(newEntity);
	}

	public List<CommentArticleResponse> getAllCommentByArticle(Integer articleId) {
		List<CommentArticleEntity> entities = commentArticleRepository.findAllByArticleId(articleId);
		return entities.stream().map(this::mapCommentEntityToDTO).toList();
	}

	private CommentArticleResponse mapCommentEntityToDTO(CommentArticleEntity entity) {
		CommentArticleResponse response = new CommentArticleResponse();
		BeanUtils.copyProperties(entity, response);

		AccountResponse accountResponse = new AccountResponse();
		Integer accountId = entity.getAccountId();

		Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);

		BeanUtils.copyProperties(accountOpt.get(), accountResponse);
		response.setAccount(accountResponse);

		if (entity.getParentId() != null) {
			response.setParentId(entity.getParentId());
		}

		if (entity.getFileId() != null) {
			Optional<FileEntity> fileOpt = fileRepository.findById(entity.getFileId());
			if (fileOpt.isPresent()) {
				FileResponse fileResponse = new FileResponse();
				BeanUtils.copyProperties(fileOpt.get(), fileResponse);
				response.setFile(fileResponse);
			}
		}

		if (entity.getMentionedAccounts() != null && entity.getMentionedAccounts().length() > 0) {
			int[] mentions = Arrays.stream(entity.getMentionedAccounts().split(",")).mapToInt(Integer::parseInt)
					.toArray();
			List<AccountResponse> accountResponses = new ArrayList<>();

			for (int i = 0; i < mentions.length; i++) {
				int accountMentionId = mentions[i];
				Optional<AccountEntity> accountMentionOpt = accountRepository.findById(accountMentionId);
				if (accountMentionOpt.isEmpty())
					throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_NOT_FOUND, accountMentionId);
				AccountEntity accountMention = accountMentionOpt.get();
				AccountResponse accountMenResponse = new AccountResponse();
				BeanUtils.copyProperties(accountMention, accountMenResponse);
				accountResponses.add(accountMenResponse);
			}

			response.setMentions(accountResponses);
		}

		List<EmotionCommentEntity> emotions = emotionCommentRepository.findAllByCommentId(entity.getCommentId());
		if (emotions != null) {
			List<EmotionCommentResponse> emotionCommentResponses = emotions.stream().map(item -> {
				EmotionCommentResponse emotionCommentResponse = new EmotionCommentResponse();
				BeanUtils.copyProperties(item, emotionCommentResponse);

				Optional<AccountEntity> accountEmotionOpt = accountRepository.findById(item.getAccountId());
				AccountEntity accountEmotion = accountEmotionOpt.get();
				AccountResponse accountEmotionResponse = new AccountResponse();
				BeanUtils.copyProperties(accountEmotion, accountEmotionResponse);
				emotionCommentResponse.setAccount(accountResponse);

				return emotionCommentResponse;
			}).toList();

			response.setEmotions(emotionCommentResponses);
		}

		return response;
	}

}
