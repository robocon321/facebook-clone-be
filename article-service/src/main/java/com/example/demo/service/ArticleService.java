package com.example.demo.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.example.demo.entity.CheckinEntity;
import com.example.demo.entity.CommentArticleEntity;
import com.example.demo.entity.FileEntity;
import com.example.demo.entity.FriendshipEntity;
import com.example.demo.entity.ImageArticleEntity;
import com.example.demo.entity.ArticleEntity;
import com.example.demo.entity.TagImageArticleEntity;
import com.example.demo.entity.TextImageArticleEntity;
import com.example.demo.entity.VideoArticleEntity;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.BlockException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CheckinRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.request.CreateArticleRequest;
import com.example.demo.request.CustomPageRequest;
import com.example.demo.response.AccountResponse;
import com.example.demo.response.CheckinResponse;
import com.example.demo.response.CommentArticleResponse;
import com.example.demo.response.CustomPageResponse;
import com.example.demo.response.EmotionCommentResponse;
import com.example.demo.response.EmotionArticleResponse;
import com.example.demo.response.FileResponse;
import com.example.demo.response.ImageArticleResponse;
import com.example.demo.response.ArticleResponse;
import com.example.demo.response.TagImageArticleResponse;
import com.example.demo.response.TextImageArticleResponse;
import com.example.demo.response.VideoArticleResponse;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.ErrorCodeType;
import com.example.demo.type.FriendshipStatusType;
import com.example.demo.utils.ListUtils;
import com.example.demo.utils.PageableUtils;

@Service
public class ArticleService {
	private ArticleRepository articleRepository;

	private AccountRepository accountRepository;

	private CheckinRepository checkinRepository;

	private FriendshipRepository friendshipRepository;

	private FileRepository fileRepository;

	private RestTemplate restTemplate;

	public ArticleService(ArticleRepository articleRepository, AccountRepository accountRepository,
			CheckinRepository checkinRepository, FriendshipRepository friendshipRepository,
			FileRepository fileRepository, RestTemplate restTemplate) {
		this.articleRepository = articleRepository;
		this.accountRepository = accountRepository;
		this.checkinRepository = checkinRepository;
		this.friendshipRepository = friendshipRepository;
		this.fileRepository = fileRepository;
		this.restTemplate = restTemplate;
	}

	private static String fileUrl = "http://localhost:9090/file";

	public boolean createArticle(CreateArticleRequest request, Integer accountId) {
		Optional<AccountEntity> currentAccountOpt = accountRepository.findById(accountId);
		AccountEntity account = currentAccountOpt.get();

		if (account.getStatus() == DeleteStatusType.INACTIVE)
			throw new BlockException(ErrorCodeType.ERROR_ACCOUNT_BLOCKED);
		Timestamp now = new Timestamp(System.currentTimeMillis());
		ArticleEntity article = ArticleEntity.builder().text(request.getText()).emotionId(request.getEmotion())
				.scope(request.getScope()).createTime(now).account(account).modTime(now).status(DeleteStatusType.ACTIVE)
				.build();

		if (request.getCheckin() != null) {
			Optional<CheckinEntity> checkinOpt = checkinRepository.findById(request.getCheckin());
			if (checkinOpt.isEmpty())
				throw new NotFoundException(ErrorCodeType.ERROR_CHECKIN_SPECIFIC_NOT_FOUND, request.getCheckin());
			CheckinEntity checkin = checkinOpt.get();

			article.setCheckin(checkin);
		}

		if (request.getTags() != null) {
			article.setTags(new ArrayList<>());
			request.getTags().forEach(item -> {
				Optional<AccountEntity> tagAccountOpt = accountRepository.findById(item);
				if (tagAccountOpt.isEmpty())
					throw new NotFoundException(ErrorCodeType.ERROR_TAG_SPECIFIC_NOT_FOUND, item);

				Optional<FriendshipEntity> friendshipOpt = friendshipRepository.customFindByReceiverIdAndSenderId(item,
						accountId);
				if (friendshipOpt.isEmpty() || friendshipOpt.get().getStatus() != FriendshipStatusType.ACCEPTED)
					throw new BadRequestException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_NOT_FRIEND, accountId);

				AccountEntity tagAccount = tagAccountOpt.get();
				article.getTags().add(tagAccount);
			});
		}

