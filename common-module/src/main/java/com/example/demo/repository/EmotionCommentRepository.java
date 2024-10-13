package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.EmotionCommentEntity;

import jakarta.transaction.Transactional;

@Repository
public interface EmotionCommentRepository extends JpaRepository<EmotionCommentEntity, Integer> {
	List<EmotionCommentEntity> findAllByCommentId(Integer commentId);

	Optional<EmotionCommentEntity> findByAccountIdAndCommentId(Integer accountId, Integer commentId);

	@Transactional
	void deleteAllByAccountIdAndCommentId(Integer accountId, Integer commentId);
}
