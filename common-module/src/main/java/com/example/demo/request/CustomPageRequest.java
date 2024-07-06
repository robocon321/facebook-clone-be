package com.example.demo.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomPageRequest {
	private Integer page = 0;
	private Integer size = 10;
	private String[] sortBy;
	private String[] sortOrder;
}
