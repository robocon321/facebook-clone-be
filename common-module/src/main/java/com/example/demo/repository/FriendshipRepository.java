package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FriendshipEntity;
import com.example.demo.type.FriendshipStatusType;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Integer> {
    @Query(value = "SELECT * FROM friendship u WHERE (u.receiver_id = :receiver_id AND u.sender_id = :sender_id) OR (u.receiver_id = :sender_id AND u.sender_id = :receiver_id)", nativeQuery = true)
    Optional<FriendshipEntity> customFindByReceiverIdAndSenderId(@Param("receiver_id") Integer receiverId, @Param("sender_id") Integer senderId);
}
