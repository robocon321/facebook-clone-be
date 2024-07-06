package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PostEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer> {
	@Query(value = "SELECT * FROM post WHERE account_id IN("
			+ "	SELECT CASE WHEN receiver_id = :account_id THEN sender_id ELSE receiver_id END AS friend_id"
			+ "	FROM friendship WHERE status = 'A' AND (receiver_id = :account_id OR sender_id = :account_id)"
			+ ") OR account_id = :account_id", nativeQuery = true)
	Page<PostEntity> recommendPost(@Param("account_id") Integer accountId, Pageable pageable);
	
}
