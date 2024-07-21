package com.example.demo.dto.request;

import com.example.demo.request.CustomPageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CheckinRequest extends CustomPageRequest {
	private String search = "";
}
