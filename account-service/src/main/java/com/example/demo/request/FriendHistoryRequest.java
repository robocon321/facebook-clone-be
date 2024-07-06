package com.example.demo.request;

import com.example.demo.type.ActionHistoryStatusType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FriendHistoryRequest extends CustomPageRequest {
	private ActionHistoryStatusType type;
}
