package com.example.demo.response;

import java.sql.Timestamp;

import com.example.demo.type.FriendshipStatusType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FriendshipResponse {
	private Integer friendshipId;
	private Integer receiverId;
	private FriendshipStatusType status;
	private Timestamp requestTime;
}
