package com.example.demo.entity;

import java.sql.Timestamp;

import com.example.demo.type.LoginHistoryStatusType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "`login_history`")
public class LoginHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer loginId;

	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;
	
	@Column(nullable = false)
	private Timestamp loginTime;
	
	@Column(nullable = false, length = 45)
	private String deviceInfo;
	
	@Column(nullable = false)
	private LoginHistoryStatusType status; 
}
