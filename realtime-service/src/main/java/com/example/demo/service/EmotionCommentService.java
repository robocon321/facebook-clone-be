package com.example.demo.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AccountEntity;
import com.example.demo.entity.CommentPostEntity;
import com.example.demo.entity.EmotionCommentEntity;
import com.example.demo.entity.PostEntity;
import com.example.demo.exception.BlockException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CommentPostRepository;
import com.example.demo.repository.EmotionCommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.response.AccountResponse;
import com.example.demo.response.EmotionCommentResponse;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.EmotionType;

import jakarta.transaction.Transactional;

@Service
public class EmotionCommentService {
	@Autowired
	private EmotionCommentRepository emotionCommentRepository;

	@Autowired
	private CommentPostRepository commentRepository;

	@Autowired
	private AccountRepository accountRepository;
	
	@Transactional
	public EmotionCommentResponse saveEmotionComment(EmotionType type, Integer accountId, Integer commentId) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		
		Optional<EmotionCommentEntity> preMotionOpt = emotionCommentRepository.findByAccountAccountIdAndCommentCommentId(accountId, commentId);
		
		Optional<CommentPostEntity> commentOpt = commentRepository.findById(commentId);
		if (commentOpt.isEmpty())
			throw new NotFoundException("CommentID: " + commentId + " is not found");
		CommentPostEntity comment = commentOpt.get();

		Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
		if (accountOpt.isEmpty())
			throw new NotFoundException("AccountID: " + accountId + " is not found");
		AccountEntity account = accountOpt.get();
		if (account.getStatus() == DeleteStatusType.INACTIVE)
			throw new BlockException("AccountID: " + accountId + " was blocked");

		EmotionCommentEntity emotion = null;

		if(preMotionOpt.isPresent()) {			
			emotion = preMotionOpt.get();
			emotion.setType(type);
			emotion.setTypeValue(type.getEmotion());		
		}
		else {			
			emotion = EmotionCommentEntity.builder()
					.createTime(now)
					.modTime(now)
					.status(DeleteStatusType.ACTIVE)
					.type(type)
					.comment(comment)
					.account(account).build();			
		}

		EmotionCommentEntity newEmotion = emotionCommentRepository.save(emotion);
		EmotionCommentResponse response = new EmotionCommentResponse();
		BeanUtils.copyProperties(newEmotion, response);

		AccountResponse accountResponse = new AccountResponse();
		BeanUtils.copyProperties(account, accountResponse);
		response.setAccount(accountResponse);

		return response;
	}

	public List<EmotionCommentResponse> getListEmotionByCommentId(Integer commentId) {
		List<EmotionCommentEntity> emotions = emotionCommentRepository.findByCommentCommentId(commentId);
		List<EmotionCommentResponse> responses = emotions.stream().map(item -> {
			EmotionCommentResponse response = new EmotionCommentResponse();
			BeanUtils.copyProperties(item, response);

			AccountResponse accountResponse = new AccountResponse();
			BeanUtils.copyProperties(item.getAccount(), accountResponse);
			response.setAccount(accountResponse);
			return response;
		}).toList();
		return responses;
	}
	
	public void deleteEmotion(Integer accountId, Integer commentId) {
		emotionCommentRepository.deleteAllByAccountAccountIdAndCommentCommentId(accountId, commentId);
	}
}
