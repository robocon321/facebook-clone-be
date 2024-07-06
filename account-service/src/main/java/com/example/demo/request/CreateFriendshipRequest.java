package com.example.demo.request;

import com.example.demo.type.FriendshipStatusType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateFriendshipRequest {
	@NotNull(message = "Receiver is not null")
	private Integer receiverId;
	
	@NotNull(message = "Status is not null")
	private FriendshipStatusType status;
}
