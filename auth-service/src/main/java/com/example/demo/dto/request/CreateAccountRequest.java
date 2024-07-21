package com.example.demo.dto.request;

import java.sql.Date;

import com.example.demo.annotation.ValidName;
import com.example.demo.annotation.ValidPhone;
import com.example.demo.type.GenderType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountRequest {
	@Email(message = "Invalid email address")
	private String email;

	@ValidPhone
	private String phone;

	@Min(value = 5, message = "Password >= 5")
	private String password;

	@ValidName(message = "Invalid firstname")
	private String firstName;

	@ValidName(message = "Invalid lastname")
	private String lastName;

	@NotNull(message = "Birthdate must be not null")
	private Date birthdate;

	@NotNull(message = "Gender must be not null")
	private GenderType gender;
}
