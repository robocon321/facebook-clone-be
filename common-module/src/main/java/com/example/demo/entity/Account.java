package com.example.demo.entity;

import java.sql.Date;

import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.GenderType;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Entity
@Table(name = "account")
@Builder
public class Account {	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer accountId;
	
	@Column(unique = true, length = 100)
	private String email;

	@Column(unique = true, length = 15)
	private String phone;

	@Column(nullable = false, length = 100)
	private String password;
	
	@Column(nullable = false, length = 200)
	private String firstName;
	
	@Column(nullable = false, length = 200)
	private String lastName;
	
	@Column(nullable = false)
	private Date birthdate;
	
	@Transient
	private GenderType gender;
	
	@Column(nullable = false, name = "gender", columnDefinition = "CHAR(1)")
    private Character genderValue;
    
	@Column(length = 2000)
	private String profilePictureUrl;
	
	@Column(length = 2000)
	private String coverPhotoUrl;
	
	@Column(length = 100)
	private String bio;
	
	@Column(length = 2)
	private String location;
	
	@Column(length = 100)
	private String website;
	
	@Transient
	private DeleteStatusType status;
	
	@Column(nullable = false, name = "status", columnDefinition = "CHAR(1)")	
	private Character statusValue;

    @PrePersist
    void fillGenderPersistent() {
        this.genderValue = gender.getGender();
        this.statusValue = status.getStatus();
    }

    @PostLoad
    void fillGenderTransient() {
        this.gender = GenderType.of(genderValue);
        this.status = DeleteStatusType.of(statusValue);
    }
}
