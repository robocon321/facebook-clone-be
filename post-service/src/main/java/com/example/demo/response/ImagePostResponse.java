package com.example.demo.response;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImagePostResponse {
	private Integer imagePostId;
	private String text;
	private Timestamp createTime;
	private Timestamp modTime;
	private String fileUrl;	
    private List<TextImagePostResponse> textImagePosts;
    private List<TagImagePostResponse> tagImagePosts;
}
