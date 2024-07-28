package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ArticleEntity;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, Integer> {
	@Query(value = "SELECT * FROM article WHERE account_id IN("
			+ "	SELECT CASE WHEN receiver_id = :account_id THEN sender_id ELSE receiver_id END AS friend_id"
			+ "	FROM friendship WHERE status = 'A' AND (receiver_id = :account_id OR sender_id = :account_id)"
			+ ") OR account_id = :account_id", nativeQuery = true)
	Page<ArticleEntity> recommendArticle(@Param("account_id") Integer accountId, Pageable pageable);

}
