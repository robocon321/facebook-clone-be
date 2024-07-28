package com.example.demo.response;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VideoArticleResponse {
	private Integer videoId;	
	private String text;
	private Timestamp createTime;
	private Timestamp modTime;
	private String fileUrl;
}
