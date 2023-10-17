package com.example.demo.request;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.type.FriendshipStatusType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountFriendshipRequest extends CustomPageRequest {
	@NotNull
	private FriendshipStatusType status;
	
	@NotNull
	private String search = "";
	
	private List<Integer> excludeIds = new ArrayList<>();
}
