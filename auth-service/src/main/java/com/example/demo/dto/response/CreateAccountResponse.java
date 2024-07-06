package com.example.demo.dto.response;

import java.sql.Date;

import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.GenderType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountResponse {
	private Integer accountId;
	private String email;
	private String phone;
	private String firstName;
	private String lastName;
	private Date birthday;
	private GenderType gender;
	private String profilePictureUrl;
	private String coverPhotoUrl;
	private String bio;
	private String location;
	private String website;
	private DeleteStatusType status;

}
