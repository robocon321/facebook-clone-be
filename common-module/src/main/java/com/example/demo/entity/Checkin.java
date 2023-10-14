package com.example.demo.entity;

import java.sql.Date;
import java.util.List;

import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.GenderType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
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
@Table(name = "checkin")
public class Checkin {	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer checkinId;
	
	@Column(nullable = false, columnDefinition = "VARCHAR(50)")
	private String longitude;
	
	@Column(nullable = false, columnDefinition = "VARCHAR(50)")
	private String latitude;
	
	@Column(columnDefinition = "VARCHAR(50)")
	private String city;
	
	@Column(nullable = false, columnDefinition = "VARCHAR(50)")
	private String country;
	
	@Column(columnDefinition = "VARCHAR(50)")
	private String address;
	
	
}
