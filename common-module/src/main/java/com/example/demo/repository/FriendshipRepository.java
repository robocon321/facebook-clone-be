package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Friendship;
import com.example.demo.type.FriendshipStatus;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {    
    Optional<Friendship> findByFriendshipIdAndStatusValue(Integer friendshipId, Character statusValue);    

    @Query(value = "SELECT * FROM friendship u WHERE (u.receiver_id = :receiver_id AND u.sender_id = :sender_id) OR (u.receiver_id = :sender_id AND u.sender_id = :receiver_id)", nativeQuery = true)
    Optional<Friendship> customFindByReceiverIdAndSenderId(@Param("receiver_id") Integer receiverId, @Param("sender_id") Integer senderId);
}
