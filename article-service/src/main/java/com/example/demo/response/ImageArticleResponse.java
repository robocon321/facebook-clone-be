package com.example.demo.response;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageArticleResponse {
	private Integer imageArticleId;
	private String text;
	private Timestamp createTime;
	private Timestamp modTime;
	private String fileUrl;	
    private List<TextImageArticleResponse> textImageArticles;
    private List<TagImageArticleResponse> tagImageArticles;
}
