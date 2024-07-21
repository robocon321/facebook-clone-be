package com.example.demo.type;

import java.util.Objects;
import java.util.stream.Stream;

public enum FileStatusType {
	ACTIVE('A'),
	NONACTIVE('N');

	private Character status;

	FileStatusType(char status) {
		this.setStatus(status);
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	public static FileStatusType of(Character status) {
		return Stream.of(FileStatusType.values())
				.filter(p -> Objects.equals(p.getStatus(), status))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

}
