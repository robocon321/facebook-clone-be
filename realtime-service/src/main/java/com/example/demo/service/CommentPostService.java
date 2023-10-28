package com.example.demo.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
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
import com.example.demo.entity.CommentPostEntity;
import com.example.demo.entity.FileEntity;
import com.example.demo.entity.PostEntity;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CommentPostRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.request.CommentPostRequest;
import com.example.demo.response.AccountResponse;
import com.example.demo.response.CommentPostResponse;
import com.example.demo.response.EmotionCommentResponse;
import com.example.demo.response.FileResponse;
import com.example.demo.type.DeleteStatusType;

@Service
public class CommentPostService {
	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private CommentPostRepository commentPostRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private RestTemplate restTemplate;

	private String fileUrl = "http://localhost:9090/file";

	public CommentPostResponse createComment(CommentPostRequest request, Integer accountId) {

		Timestamp now = new Timestamp(System.currentTimeMillis());
		CommentPostEntity entity = CommentPostEntity.builder().createTime(now).modTime(now)
				.status(DeleteStatusType.ACTIVE).build();
		BeanUtils.copyProperties(request, entity);

		if (request.getFile() != null) {
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", request.getFile().getResource());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

			ResponseEntity<FileResponse> fileResponse = restTemplate.postForEntity(fileUrl, requestEntity,
					FileResponse.class);
			FileResponse fileBodyResponse = fileResponse.getBody();
			if (fileResponse.getStatusCode() == HttpStatus.OK) {
				Optional<FileEntity> fileOpt = fileRepository.findById(fileBodyResponse.getFileId());
				if (fileOpt.isEmpty())
					throw new NotFoundException("Save file fail");

				FileEntity file = fileOpt.get();
				entity.setFile(file);
			}
		}

		Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
		if (accountOpt.isEmpty())
			throw new NotFoundException("AccountID: " + accountId + " not found");
		AccountEntity account = accountOpt.get();
		entity.setAccount(account);

		Optional<PostEntity> postOpt = postRepository.findById(request.getPostId());
		if (postOpt.isEmpty())
			throw new NotFoundException("PostID: " + request.getPostId() + " not found");
		PostEntity post = postOpt.get();
		entity.setPost(post);

		if (request.getParentId() != null) {
			Integer parentId = request.getParentId();
			Optional<CommentPostEntity> commentOpt = commentPostRepository.findById(parentId);
			if (commentOpt.isEmpty())
				throw new NotFoundException("Comment parent Id: " + parentId + " not found");
			CommentPostEntity parentComment = commentOpt.get();
			entity.setParent(parentComment);
		}

		CommentPostEntity newEntity = commentPostRepository.save(entity);
		CommentPostResponse response = mapCommentEntityToDTO(newEntity);
		return response;
	}

	public List<CommentPostResponse> getAllCommentByPost(Integer postId) {
		List<CommentPostEntity> entities = commentPostRepository.findByPostPostId(postId);
		List<CommentPostResponse> responses = entities.stream().map(item -> mapCommentEntityToDTO(item)).toList();
		return responses;
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
