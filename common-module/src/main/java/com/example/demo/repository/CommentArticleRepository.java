package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CommentArticleEntity;

@Repository
public interface CommentArticleRepository extends JpaRepository<CommentArticleEntity, Integer> {
	List<CommentArticleEntity> findAllByArticleId(Integer articleId);
}
