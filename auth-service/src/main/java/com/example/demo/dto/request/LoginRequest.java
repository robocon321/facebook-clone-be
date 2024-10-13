package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
	@NotBlank(message = "Username must not be blank")
	private String username;

	@NotBlank(message = "Password must not be blank")
	private String password;
}
