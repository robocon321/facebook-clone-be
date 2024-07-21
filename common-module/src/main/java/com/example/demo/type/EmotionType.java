package com.example.demo.type;

import java.util.Objects;
import java.util.stream.Stream;

public enum EmotionType {
	LIKE('L'),
	HEART('H'),
	LOVE('V'),
	HAHA('A'),
	WOW('W'),
	CRY('C'),
	ANGRY('R');

	private Character emotion;

	EmotionType(char emotion) {
		this.emotion = emotion;
	}

	public Character getEmotion() {
		return emotion;
	}

	public static EmotionType of(Character emotion) {
		return Stream.of(EmotionType.values())
				.filter(p -> Objects.equals(p.getEmotion(), emotion))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

}
