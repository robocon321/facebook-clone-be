package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.EmotionArticleEntity;

import jakarta.transaction.Transactional;

@Repository
public interface EmotionArticleRepository extends JpaRepository<EmotionArticleEntity, Integer> {
	List<EmotionArticleEntity> findByArticleArticleId(Integer articleId);

	Optional<EmotionArticleEntity> findByAccountAccountIdAndArticleArticleId(Integer accountId, Integer articleId);

	@Transactional
	void deleteAllByAccountAccountIdAndArticleArticleId(Integer accountId, Integer articleId);
}
