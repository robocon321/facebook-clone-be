package com.example.demo.type;


public enum LoginHistoryStatusType {
	ACTIVE('A'),
	NONACTIVE('N');
	
	private Character status;
	
	LoginHistoryStatusType(char status) {
		this.status = status;
	}
	
}
