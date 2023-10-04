package com.example.demo.type;

import java.util.stream.Stream;

public enum FriendshipStatus {
	PENDING('P'),
	ACCEPTED('A'),
	REJECTED('R'),
	CANCEL('C'),
	BLOCK('B');
	
	private Character status;
	
	FriendshipStatus(char status) {
		this.setStatus(status);
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}
	
    public static FriendshipStatus of(Character status) {
        return Stream.of(FriendshipStatus.values())
          .filter(p -> p.getStatus() == status)
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }

}
