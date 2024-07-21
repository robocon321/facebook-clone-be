package com.example.demo.type;

import java.util.Objects;
import java.util.stream.Stream;

public enum ActionHistoryStatusType {
	ONLINE('O'),
	OFFLINE('F'),
	LOGIN('L'),
	LOGOUT('U'),
	REGISTER('R');

	private Character status;

	ActionHistoryStatusType(char status) {
		this.status = status;
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	public static ActionHistoryStatusType of(Character status) {
		return Stream.of(ActionHistoryStatusType.values())
				.filter(p -> Objects.equals(p.getStatus(), status))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

}
