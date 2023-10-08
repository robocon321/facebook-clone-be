package com.example.demo.request;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.type.FriendshipStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountFriendshipRequest extends CustomPageRequest {
	@NotNull
	private FriendshipStatus status;
	
	@NotNull
	private String search = "";
	
	private List<Integer> excludeIds = new ArrayList<>();
}
