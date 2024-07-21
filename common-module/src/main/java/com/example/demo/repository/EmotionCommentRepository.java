package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.EmotionCommentEntity;

import jakarta.transaction.Transactional;

@Repository
public interface EmotionCommentRepository extends JpaRepository<EmotionCommentEntity, Integer> {
	List<EmotionCommentEntity> findByCommentCommentId(Integer commentId);

	Optional<EmotionCommentEntity> findByAccountAccountIdAndCommentCommentId(Integer accountId, Integer commentId);

	@Transactional
	void deleteAllByAccountAccountIdAndCommentCommentId(Integer accountId, Integer commentId);
}
