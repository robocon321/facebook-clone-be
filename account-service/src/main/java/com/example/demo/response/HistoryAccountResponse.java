package com.example.demo.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HistoryAccountResponse {
	private AccountResponse account;
	private ActionHistoryResponse currentHistory;
}
