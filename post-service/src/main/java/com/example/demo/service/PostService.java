package com.example.demo.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.demo.entity.CommentPostEntity;
import com.example.demo.entity.FileEntity;
import com.example.demo.entity.FriendshipEntity;
import com.example.demo.entity.ImagePostEntity;
import com.example.demo.entity.PostEntity;
import com.example.demo.entity.TagImagePostEntity;
import com.example.demo.entity.TextImagePostEntity;
import com.example.demo.entity.VideoPostEntity;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CheckinRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.request.CreatePostRequest;
import com.example.demo.request.CustomPageRequest;
import com.example.demo.response.AccountResponse;
import com.example.demo.response.CheckinResponse;
import com.example.demo.response.CommentPostResponse;
import com.example.demo.response.CustomPageResponse;
import com.example.demo.response.EmotionCommentResponse;
import com.example.demo.response.EmotionPostResponse;
import com.example.demo.response.FileResponse;
import com.example.demo.response.ImagePostResponse;
import com.example.demo.response.PostResponse;
import com.example.demo.response.TagImagePostResponse;
import com.example.demo.response.TextImagePostResponse;
import com.example.demo.response.VideoPostResponse;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.FriendshipStatusType;
import com.example.demo.utils.PageableUtils;

@Service
public class PostService {
	@Autowired
	private PostRepository postRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CheckinRepository checkinRepository;

	@Autowired
	private FriendshipRepository friendshipRepository;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private RestTemplate restTemplate;

	private String fileUrl = "http://localhost:9090/file";

	public boolean createPost(CreatePostRequest request, Integer accountId) {
		Optional<AccountEntity> currentAccountOpt = accountRepository.findById(accountId);
		AccountEntity account = currentAccountOpt.get();

		Timestamp now = new Timestamp(System.currentTimeMillis());
		if (account.getStatus() == DeleteStatusType.INACTIVE)
			throw new NotFoundException("Your account was blocked");
		PostEntity post = PostEntity.builder().text(request.getText()).emotionId(request.getEmotion())
				.scope(request.getScope()).createTime(now).account(account).modTime(now).status(DeleteStatusType.ACTIVE)
				.build();

		if (request.getCheckin() != null) {
			Optional<CheckinEntity> checkinOpt = checkinRepository.findById(request.getCheckin());
			if (checkinOpt.isEmpty())
				throw new NotFoundException("CheckinID: " + request.getCheckin() + " not found");
			CheckinEntity checkin = checkinOpt.get();

//			checkin.getPosts().add(post);
			post.setCheckin(checkin);
		}

		if (request.getTags() != null) {
			post.setTags(new ArrayList<>());
			request.getTags().forEach((item) -> {
				Optional<AccountEntity> tagAccountOpt = accountRepository.findById(item);
				if (tagAccountOpt.isEmpty())
					throw new NotFoundException("Tagged AccountID: " + item + " not found");
				AccountEntity tagAccount = tagAccountOpt.get();
//				if(tagAccount.getStatus() == DeleteStatusType.INACTIVE) throw new NotFoundException("Tagged AccountID: " + item +" was blocked");

				Optional<FriendshipEntity> friendshipOpt = friendshipRepository.customFindByReceiverIdAndSenderId(item,
						accountId);
				if (friendshipOpt.isEmpty() || friendshipOpt.get().getStatus() != FriendshipStatusType.ACCEPTED)
					throw new BadRequestException("Tagged AccountID: " + item + " is not your friend");

//				tagAccount.getTagPosts().add(post);
				post.getTags().add(tagAccount);
			});
		}

		if (request.getVideos() != null) {
			post.setVideoPosts(new ArrayList<>());
			request.getVideos().forEach(item -> {
				Timestamp createTime = new Timestamp(item.getCreateTime());
				VideoPostEntity video = VideoPostEntity.builder().text(item.getNote()).createTime(createTime)
						.modTime(createTime).status(DeleteStatusType.ACTIVE).build();

				MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
				body.add("file", item.getFile().getResource());
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);

				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

				ResponseEntity<FileResponse> response = restTemplate.postForEntity(fileUrl, requestEntity,
						FileResponse.class);
				FileResponse fileResponse = response.getBody();
				if (response.getStatusCode() == HttpStatus.OK) {
					Optional<FileEntity> fileOpt = fileRepository.findById(fileResponse.getFileId());
					if (fileOpt.isEmpty())
						throw new NotFoundException("Sorry. we cannot save you file");
					FileEntity file = fileOpt.get();
					video.setFile(file);

					video.setPost(post);
					post.getVideoPosts().add(video);

				} else {
					throw new BadRequestException(response.toString());
				}
			});
		}

