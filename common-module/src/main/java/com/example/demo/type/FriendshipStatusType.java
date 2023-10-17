package com.example.demo.type;

import java.util.stream.Stream;

public enum FriendshipStatusType {
	PENDING('P'),
	ACCEPTED('A'),
	REJECTED('R'),
	CANCEL('C'),
	BLOCK('B');
	
	private Character status;
	
	FriendshipStatusType(char status) {
		this.setStatus(status);
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}
	
    public static FriendshipStatusType of(Character status) {
        return Stream.of(FriendshipStatusType.values())
          .filter(p -> p.getStatus() == status)
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }

}
