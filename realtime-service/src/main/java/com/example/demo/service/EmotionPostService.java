package com.example.demo.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
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
import com.example.demo.type.ErrorCodeType;

import jakarta.transaction.Transactional;

@Service
public class EmotionPostService {
	private EmotionPostRepository emotionPostRepository;

	private PostRepository postRepository;

	private AccountRepository accountRepository;

	public EmotionPostService(EmotionPostRepository emotionPostRepository, PostRepository postRepository,
			AccountRepository accountRepository) {
		this.emotionPostRepository = emotionPostRepository;
		this.postRepository = postRepository;
		this.accountRepository = accountRepository;
	}

	@Transactional
	public EmotionPostResponse saveEmotionPost(EmotionType type, Integer accountId, Integer postId) {

		Optional<PostEntity> postOpt = postRepository.findById(postId);
		if (postOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_POST_SPECIFIC_NOT_FOUND, postId);

		Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
		if (accountOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_NOT_FOUND, accountId);
		AccountEntity account = accountOpt.get();
		if (account.getStatus() == DeleteStatusType.INACTIVE)
			throw new BlockException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_BLOCKED, accountId);

		EmotionPostEntity emotion = null;

		Optional<EmotionPostEntity> preMotionOpt = emotionPostRepository.findByAccountAccountIdAndPostPostId(accountId,
				postId);
		if (preMotionOpt.isPresent()) {
			emotion = preMotionOpt.get();
			emotion.setType(type);
			emotion.setTypeValue(type.getEmotion());
		} else {
			PostEntity post = postOpt.get();
			Timestamp now = new Timestamp(System.currentTimeMillis());
			emotion = EmotionPostEntity.builder().createTime(now).modTime(now).status(DeleteStatusType.ACTIVE)
					.type(type).post(post)
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
		return emotions.stream().map(item -> {
			EmotionPostResponse response = new EmotionPostResponse();
			BeanUtils.copyProperties(item, response);

			AccountResponse accountResponse = new AccountResponse();
			BeanUtils.copyProperties(item.getAccount(), accountResponse);
			response.setAccount(accountResponse);
			return response;
		}).toList();
	}

	public void deleteEmotion(Integer accountId, Integer postId) {
		emotionPostRepository.deleteAllByAccountAccountIdAndPostPostId(accountId, postId);
	}
}
