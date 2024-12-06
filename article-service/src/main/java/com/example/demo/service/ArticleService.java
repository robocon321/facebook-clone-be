package com.example.demo.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.example.demo.entity.ArticleEntity;
import com.example.demo.entity.CheckinEntity;
import com.example.demo.entity.CommentArticleEntity;
import com.example.demo.entity.EmotionArticleEntity;
import com.example.demo.entity.EmotionCommentEntity;
import com.example.demo.entity.FileEntity;
import com.example.demo.entity.FriendshipEntity;
import com.example.demo.entity.ImageArticleEntity;
import com.example.demo.entity.TagArticleEntity;
import com.example.demo.entity.TagImageArticleEntity;
import com.example.demo.entity.TextImageArticleEntity;
import com.example.demo.entity.VideoArticleEntity;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.BlockException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CheckinRepository;
import com.example.demo.repository.CommentArticleRepository;
import com.example.demo.repository.EmotionArticleRepository;
import com.example.demo.repository.EmotionCommentRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.ImageArticleRepository;
import com.example.demo.repository.TagArticleRepository;
import com.example.demo.repository.TagImageArticleRepository;
import com.example.demo.repository.TextImageArticleRepository;
import com.example.demo.repository.VideoArticleRepository;
import com.example.demo.request.CreateArticleRequest;
import com.example.demo.request.CustomPageRequest;
import com.example.demo.response.AccountResponse;
import com.example.demo.response.ArticleResponse;
import com.example.demo.response.CheckinResponse;
import com.example.demo.response.CommentArticleResponse;
import com.example.demo.response.CustomPageResponse;
import com.example.demo.response.EmotionArticleResponse;
import com.example.demo.response.EmotionCommentResponse;
import com.example.demo.response.FileResponse;
import com.example.demo.response.ImageArticleResponse;
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

	private TagArticleRepository tagArticleRepository;

	private VideoArticleRepository videoRepository;

	private TagImageArticleRepository tagImageRepository;

	private ImageArticleRepository imageArticleRepository;

	private TextImageArticleRepository textImageArticleRepository;

	private CommentArticleRepository commentArticleRepository;

	private EmotionArticleRepository emotionArticleRepository;

	private EmotionCommentRepository emotionCommentRepository;

	private RestTemplate restTemplate;

	public ArticleService(
			ArticleRepository articleRepository,
			AccountRepository accountRepository,
			CheckinRepository checkinRepository,
			FriendshipRepository friendshipRepository,
			FileRepository fileRepository,
			TagArticleRepository tagArticleRepository,
			ImageArticleRepository imageArticleRepository,
			TagImageArticleRepository tagImageRepository,
			TextImageArticleRepository textImageArticleRepository,
			CommentArticleRepository commentArticleRepository,
			EmotionArticleRepository emotionArticleRepository,
			EmotionCommentRepository emotionCommentRepository,
			VideoArticleRepository videoArticleRepository,
			RestTemplate restTemplate) {
		this.articleRepository = articleRepository;
		this.accountRepository = accountRepository;
		this.checkinRepository = checkinRepository;
		this.friendshipRepository = friendshipRepository;
		this.fileRepository = fileRepository;
		this.tagArticleRepository = tagArticleRepository;
		this.tagImageRepository = tagImageRepository;
		this.imageArticleRepository = imageArticleRepository;
		this.textImageArticleRepository = textImageArticleRepository;
		this.commentArticleRepository = commentArticleRepository;
		this.emotionArticleRepository = emotionArticleRepository;
		this.emotionCommentRepository = emotionCommentRepository;
		this.videoRepository = videoArticleRepository;
		this.restTemplate = restTemplate;
	}

	private static String fileUrl = "http://localhost:9090/file";

	public boolean createArticle(CreateArticleRequest request, Integer accountId) {
		Optional<AccountEntity> currentAccountOpt = accountRepository.findById(accountId);
		AccountEntity account = currentAccountOpt.get();

		if (account.getStatus() == DeleteStatusType.INACTIVE)
			throw new BlockException(ErrorCodeType.ERROR_ACCOUNT_BLOCKED);
		Timestamp now = new Timestamp(System.currentTimeMillis());
		ArticleEntity article = ArticleEntity.builder()
				.text(request.getText())
				.emotionId(request.getEmotion())
				.scope(request.getScope())
				.createTime(now)
				.accountId(account.getAccountId())
				.modTime(now)
				.status(DeleteStatusType.ACTIVE)
				.build();

		if (request.getCheckin() != null) {
			Optional<CheckinEntity> checkinOpt = checkinRepository.findById(request.getCheckin());
			if (checkinOpt.isEmpty())
				throw new NotFoundException(ErrorCodeType.ERROR_CHECKIN_SPECIFIC_NOT_FOUND, request.getCheckin());
			article.setCheckinId(checkinOpt.get().getCheckinId());
		}
		articleRepository.save(article);

		if (request.getTags() != null) {
			request.getTags().forEach(item -> {
				Optional<AccountEntity> tagAccountOpt = accountRepository.findById(item);
				if (tagAccountOpt.isEmpty())
					throw new NotFoundException(ErrorCodeType.ERROR_TAG_SPECIFIC_NOT_FOUND, item);

				Optional<FriendshipEntity> friendshipOpt = friendshipRepository.customFindByReceiverIdAndSenderId(item,
						accountId);
				if (friendshipOpt.isEmpty() || friendshipOpt.get().getStatus() != FriendshipStatusType.ACCEPTED)
					throw new BadRequestException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_NOT_FRIEND, accountId);

				TagArticleEntity tagArticleEntity = TagArticleEntity.builder()
						.accountId(accountId)
						.articleId(article.getArticleId())
						.build();

				tagArticleRepository.save(tagArticleEntity);
			});
		}

		if (request.getVideos() != null) {
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
					VideoArticleEntity video = VideoArticleEntity.builder()
							.text(item.getNote())
							.createTime(createTime)
							.modTime(createTime)
							.status(DeleteStatusType.ACTIVE)
							.build();

					video.setFileId(file.getFileId());
					video.setArticleId(article.getArticleId());
					videoRepository.save(video);
				} else {
					throw new BadRequestException(ErrorCodeType.ERROR_CANNOT_SAVE_FILE, item.getFile().getName());
				}
			});
		}

		if (request.getImages() != null) {
			request.getImages().forEach(item -> {
				Timestamp createTime = new Timestamp(item.getCreateTime());
				ImageArticleEntity image = ImageArticleEntity.builder()
						.text(item.getNote())
						.createTime(createTime)
						.modTime(createTime)
						.status(DeleteStatusType.ACTIVE)
						.build();
				imageArticleRepository.save(image);

				if (item.getTexts() != null) {
					item.getTexts().forEach(textImageRequest -> {
						TextImageArticleEntity textImage = TextImageArticleEntity.builder()
								.text(textImageRequest.getText())
								.xPos(textImageRequest.getXPos())
								.yPos(textImageRequest.getYPos())
								.color(textImageRequest.getColor())
								.size(textImageRequest.getSize())
								.imageArticleId(image.getImageArticleId())
								.build();
						textImageArticleRepository.save(textImage);
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
					image.setFileId(file.getFileId());
					image.setArticleId(article.getArticleId());
					imageArticleRepository.save(image);
				} else {
					throw new NotFoundException(ErrorCodeType.ERROR_CANNOT_SAVE_FILE, item.getFile().getName());
				}

				if (item.getTags() != null) {
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
								.yPos(tagImageRequest.getYPos()).build();

						AccountEntity tagImageAccount = tagImageAccountOpt.get();

						tagImage.setAccountId(tagImageAccount.getAccountId());
						tagImage.setImageArticleId(image.getImageArticleId());
						tagImageRepository.save(tagImage);
					});
				}

			});
		}

		return true;
	}

	public CustomPageResponse recommendArticle(CustomPageRequest request, Integer accountId) {
		Pageable pageable = PageableUtils.createPageableFromCustomPageRequest(request);
		Page<ArticleEntity> pageArticle = articleRepository.recommendArticle(accountId, pageable);
		List<ArticleEntity> articles = pageArticle.getContent();
		List<ArticleResponse> articleResponses = new ArrayList<>();
		articles.stream().forEach(item -> {
			ArticleResponse articleResponse = mapArticleResponse(item);

			List<ImageArticleEntity> imageEntities = imageArticleRepository.findByArticleId(item.getArticleId());
			List<ImageArticleResponse> imageResponses = mapImageArticle(imageEntities);
			articleResponse.setImages(imageResponses);

			articleResponses.add(articleResponse);
		});
		return CustomPageResponse.builder()
				.totalItem(pageArticle.getTotalElements())
				.totalPage(pageArticle.getTotalPages())
				.data(articleResponses)
				.build();
	}

	public List<ImageArticleResponse> mapImageArticle(List<ImageArticleEntity> entities) {
		if (!ListUtils.isEmpty(entities)) {
			List<ImageArticleResponse> imageResponses = new ArrayList<>();
			entities.forEach(imageItem -> {
				ImageArticleResponse imageResponse = new ImageArticleResponse();
				BeanUtils.copyProperties(imageItem, imageResponse);

				Optional<FileEntity> fileOpt = fileRepository.findById(imageItem.getFileId());
				if (fileOpt.isPresent()) {
					imageResponse.setFileUrl(fileOpt.get().getName());
				}

				List<TagImageArticleEntity> tags = tagImageRepository
						.findAllByImageArticleId(imageItem.getImageArticleId());
				if (!ListUtils.isEmpty(tags)) {
					List<TagImageArticleResponse> tagImageArticleResponses = new ArrayList<>();
					tags.forEach(item -> {
						TagImageArticleResponse tagImageArticleResponse = new TagImageArticleResponse();
						BeanUtils.copyProperties(item, tagImageArticleResponse);

						AccountResponse tagImageArticleAccountResponse = new AccountResponse();
						Optional<AccountEntity> tagImageArticleAccountOpt = accountRepository
								.findById(item.getAccountId());
						if (tagImageArticleAccountOpt.isPresent()) {
							BeanUtils.copyProperties(tagImageArticleAccountOpt.get(), tagImageArticleAccountResponse);
							tagImageArticleResponse.setAccount(tagImageArticleAccountResponse);
							tagImageArticleResponses.add(tagImageArticleResponse);
						}
					});
					imageResponse.setTagImageArticles(tagImageArticleResponses);
				}

				List<TextImageArticleEntity> texts = textImageArticleRepository
						.findAllByImageArticleId(imageItem.getImageArticleId());
				if (!ListUtils.isEmpty(texts)) {
					List<TextImageArticleResponse> textImageArticleResponses = new ArrayList<>();
					texts.forEach(item -> {
						TextImageArticleResponse textImageArticleResponse = new TextImageArticleResponse();
						BeanUtils.copyProperties(item, textImageArticleResponse);
						textImageArticleResponses.add(textImageArticleResponse);
					});
					imageResponse.setTextImageArticles(textImageArticleResponses);
				}

				imageResponses.add(imageResponse);
			});
			return imageResponses;
		}
		return Collections.emptyList();
	}

	public ArticleResponse mapArticleResponse(ArticleEntity article) {
		ArticleResponse articleResponse = new ArticleResponse();
		BeanUtils.copyProperties(article, articleResponse);

		Optional<AccountEntity> account = accountRepository.findById(article.getAccountId());
		AccountResponse articleOwnerResponse = new AccountResponse();
		BeanUtils.copyProperties(account.get(), articleOwnerResponse);
		articleResponse.setAccount(articleOwnerResponse);

		if (article.getCheckinId() != null) {
			Optional<CheckinEntity> checkin = checkinRepository.findById(article.getCheckinId());
			CheckinResponse checkinResponse = new CheckinResponse();
			BeanUtils.copyProperties(checkin.get(), checkinResponse);
			articleResponse.setCheckin(checkinResponse);
		}

		List<TagArticleEntity> tags = tagArticleRepository.findAllByArticleId(article.getArticleId());

		if (!ListUtils.isEmpty(tags)) {
			List<AccountResponse> tagAccountResponses = new ArrayList<>();
			tags.forEach(item -> {
				AccountResponse tagAccountResponse = new AccountResponse();
				BeanUtils.copyProperties(item, tagAccountResponse);
				tagAccountResponses.add(tagAccountResponse);
			});
			articleResponse.setTags(tagAccountResponses);
		}

		List<VideoArticleEntity> videos = videoRepository.findAllByArticleId(article.getArticleId());
		if (!ListUtils.isEmpty(videos)) {
			List<VideoArticleResponse> videoArticleResponses = new ArrayList<>();
			videos.forEach(item -> {
				VideoArticleResponse videoArticleResponse = new VideoArticleResponse();
				BeanUtils.copyProperties(item, videoArticleResponse);

				Optional<FileEntity> fileOpt = fileRepository.findById(item.getFileId());
				if (fileOpt.isPresent()) {
					videoArticleResponse.setFileUrl(fileOpt.get().getName());
				}

				videoArticleResponses.add(videoArticleResponse);
			});
			articleResponse.setVideos(videoArticleResponses);
		}

		List<EmotionArticleEntity> emotions = emotionArticleRepository.findAllByArticleId(article.getArticleId());
		if (!ListUtils.isEmpty(emotions)) {
			List<EmotionArticleResponse> responses = emotions.stream().map(item -> {
				EmotionArticleResponse emotionArticleResponse = new EmotionArticleResponse();
				BeanUtils.copyProperties(item, emotionArticleResponse);

				AccountResponse accountResponse = new AccountResponse();
				Optional<AccountEntity> accountOpt = accountRepository.findById(item.getAccountId());
				BeanUtils.copyProperties(accountOpt.get(), accountResponse);
				emotionArticleResponse.setAccount(accountResponse);
				return emotionArticleResponse;
			}).toList();
			articleResponse.setEmotions(responses);
		}

		List<CommentArticleEntity> comments = commentArticleRepository.findAllByArticleId(article.getArticleId());
		if (!ListUtils.isEmpty(comments)) {
			List<CommentArticleResponse> responses = comments.stream().map(this::mapCommentEntityToDTO)
					.toList();
			articleResponse.setComments(responses);
		}

		return articleResponse;
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
			Integer fileId = entity.getFileId();
			Optional<FileEntity> fileOpt = fileRepository.findById(fileId);
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
