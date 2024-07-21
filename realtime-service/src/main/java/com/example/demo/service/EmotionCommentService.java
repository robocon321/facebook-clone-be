package com.example.demo.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AccountEntity;
import com.example.demo.entity.CommentPostEntity;
import com.example.demo.entity.EmotionCommentEntity;
import com.example.demo.exception.BlockException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CommentPostRepository;
import com.example.demo.repository.EmotionCommentRepository;
import com.example.demo.response.AccountResponse;
import com.example.demo.response.EmotionCommentResponse;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.EmotionType;
import com.example.demo.type.ErrorCodeType;

import jakarta.transaction.Transactional;

@Service
public class EmotionCommentService {
	private EmotionCommentRepository emotionCommentRepository;

	private CommentPostRepository commentRepository;

	private AccountRepository accountRepository;

	public EmotionCommentService(EmotionCommentRepository emotionCommentRepository,
			CommentPostRepository commentRepository, AccountRepository accountRepository) {
		this.emotionCommentRepository = emotionCommentRepository;
		this.commentRepository = commentRepository;
		this.accountRepository = accountRepository;
	}

	@Transactional
	public EmotionCommentResponse saveEmotionComment(EmotionType type, Integer accountId, Integer commentId) {

		Optional<CommentPostEntity> commentOpt = commentRepository.findById(commentId);
		if (commentOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_COMMENT_SPECIFIC_NOT_FOUND, commentId);

		Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
		if (accountOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_NOT_FOUND, accountId);
		AccountEntity account = accountOpt.get();
		if (account.getStatus() == DeleteStatusType.INACTIVE)
			throw new BlockException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_BLOCKED, accountId);

		EmotionCommentEntity emotion = null;

		Optional<EmotionCommentEntity> preMotionOpt = emotionCommentRepository
				.findByAccountAccountIdAndCommentCommentId(accountId, commentId);
		if (preMotionOpt.isPresent()) {
			emotion = preMotionOpt.get();
			emotion.setType(type);
			emotion.setTypeValue(type.getEmotion());
		} else {
			CommentPostEntity comment = commentOpt.get();
			Timestamp now = new Timestamp(System.currentTimeMillis());
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
		return emotions.stream().map(item -> {
			EmotionCommentResponse response = new EmotionCommentResponse();
			BeanUtils.copyProperties(item, response);

			AccountResponse accountResponse = new AccountResponse();
			BeanUtils.copyProperties(item.getAccount(), accountResponse);
			response.setAccount(accountResponse);
			return response;
		}).toList();
	}

	public void deleteEmotion(Integer accountId, Integer commentId) {
		emotionCommentRepository.deleteAllByAccountAccountIdAndCommentCommentId(accountId, commentId);
	}
}
