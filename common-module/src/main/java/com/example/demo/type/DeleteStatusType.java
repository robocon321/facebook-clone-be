package com.example.demo.type;

import java.util.stream.Stream;

public enum DeleteStatusType {
	ACTIVE('A'),
	INACTIVE('I');
	
	private Character status;
	
	DeleteStatusType(char status) {
		this.setStatus(status);
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}
	
    public static DeleteStatusType of(Character status) {
        return Stream.of(DeleteStatusType.values())
          .filter(p -> p.getStatus() == status)
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }

}
