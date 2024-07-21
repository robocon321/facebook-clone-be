package com.example.demo.request;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.type.FriendshipStatusType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AccountFriendshipRequest extends CustomPageRequest {
	@NotNull
	private FriendshipStatusType status;

	@NotNull
	private String search = "";

	private List<Integer> excludeIds = new ArrayList<>();
}
