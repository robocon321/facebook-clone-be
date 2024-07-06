package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CommentPostEntity;

@Repository
public interface CommentPostRepository extends JpaRepository<CommentPostEntity, Integer> {
	List<CommentPostEntity> findByPostPostId(Integer postId);
}
