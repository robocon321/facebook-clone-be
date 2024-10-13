package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "checkin")
public class CheckinEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer checkinId;

	@Column(nullable = false, columnDefinition = "VARCHAR(50)")
	private String longitude;

	@Column(nullable = false, columnDefinition = "VARCHAR(50)")
	private String latitude;

	@Column(columnDefinition = "NVARCHAR(50)")
	private String city;

	@Column(nullable = false, columnDefinition = "NVARCHAR(50)")
	private String country;

	@Column(columnDefinition = "NVARCHAR(50)")
	private String address;
}
