package com.example.demo.entity;

import java.sql.Timestamp;

import com.example.demo.type.ActionHistoryStatusType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Builder
@Entity
@Table(name = "`action_history`")
public class ActionHistoryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer historyId;

	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	private AccountEntity account;

	@Column(nullable = false)
	private Timestamp actionTime;

	@Column(length = 45)
	private String deviceInfo;

	@Transient
	private ActionHistoryStatusType status;

	@Column(nullable = false, name = "status", columnDefinition = "CHAR(1)")
	private Character statusValue;

	@PrePersist
	@PreUpdate
	void fillGenderPersistent() {
		this.statusValue = status.getStatus();
	}

	@PostLoad
	void fillGenderTransient() {
		this.status = ActionHistoryStatusType.of(statusValue);
	}
}
