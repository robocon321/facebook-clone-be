package com.example.demo.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AccountEntity;
import com.example.demo.entity.EmotionArticleEntity;
import com.example.demo.entity.ArticleEntity;
import com.example.demo.exception.BlockException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.EmotionArticleRepository;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.response.AccountResponse;
import com.example.demo.response.EmotionArticleResponse;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.EmotionType;
import com.example.demo.type.ErrorCodeType;

import jakarta.transaction.Transactional;

@Service
public class EmotionArticleService {
	private EmotionArticleRepository emotionArticleRepository;

	private ArticleRepository articleRepository;

	private AccountRepository accountRepository;

	public EmotionArticleService(EmotionArticleRepository emotionArticleRepository, ArticleRepository articleRepository,
			AccountRepository accountRepository) {
		this.emotionArticleRepository = emotionArticleRepository;
		this.articleRepository = articleRepository;
		this.accountRepository = accountRepository;
	}

	@Transactional
	public EmotionArticleResponse saveEmotionArticle(EmotionType type, Integer accountId, Integer articleId) {

		Optional<ArticleEntity> articleOpt = articleRepository.findById(articleId);
		if (articleOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ARTICLE_SPECIFIC_NOT_FOUND, articleId);

		Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
		if (accountOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_NOT_FOUND, accountId);
		AccountEntity account = accountOpt.get();
		if (account.getStatus() == DeleteStatusType.INACTIVE)
			throw new BlockException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_BLOCKED, accountId);

		EmotionArticleEntity emotion = null;

		Optional<EmotionArticleEntity> preMotionOpt = emotionArticleRepository
				.findByAccountIdAndArticleId(accountId,
						articleId);
		if (preMotionOpt.isPresent()) {
			emotion = preMotionOpt.get();
			emotion.setType(type);
			emotion.setTypeValue(type.getEmotion());
		} else {
			ArticleEntity article = articleOpt.get();
			Timestamp now = new Timestamp(System.currentTimeMillis());
			emotion = EmotionArticleEntity.builder()
					.createTime(now)
					.modTime(now)
					.status(DeleteStatusType.ACTIVE)
					.type(type)
					.articleId(article.getArticleId())
					.accountId(accountId).build();
		}

		EmotionArticleEntity newEmotion = emotionArticleRepository.save(emotion);
		EmotionArticleResponse response = new EmotionArticleResponse();
		BeanUtils.copyProperties(newEmotion, response);

		AccountResponse accountResponse = new AccountResponse();
		BeanUtils.copyProperties(account, accountResponse);
		response.setAccount(accountResponse);

		return response;
	}

	public List<EmotionArticleResponse> getListEmotionByArticleId(Integer articleId) {
		List<EmotionArticleEntity> emotions = emotionArticleRepository.findAllByArticleId(articleId);
		return emotions.stream().map(item -> {
			EmotionArticleResponse response = new EmotionArticleResponse();
			BeanUtils.copyProperties(item, response);

			AccountResponse accountResponse = new AccountResponse();
			Optional<AccountEntity> accountOpt = accountRepository.findById(item.getAccountId());
			BeanUtils.copyProperties(accountOpt.get(), accountResponse);
			response.setAccount(accountResponse);
			return response;
		}).toList();
	}

	public void deleteEmotion(Integer accountId, Integer articleId) {
		emotionArticleRepository.deleteAllByAccountIdAndArticleId(accountId, articleId);
	}
}
