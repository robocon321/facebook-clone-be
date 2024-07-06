package com.example.demo.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecommendFriendshipRequest extends CustomPageRequest {
	@NotNull
	private String search = "";

	private List<Integer> excludeIds = new ArrayList<>();
}
