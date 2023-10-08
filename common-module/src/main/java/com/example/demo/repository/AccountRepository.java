package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
	@Query(value = "SELECT * FROM account u WHERE u.email = :username OR u.phone = :username", nativeQuery = true)
	Optional<Account> findByUsername(@Param("username") String username);

	@Query(value = "SELECT * FROM account WHERE CONCAT(first_name, ' ', last_name) LIKE %:search% AND account_id IN ("
			+ " SELECT CASE WHEN receiver_id = :current_id THEN sender_id ELSE receiver_id END AS friend_id"
			+ " FROM friendship WHERE status = :status AND (receiver_id = :current_id OR sender_id = :current_id)"
			+ ") AND account_id NOT IN :ids", nativeQuery = true)
	Page<Account> findByCurrentIdAndFriendshipStatus(@Param("current_id") Integer currentId,
			@Param("status") Character status, @Param("search") String search, @Param("ids") List<Integer> excludeIds, Pageable pageable);

	@Query(value = "SELECT * FROM account WHERE CONCAT(first_name, ' ', last_name) LIKE %:search% AND account_id IN ("
			+ " SELECT CASE WHEN receiver_id = :current_id THEN sender_id ELSE receiver_id END AS friend_id"
			+ " FROM friendship WHERE status = :status AND (receiver_id = :current_id OR sender_id = :current_id)"
			+ ")", nativeQuery = true)
	Page<Account> findByCurrentIdAndFriendshipStatus(@Param("current_id") Integer currentId,
			@Param("status") Character status, @Param("search") String search, Pageable pageable);

}
