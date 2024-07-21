package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.AccountEntity;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {
	@Query(value = "SELECT * FROM account u WHERE u.email = :username OR u.phone = :username", nativeQuery = true)
	Optional<AccountEntity> findByUsername(@Param("username") String username);

	@Query(value = "SELECT * FROM account WHERE status = 'A' AND CONCAT(first_name, ' ', last_name) LIKE %:search% AND account_id IN ("
			+ " SELECT CASE WHEN receiver_id = :current_id THEN sender_id ELSE receiver_id END AS friend_id"
			+ " FROM friendship WHERE status = :status AND (receiver_id = :current_id OR sender_id = :current_id)"
			+ ") AND account_id NOT IN :ids", nativeQuery = true)
	Page<AccountEntity> findByCurrentIdAndFriendshipStatus(@Param("current_id") Integer currentId,
			@Param("status") Character status, @Param("search") String search, @Param("ids") List<Integer> excludeIds,
			Pageable pageable);

	@Query(value = "SELECT * FROM account WHERE status = 'A' AND CONCAT(first_name, ' ', last_name) LIKE %:search% AND account_id IN ("
			+ " SELECT CASE WHEN receiver_id = :current_id THEN sender_id ELSE receiver_id END AS friend_id"
			+ " FROM friendship WHERE status = :status AND (receiver_id = :current_id OR sender_id = :current_id)"
			+ ")", nativeQuery = true)
	Page<AccountEntity> findByCurrentIdAndFriendshipStatus(@Param("current_id") Integer currentId,
			@Param("status") Character status, @Param("search") String search, Pageable pageable);

	@Query(value = "SELECT * FROM account WHERE status = 'A' AND CONCAT(first_name, ' ', last_name) LIKE %:search% AND account_id IN ("
			+ " SELECT receiver_id FROM friendship WHERE status = :status AND sender_id = :sender_id"
			+ ") AND account_id NOT IN :ids", nativeQuery = true)
	Page<AccountEntity> findByReceiverIdAndFriendshipStatus(@Param("sender_id") Integer senderId,
			@Param("status") Character status, @Param("search") String search, @Param("ids") List<Integer> excludeIds,
			Pageable pageable);

	@Query(value = "SELECT * FROM account WHERE status = 'A' AND CONCAT(first_name, ' ', last_name) LIKE %:search% AND account_id IN ("
			+ " SELECT receiver_id FROM friendship WHERE status = :status AND sender_id = :sender_id"
			+ ")", nativeQuery = true)
	Page<AccountEntity> findByReceiverIdAndFriendshipStatus(@Param("sender_id") Integer senderId,
			@Param("status") Character status, @Param("search") String search, Pageable pageable);

	@Query(value = "SELECT * FROM account WHERE status = 'A' AND CONCAT(first_name, ' ', last_name) LIKE %:search% AND account_id IN ("
			+ " SELECT sender_id FROM friendship WHERE status = :status AND receiver_id = :receiver_id"
			+ ") AND account_id NOT IN :ids", nativeQuery = true)
	Page<AccountEntity> findBySenderIdAndFriendshipStatus(@Param("receiver_id") Integer receiverId,
			@Param("status") Character status, @Param("search") String search, @Param("ids") List<Integer> excludeIds,
			Pageable pageable);

	@Query(value = "SELECT * FROM account WHERE status = 'A' AND CONCAT(first_name, ' ', last_name) LIKE %:search% AND account_id IN ("
			+ " SELECT sender_id FROM friendship WHERE status = :status AND receiver_id = :receiver_id"
			+ ")", nativeQuery = true)
	Page<AccountEntity> findBySenderIdAndFriendshipStatus(@Param("receiver_id") Integer receiverId,
			@Param("status") Character status, @Param("search") String search, Pageable pageable);

	@Query(value = "SELECT * FROM account WHERE status = 'A' AND CONCAT(first_name, ' ', last_name) LIKE %:search% AND NOT EXISTS ("
			+ "	SELECT * FROM friendship where (receiver_id = account_id AND sender_id = :current_id) OR (receiver_id = :current_id AND sender_id = account_id)"
			+ " ) OR EXISTS ("
			+ "	SELECT * FROM friendship f where (receiver_id = account_id AND sender_id = :current_id AND f.status IN ('C', 'R'))"
			+ " OR (receiver_id = :current_id AND sender_id = account_id AND f.status IN ('C', 'R'))"
			+ " ) AND account_id NOT IN :ids", nativeQuery = true)
	Page<AccountEntity> recommendAccount(@Param("current_id") Integer currentId, @Param("search") String search,
			@Param("ids") List<Integer> excludeIds, Pageable pageable);

	@Query(value = "SELECT * FROM account WHERE status = 'A' AND account_id <> :current_id AND CONCAT(first_name, ' ', last_name) LIKE %:search% AND NOT EXISTS ("
			+ "	SELECT * FROM friendship where (receiver_id = account_id AND sender_id = :current_id) OR (receiver_id = :current_id AND sender_id = account_id)"
			+ " ) OR EXISTS ("
			+ "	SELECT * FROM friendship f where (receiver_id = account_id AND sender_id = :current_id AND f.status IN ('C', 'R'))"
			+ " OR (receiver_id = :current_id AND sender_id = account_id AND f.status IN ('C', 'R')))", nativeQuery = true)
	Page<AccountEntity> recommendAccount(@Param("current_id") Integer currentId, @Param("search") String search,
			Pageable pageable);

	@Query(value = "SELECT * FROM account WHERE status = 'A' AND account_id <> :current_id AND account_id IN ("
			+ "	SELECT CASE WHEN receiver_id = :current_id THEN sender_id ELSE receiver_id END AS friend_id"
			+ "	FROM friendship WHERE (receiver_id = :current_id OR sender_id = :current_id)"
			+ ") AND account_id IN ("
			+ "	SELECT CASE WHEN receiver_id = :search_id THEN sender_id ELSE receiver_id END AS friend_id"
			+ "	FROM friendship WHERE (receiver_id = :search_id OR sender_id = :search_id)"
			+ ")", nativeQuery = true)
	List<AccountEntity> findManualFriends(@Param("current_id") Integer currentId, @Param("search_id") Integer searchId);

}
