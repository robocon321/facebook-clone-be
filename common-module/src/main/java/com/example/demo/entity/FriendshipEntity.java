package com.example.demo.entity;

import java.sql.Timestamp;

import com.example.demo.type.FriendshipStatusType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`friendship`")
@Builder
public class FriendshipEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer friendshipId;

	@Column(name = "sender_id", nullable = false)
	private Integer senderId;

	@Column(name = "receiver_id", nullable = false)
	private Integer receiverId;

	@Transient
	private FriendshipStatusType status;

	@Column(nullable = false, name = "status", columnDefinition = "CHAR(1)")
	private Character statusValue;

	@PrePersist
	@PreUpdate
	void fillGenderPersistent() {
		this.statusValue = status.getStatus();
	}

	@PostLoad
	void fillGenderTransient() {
		this.status = FriendshipStatusType.of(statusValue);
	}

	@Column(nullable = false)
	private Timestamp requestTime;
}
