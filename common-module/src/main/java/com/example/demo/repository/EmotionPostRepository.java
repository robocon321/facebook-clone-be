package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.EmotionPostEntity;

import jakarta.transaction.Transactional;

@Repository
public interface EmotionPostRepository extends JpaRepository<EmotionPostEntity, Integer> {
	List<EmotionPostEntity> findByPostPostId(Integer postId);

	Optional<EmotionPostEntity> findByAccountAccountIdAndPostPostId(Integer accountId, Integer postId);

	@Transactional
	void deleteAllByAccountAccountIdAndPostPostId(Integer accountId, Integer postId);
}
