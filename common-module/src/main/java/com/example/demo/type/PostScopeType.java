package com.example.demo.type;

import java.util.Objects;
import java.util.stream.Stream;

public enum PostScopeType {
	PUBLIC('P'),
	FRIEND('F'),
	ME('M');

	private Character scope;

	PostScopeType(char scope) {
		this.scope = scope;
	}

	public Character getScope() {
		return scope;
	}

	public void setScope(Character scope) {
		this.scope = scope;
	}

	public static PostScopeType of(Character scope) {
		return Stream.of(PostScopeType.values())
				.filter(p -> Objects.equals(p.getScope(), scope))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
}
