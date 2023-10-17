package com.example.demo.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.demo.response.FileResponse;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.FriendshipStatusType;

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
		if(account.getStatus() == DeleteStatusType.INACTIVE) throw new NotFoundException("Your account was blocked");
		PostEntity post = PostEntity.builder()
				.text(request.getText())
				.emotionId(request.getEmotion())
				.scope(request.getScope())
				.createTime(now)
				.account(account)
				.modTime(now)
				.status(DeleteStatusType.ACTIVE)
				.build();
		
		if(request.getCheckin() != null) {
			Optional<CheckinEntity> checkinOpt = checkinRepository.findById(request.getCheckin());
			if(checkinOpt.isEmpty()) throw new NotFoundException("CheckinID: " + request.getCheckin() + " not found"); 
			CheckinEntity checkin =  checkinOpt.get();

//			checkin.getPosts().add(post);
			post.setCheckin(checkin);			
		}
		
		if(request.getTags() != null) {
			post.setTags(new ArrayList<>());
			request.getTags().forEach((item) -> {
				Optional<AccountEntity> tagAccountOpt = accountRepository.findById(item);
				if(tagAccountOpt.isEmpty()) throw new NotFoundException("Tagged AccountID: " + item + " not found");
				AccountEntity tagAccount = tagAccountOpt.get();
//				if(tagAccount.getStatus() == DeleteStatusType.INACTIVE) throw new NotFoundException("Tagged AccountID: " + item +" was blocked");
				
				Optional<FriendshipEntity> friendshipOpt = friendshipRepository.customFindByReceiverIdAndSenderId(item, accountId);
				if(friendshipOpt.isEmpty() || friendshipOpt.get().getStatus() != FriendshipStatusType.ACCEPTED) throw new BadRequestException("Tagged AccountID: " + item + " is not your friend");

//				tagAccount.getTagPosts().add(post);
				post.getTags().add(tagAccount);
			});
		}
		
		if(request.getVideos() != null) {
			post.setVideoPosts(new ArrayList<>());
			request.getVideos().forEach(item -> {
				Timestamp createTime = new Timestamp(item.getCreateTime());
				VideoPostEntity video = VideoPostEntity.builder()
						.text(item.getNote())
						.createTime(createTime)				
						.modTime(createTime)
						.status(DeleteStatusType.ACTIVE)						
						.build();
				
				MultiValueMap<String, Object> body
				  = new LinkedMultiValueMap<>();
				body.add("file", item.getFile().getResource());			
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);
				
				HttpEntity<MultiValueMap<String, Object>> requestEntity
				 = new HttpEntity<>(body, headers);
				
				ResponseEntity<FileResponse> response = restTemplate.postForEntity(fileUrl, requestEntity, FileResponse.class);
				FileResponse fileResponse = response.getBody();
				if(response.getStatusCode() == HttpStatus.OK) {
					Optional<FileEntity> fileOpt = fileRepository.findById(fileResponse.getFileId());
					if(fileOpt.isEmpty()) throw new NotFoundException("Sorry. we cannot save you file");
					FileEntity file = fileOpt.get();
					video.setFile(file);
					
					video.setPost(post);
					post.getVideoPosts().add(video);
					
				} else {
					throw new BadRequestException(response.toString());
				}
			});
		}
		
		if(request.getImages() != null) {
			post.setImagePosts(new ArrayList<>());
			request.getImages().forEach(item -> {
				Timestamp createTime = new Timestamp(item.getCreateTime());
				ImagePostEntity image = ImagePostEntity.builder()
						.text(item.getNote())
						.createTime(createTime)				
						.modTime(createTime)
						.status(DeleteStatusType.ACTIVE)						
						.build();
				
				if(item.getTags() != null) {
					image.setTagImagePosts(new ArrayList<>());
					item.getTags().forEach(tagImageRequest -> {
						TagImagePostEntity tagImage = TagImagePostEntity.builder()
								.xPos(tagImageRequest.getXPos())
								.yPos(tagImageRequest.getYPos())
								.imagePost(image)
								.build();
						
						Optional<AccountEntity> tagImageAccountOpt = accountRepository.findById(tagImageRequest.getAccountId());
						if(tagImageAccountOpt.isEmpty()) throw new NotFoundException("Tagged Image AccountID: " + tagImageRequest.getAccountId() + " not found");
						AccountEntity tagImageAccount = tagImageAccountOpt.get();
//						if(tagImageAccount.getStatus() == DeleteStatusType.INACTIVE)  throw new NotFoundException("Tagged Image AccountID: " + item + " was blocked");
						
						Optional<FriendshipEntity> friendshipOpt = friendshipRepository.customFindByReceiverIdAndSenderId(tagImageRequest.getAccountId(), accountId);
						if(friendshipOpt.isEmpty() || friendshipOpt.get().getStatus() != FriendshipStatusType.ACCEPTED) throw new BadRequestException("Tagged AccountID: " + tagImageRequest.getAccountId() + " is not your friend");
						
						tagImage.setAccount(tagImageAccount);
						account.getTagImagePosts().add(tagImage);
						
						tagImage.setImagePost(image);
						image.getTagImagePosts().add(tagImage);
					});
				}
				
				if(item.getTexts() != null) {
					image.setTextImagePosts(new ArrayList<>());
					item.getTexts().forEach(textImageRequest -> {
						TextImagePostEntity textImage = TextImagePostEntity.builder()
								.text(textImageRequest.getText())								
								.xPos(textImageRequest.getXPos())
								.yPos(textImageRequest.getYPos())
								.color(textImageRequest.getColor())
								.size(textImageRequest.getSize())
								.imagePost(image)
								.build();
						
						textImage.setImagePost(image);
						image.getTextImagePosts().add(textImage);
					});
				}
				MultiValueMap<String, Object> body
				  = new LinkedMultiValueMap<>();
				body.add("file", item.getFile().getResource());
				
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);
				
				HttpEntity<MultiValueMap<String, Object>> requestEntity
				 = new HttpEntity<>(body, headers);

				ResponseEntity<FileResponse> response = restTemplate.postForEntity(fileUrl, requestEntity, FileResponse.class);
				FileResponse fileResponse = response.getBody();
				if(response.getStatusCode() == HttpStatus.OK) {
					Optional<FileEntity> fileOpt = fileRepository.findById(fileResponse.getFileId());
					if(fileOpt.isEmpty()) throw new NotFoundException("Sorry. we cannot save you file");
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
	
}
