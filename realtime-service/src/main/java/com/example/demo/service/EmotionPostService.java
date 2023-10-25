package com.example.demo.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AccountEntity;
import com.example.demo.entity.EmotionPostEntity;
import com.example.demo.entity.PostEntity;
import com.example.demo.exception.BlockException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.EmotionPostRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.response.AccountResponse;
import com.example.demo.response.EmotionPostResponse;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.EmotionType;

import jakarta.transaction.Transactional;

@Service
public class EmotionPostService {
	@Autowired
	private EmotionPostRepository emotionPostRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private AccountRepository accountRepository;
	
	@Transactional
	public EmotionPostResponse saveEmotionPost(EmotionType type, Integer accountId, Integer postId) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		
		Optional<EmotionPostEntity> preMotionOpt = emotionPostRepository.findByAccountAccountIdAndPostPostId(accountId, postId);
		
		Optional<PostEntity> postOpt = postRepository.findById(postId);
		if (postOpt.isEmpty())
			throw new NotFoundException("PostID: " + postId + " is not found");
		PostEntity post = postOpt.get();

		Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
		if (accountOpt.isEmpty())
			throw new NotFoundException("AccountID: " + accountId + " is not found");
		AccountEntity account = accountOpt.get();
		if (account.getStatus() == DeleteStatusType.INACTIVE)
			throw new BlockException("AccountID: " + accountId + " was blocked");

		EmotionPostEntity emotion = null;

		if(preMotionOpt.isPresent()) {			
			emotion = preMotionOpt.get();
			emotion.setType(type);
			emotion.setTypeValue(type.getEmotion());		
		}
		else {			
			emotion = EmotionPostEntity.builder().createTime(now).modTime(now).status(DeleteStatusType.ACTIVE).type(type).post(post)
					.account(account).build();			
		}

		EmotionPostEntity newEmotion = emotionPostRepository.save(emotion);
		EmotionPostResponse response = new EmotionPostResponse();
		BeanUtils.copyProperties(newEmotion, response);

		AccountResponse accountResponse = new AccountResponse();
		BeanUtils.copyProperties(account, accountResponse);
		response.setAccount(accountResponse);

		return response;
	}

	public List<EmotionPostResponse> getListEmotionByPostId(Integer postId) {
		List<EmotionPostEntity> emotions = emotionPostRepository.findByPostPostId(postId);
		List<EmotionPostResponse> responses = emotions.stream().map(item -> {
			EmotionPostResponse response = new EmotionPostResponse();
			BeanUtils.copyProperties(item, response);

			AccountResponse accountResponse = new AccountResponse();
			BeanUtils.copyProperties(item.getAccount(), accountResponse);
			response.setAccount(accountResponse);
			return response;
		}).toList();
		return responses;
	}
	
	public void deleteEmotion(Integer accountId, Integer postId) {
		emotionPostRepository.deleteAllByAccountAccountIdAndPostPostId(accountId, postId);
	}
}
