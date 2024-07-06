package com.example.demo.dto.response;

import java.sql.Timestamp;

import com.example.demo.type.ActionHistoryStatusType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActionHistoryResponse {
	private Integer historyId;
	private Timestamp actionTime;
	private String deviceInfo;
	private ActionHistoryStatusType status;
}