		if (request.getImages() != null) {
			post.setImagePosts(new ArrayList<>());
			request.getImages().forEach(item -> {
				Timestamp createTime = new Timestamp(item.getCreateTime());
				ImagePostEntity image = ImagePostEntity.builder().text(item.getNote()).createTime(createTime)
						.modTime(createTime).status(DeleteStatusType.ACTIVE).build();

				if (item.getTags() != null) {
					image.setTagImagePosts(new ArrayList<>());
					item.getTags().forEach(tagImageRequest -> {
						TagImagePostEntity tagImage = TagImagePostEntity.builder().xPos(tagImageRequest.getXPos())
								.yPos(tagImageRequest.getYPos()).imagePost(image).build();

						Optional<AccountEntity> tagImageAccountOpt = accountRepository
								.findById(tagImageRequest.getAccountId());
						if (tagImageAccountOpt.isEmpty())
							throw new NotFoundException(
									"Tagged Image AccountID: " + tagImageRequest.getAccountId() + " not found");
						AccountEntity tagImageAccount = tagImageAccountOpt.get();
//						if(tagImageAccount.getStatus() == DeleteStatusType.INACTIVE)  throw new NotFoundException("Tagged Image AccountID: " + item + " was blocked");

						Optional<FriendshipEntity> friendshipOpt = friendshipRepository
								.customFindByReceiverIdAndSenderId(tagImageRequest.getAccountId(), accountId);
						if (friendshipOpt.isEmpty() || friendshipOpt.get().getStatus() != FriendshipStatusType.ACCEPTED)
							throw new BadRequestException(
									"Tagged AccountID: " + tagImageRequest.getAccountId() + " is not your friend");

						tagImage.setAccount(tagImageAccount);
						account.getTagImagePosts().add(tagImage);

						tagImage.setImagePost(image);
						image.getTagImagePosts().add(tagImage);
					});
				}

				if (item.getTexts() != null) {
					image.setTextImagePosts(new ArrayList<>());
					item.getTexts().forEach(textImageRequest -> {
						TextImagePostEntity textImage = TextImagePostEntity.builder().text(textImageRequest.getText())
								.xPos(textImageRequest.getXPos()).yPos(textImageRequest.getYPos())
								.color(textImageRequest.getColor()).size(textImageRequest.getSize()).imagePost(image)
								.build();

						textImage.setImagePost(image);
						image.getTextImagePosts().add(textImage);
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
				if (response.getStatusCode() == HttpStatus.OK) {
					Optional<FileEntity> fileOpt = fileRepository.findById(fileResponse.getFileId());
					if (fileOpt.isEmpty())
						throw new NotFoundException("Sorry. we cannot save you file");
					FileEntity file = fileOpt.get();
					image.setFile(file);
					post.getImagePosts().add(image);
				} else {
					throw new BadRequestException(response.toString());
				}
				image.setPost(post);
				post.getImagePosts().add(image);
			});
		}
		postRepository.save(post);
		return true;
	}

	public CustomPageResponse recommendPost(CustomPageRequest request, Integer accountId) {
		Pageable pageable = PageableUtils.createPageableFromCustomPageRequest(request);
		Page<PostEntity> pagePost = postRepository.recommendPost(accountId, pageable);
		List<PostEntity> posts = pagePost.getContent();
		List<PostResponse> postResponses = new ArrayList<>();
		posts.stream().forEach(item -> {
			PostResponse postResponse = mapToPostResponse(item);
			postResponses.add(postResponse);
		});
		return CustomPageResponse.builder().totalItem(pagePost.getTotalElements()).totalPage(pagePost.getTotalPages())
				.data(postResponses).build();
	}

	public PostResponse mapToPostResponse(PostEntity post) {
		PostResponse postResponse = new PostResponse();
		BeanUtils.copyProperties(post, postResponse);

		AccountResponse postOwnerResponse = new AccountResponse();
		BeanUtils.copyProperties(post.getAccount(), postOwnerResponse);
		postResponse.setAccount(postOwnerResponse);

		if (post.getCheckin() != null) {
			CheckinResponse checkinResponse = new CheckinResponse();
			BeanUtils.copyProperties(post.getCheckin(), checkinResponse);
			postResponse.setCheckin(checkinResponse);
		}

		if (post.getTags() != null && post.getTags().size() > 0) {
			List<AccountResponse> tagAccountResponses = new ArrayList<>();
			post.getTags().forEach(item -> {
				AccountResponse tagAccountResponse = new AccountResponse();
				BeanUtils.copyProperties(item, tagAccountResponse);
				tagAccountResponses.add(tagAccountResponse);
			});
			postResponse.setTags(tagAccountResponses);
		}

		if (post.getVideoPosts() != null && post.getVideoPosts().size() > 0) {
			List<VideoPostResponse> videoPostResponses = new ArrayList<>();
			post.getVideoPosts().forEach(item -> {
				VideoPostResponse videoPostResponse = new VideoPostResponse();
				BeanUtils.copyProperties(item, videoPostResponse);

				videoPostResponse.setFileUrl(item.getFile().getName());

				videoPostResponses.add(videoPostResponse);
			});
			postResponse.setVideos(videoPostResponses);
		}

		if (post.getImagePosts() != null && post.getImagePosts().size() > 0) {
			List<ImagePostResponse> imagePostResponses = new ArrayList<>();
			post.getImagePosts().forEach(imagePostItem -> {
				ImagePostResponse imagePostResponse = new ImagePostResponse();
				BeanUtils.copyProperties(imagePostItem, imagePostResponse);

				imagePostResponse.setFileUrl(imagePostItem.getFile().getName());

				if (imagePostItem.getTagImagePosts() != null && imagePostItem.getTagImagePosts().size() > 0) {
					List<TagImagePostResponse> tagImagePostResponses = new ArrayList<>();
					imagePostItem.getTagImagePosts().forEach(item -> {
						TagImagePostResponse tagImagePostResponse = new TagImagePostResponse();
						BeanUtils.copyProperties(item, tagImagePostResponse);

						AccountResponse tagImagePostAccountResponse = new AccountResponse();
						BeanUtils.copyProperties(item.getAccount(), tagImagePostAccountResponse);
						tagImagePostResponse.setAccount(tagImagePostAccountResponse);

						tagImagePostResponses.add(tagImagePostResponse);
					});
					imagePostResponse.setTagImagePosts(tagImagePostResponses);
				}

				if (imagePostItem.getTextImagePosts() != null && imagePostItem.getTextImagePosts().size() > 0) {
					List<TextImagePostResponse> textImagePostResponses = new ArrayList<>();
					imagePostItem.getTextImagePosts().forEach(item -> {
						TextImagePostResponse textImagePostResponse = new TextImagePostResponse();
						BeanUtils.copyProperties(item, textImagePostResponse);
						textImagePostResponses.add(textImagePostResponse);
					});
					imagePostResponse.setTextImagePosts(textImagePostResponses);
				}

				imagePostResponses.add(imagePostResponse);
			});
			postResponse.setImages(imagePostResponses);
		}

		if (post.getEmotions().size() > 0) {
			List<EmotionPostResponse> responses = post.getEmotions().stream().map(item -> {
				EmotionPostResponse emotionPostResponse = new EmotionPostResponse();
				BeanUtils.copyProperties(item, emotionPostResponse);

				AccountResponse accountResponse = new AccountResponse();
				BeanUtils.copyProperties(item.getAccount(), accountResponse);
				emotionPostResponse.setAccount(accountResponse);
				return emotionPostResponse;
			}).toList();
			postResponse.setEmotions(responses);
		}
		
		if(post.getComments().size() > 0) {
			List<CommentPostResponse> responses = post.getComments().stream().map(item -> mapCommentEntityToDTO(item)).toList();
			postResponse.setComments(responses);
		}

		return postResponse;
	}
	
	private CommentPostResponse mapCommentEntityToDTO(CommentPostEntity entity) {
		CommentPostResponse response = new CommentPostResponse();
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
					throw new NotFoundException("Mention AccountID: " + accountMentionId);
				AccountEntity accountMention = accountMentionOpt.get();
				AccountResponse accountMenResponse = new AccountResponse();
				BeanUtils.copyProperties(accountMention, accountMenResponse);
				accountResponses.add(accountMenResponse);
			}
			
			response.setMentions(accountResponses);;
		}
		
		if(entity.getEmotions() != null) {
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
