package com.example.demo.response;

import java.sql.Date;

import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.GenderType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileResponse {
	private Integer accountId;
	private String email;
	private String phone;
	private String firstName;
	private String lastName;
	private Date birthdate;
	private GenderType gender;
	private String profilePictureUrl;
	private String coverPhotoUrl;
	private String bio;
	private String location;
	private String website;
	private DeleteStatusType status;
}