		if (request.getVideos() != null) {
			article.setVideoArticles(new ArrayList<>());
			request.getVideos().forEach(item -> {
				MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
				body.add("file", item.getFile().getResource());
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);

				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

				ResponseEntity<FileResponse> response = restTemplate.postForEntity(fileUrl, requestEntity,
						FileResponse.class);
				FileResponse fileResponse = response.getBody();
				if (fileResponse == null)
					throw new BadRequestException(ErrorCodeType.ERROR_CANNOT_SAVE_FILE, item.getFile().getName());
				if (response.getStatusCode() == HttpStatus.OK) {
					Optional<FileEntity> fileOpt = fileRepository.findById(fileResponse.getFileId());
					if (fileOpt.isEmpty())
						throw new BadRequestException(ErrorCodeType.ERROR_CANNOT_SAVE_FILE, item.getFile().getName());
					FileEntity file = fileOpt.get();

					Timestamp createTime = new Timestamp(item.getCreateTime());
					VideoArticleEntity video = VideoArticleEntity.builder().text(item.getNote()).createTime(createTime)
							.modTime(createTime).status(DeleteStatusType.ACTIVE).build();
					video.setFile(file);
					video.setArticle(article);
					article.getVideoArticles().add(video);

				} else {
					throw new BadRequestException(ErrorCodeType.ERROR_CANNOT_SAVE_FILE, item.getFile().getName());
				}
			});
		}

		if (request.getImages() != null) {
			article.setImageArticles(new ArrayList<>());
			request.getImages().forEach(item -> {
				Timestamp createTime = new Timestamp(item.getCreateTime());
				ImageArticleEntity image = ImageArticleEntity.builder().text(item.getNote()).createTime(createTime)
						.modTime(createTime).status(DeleteStatusType.ACTIVE).build();

				if (item.getTags() != null) {
					image.setTagImageArticles(new ArrayList<>());
					item.getTags().forEach(tagImageRequest -> {

						Optional<AccountEntity> tagImageAccountOpt = accountRepository
								.findById(tagImageRequest.getAccountId());
						if (tagImageAccountOpt.isEmpty())
							throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_NOT_FOUND,
									tagImageRequest.getAccountId());

						Optional<FriendshipEntity> friendshipOpt = friendshipRepository
								.customFindByReceiverIdAndSenderId(tagImageRequest.getAccountId(), accountId);
						if (friendshipOpt.isEmpty() || friendshipOpt.get().getStatus() != FriendshipStatusType.ACCEPTED)
							throw new BadRequestException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_NOT_FOUND,
									tagImageRequest.getAccountId());

						TagImageArticleEntity tagImage = TagImageArticleEntity.builder().xPos(tagImageRequest.getXPos())
								.yPos(tagImageRequest.getYPos()).imageArticle(image).build();

						AccountEntity tagImageAccount = tagImageAccountOpt.get();

						tagImage.setAccount(tagImageAccount);
						account.getTagImageArticles().add(tagImage);

						tagImage.setImageArticle(image);
						image.getTagImageArticles().add(tagImage);
					});
				}

				if (item.getTexts() != null) {
					image.setTextImageArticles(new ArrayList<>());
					item.getTexts().forEach(textImageRequest -> {
						TextImageArticleEntity textImage = TextImageArticleEntity.builder()
								.text(textImageRequest.getText())
								.xPos(textImageRequest.getXPos()).yPos(textImageRequest.getYPos())
								.color(textImageRequest.getColor()).size(textImageRequest.getSize()).imageArticle(image)
								.build();

						textImage.setImageArticle(image);
						image.getTextImageArticles().add(textImage);
					});
				}
				MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
				body.add("file", item.getFile().getResource());

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);

				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

				ResponseEntity<FileResponse> response = restTemplate.postForEntity(fileUrl, requestEntity,
						FileResponse.class);
				FileResponse fileResponse = response.getBody();
				if (fileResponse == null)
					throw new NotFoundException(ErrorCodeType.ERROR_CANNOT_SAVE_FILE, item.getFile().getName());
				if (response.getStatusCode() == HttpStatus.OK) {
					Optional<FileEntity> fileOpt = fileRepository.findById(fileResponse.getFileId());
					if (fileOpt.isEmpty())
						throw new NotFoundException(ErrorCodeType.ERROR_CANNOT_SAVE_FILE, item.getFile().getName());
					FileEntity file = fileOpt.get();
					image.setFile(file);
					article.getImageArticles().add(image);
				} else {
					throw new NotFoundException(ErrorCodeType.ERROR_CANNOT_SAVE_FILE, item.getFile().getName());
				}
				image.setArticle(article);
				article.getImageArticles().add(image);
			});
		}
		articleRepository.save(article);
		return true;
	}

	public CustomPageResponse recommendArticle(CustomPageRequest request, Integer accountId) {
		Pageable pageable = PageableUtils.createPageableFromCustomPageRequest(request);
		Page<ArticleEntity> pageArticle = articleRepository.recommendArticle(accountId, pageable);
		List<ArticleEntity> articles = pageArticle.getContent();
		List<ArticleResponse> articleResponses = new ArrayList<>();
		articles.stream().forEach(item -> {
			ArticleResponse articleResponse = mapToArticleResponse(item);
			articleResponses.add(articleResponse);
		});
		return CustomPageResponse.builder().totalItem(pageArticle.getTotalElements())
				.totalPage(pageArticle.getTotalPages())
				.data(articleResponses).build();
	}

	public ArticleResponse mapToArticleResponse(ArticleEntity article) {
		ArticleResponse articleResponse = new ArticleResponse();
		BeanUtils.copyProperties(article, articleResponse);

		AccountResponse articleOwnerResponse = new AccountResponse();
		BeanUtils.copyProperties(article.getAccount(), articleOwnerResponse);
		articleResponse.setAccount(articleOwnerResponse);

		if (article.getCheckin() != null) {
			CheckinResponse checkinResponse = new CheckinResponse();
			BeanUtils.copyProperties(article.getCheckin(), checkinResponse);
			articleResponse.setCheckin(checkinResponse);
		}

		if (!ListUtils.isEmpty(article.getTags())) {
			List<AccountResponse> tagAccountResponses = new ArrayList<>();
			article.getTags().forEach(item -> {
				AccountResponse tagAccountResponse = new AccountResponse();
				BeanUtils.copyProperties(item, tagAccountResponse);
				tagAccountResponses.add(tagAccountResponse);
			});
			articleResponse.setTags(tagAccountResponses);
		}

		if (!ListUtils.isEmpty(article.getVideoArticles())) {
			List<VideoArticleResponse> videoArticleResponses = new ArrayList<>();
			article.getVideoArticles().forEach(item -> {
				VideoArticleResponse videoArticleResponse = new VideoArticleResponse();
				BeanUtils.copyProperties(item, videoArticleResponse);

				videoArticleResponse.setFileUrl(item.getFile().getName());

				videoArticleResponses.add(videoArticleResponse);
			});
			articleResponse.setVideos(videoArticleResponses);
		}

		if (!ListUtils.isEmpty(article.getImageArticles())) {
			List<ImageArticleResponse> imageArticleResponses = new ArrayList<>();
			article.getImageArticles().forEach(imageArticleItem -> {
				ImageArticleResponse imageArticleResponse = new ImageArticleResponse();
				BeanUtils.copyProperties(imageArticleItem, imageArticleResponse);

				imageArticleResponse.setFileUrl(imageArticleItem.getFile().getName());

				if (!ListUtils.isEmpty(imageArticleItem.getTagImageArticles())) {
					List<TagImageArticleResponse> tagImageArticleResponses = new ArrayList<>();
					imageArticleItem.getTagImageArticles().forEach(item -> {
						TagImageArticleResponse tagImageArticleResponse = new TagImageArticleResponse();
						BeanUtils.copyProperties(item, tagImageArticleResponse);

						AccountResponse tagImageArticleAccountResponse = new AccountResponse();
						BeanUtils.copyProperties(item.getAccount(), tagImageArticleAccountResponse);
						tagImageArticleResponse.setAccount(tagImageArticleAccountResponse);

						tagImageArticleResponses.add(tagImageArticleResponse);
					});
					imageArticleResponse.setTagImageArticles(tagImageArticleResponses);
				}

				if (!ListUtils.isEmpty(imageArticleItem.getTextImageArticles())) {
					List<TextImageArticleResponse> textImageArticleResponses = new ArrayList<>();
					imageArticleItem.getTextImageArticles().forEach(item -> {
						TextImageArticleResponse textImageArticleResponse = new TextImageArticleResponse();
						BeanUtils.copyProperties(item, textImageArticleResponse);
						textImageArticleResponses.add(textImageArticleResponse);
					});
					imageArticleResponse.setTextImageArticles(textImageArticleResponses);
				}

				imageArticleResponses.add(imageArticleResponse);
			});
			articleResponse.setImages(imageArticleResponses);
		}

		if (!ListUtils.isEmpty(article.getEmotions())) {
			List<EmotionArticleResponse> responses = article.getEmotions().stream().map(item -> {
				EmotionArticleResponse emotionArticleResponse = new EmotionArticleResponse();
				BeanUtils.copyProperties(item, emotionArticleResponse);

				AccountResponse accountResponse = new AccountResponse();
				BeanUtils.copyProperties(item.getAccount(), accountResponse);
				emotionArticleResponse.setAccount(accountResponse);
				return emotionArticleResponse;
			}).toList();
			articleResponse.setEmotions(responses);
		}

		if (!ListUtils.isEmpty(article.getComments())) {
			List<CommentArticleResponse> responses = article.getComments().stream().map(this::mapCommentEntityToDTO)
					.toList();
			articleResponse.setComments(responses);
		}

		return articleResponse;
	}

	private CommentArticleResponse mapCommentEntityToDTO(CommentArticleEntity entity) {
		CommentArticleResponse response = new CommentArticleResponse();
		BeanUtils.copyProperties(entity, response);

		AccountResponse accountResponse = new AccountResponse();
		AccountEntity account = entity.getAccount();
		BeanUtils.copyProperties(account, accountResponse);
		response.setAccount(accountResponse);

		if (entity.getParent() != null) {
			response.setParentId(entity.getParent().getCommentId());
		}

		if (entity.getFile() != null) {
			FileEntity fileEntity = entity.getFile();
			FileResponse fileResponse = new FileResponse();
			BeanUtils.copyProperties(fileEntity, fileResponse);
			response.setFile(fileResponse);
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

		if (entity.getEmotions() != null) {
			List<EmotionCommentResponse> emotionCommentResponses = entity.getEmotions().stream().map(item -> {
				EmotionCommentResponse emotionCommentResponse = new EmotionCommentResponse();
				BeanUtils.copyProperties(item, emotionCommentResponse);

				AccountEntity accountEmotion = item.getAccount();
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
