package com.example.demo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AcceptFriendshipRequest {
	@NotNull(message = "Friendhip id is not null")
	private Integer friendshipId;
}
