package com.example.demo.dto.response;


import lombok.Data;

@Data
public class CheckinResponse {
	private Integer checkinId;
	private String longitude;
	private String latitude;
	private String city;
	private String country;
	private String address;

}
