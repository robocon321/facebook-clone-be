package com.example.demo.request;

import com.example.demo.type.ActionHistoryStatusType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FriendHistoryRequest extends CustomPageRequest {
	private ActionHistoryStatusType type;
}
