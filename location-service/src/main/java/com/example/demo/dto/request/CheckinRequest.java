package com.example.demo.dto.request;

import com.example.demo.request.CustomPageRequest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CheckinRequest extends CustomPageRequest {	
	private String search = "";
}
