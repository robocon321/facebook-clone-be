package com.example.demo.type;

import java.util.Objects;
import java.util.stream.Stream;

public enum ArticleScopeType {
	PUBLIC('P'),
	FRIEND('F'),
	ME('M');

	private Character scope;

	ArticleScopeType(char scope) {
		this.scope = scope;
	}

	public Character getScope() {
		return scope;
	}

	public void setScope(Character scope) {
		this.scope = scope;
	}

	public static ArticleScopeType of(Character scope) {
		return Stream.of(ArticleScopeType.values())
				.filter(p -> Objects.equals(p.getScope(), scope))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
}
