package com.example.demo.type;

import java.util.stream.Stream;

public enum GenderType {
	MALE('M'),
	FEMALE('F'),
	OTHER('O');
	
	private Character gender;

	GenderType(char gender) {
		this.setGender(gender);
	}

	public Character getGender() {
		return gender;
	}

	public void setGender(Character gender) {
		this.gender = gender;
	}
	
    public static GenderType of(Character gender) {
        return Stream.of(GenderType.values())
          .filter(p -> p.getGender() == gender)
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }
}
