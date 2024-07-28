package com.example.demo.response;

import java.sql.Timestamp;
import java.util.List;

import com.example.demo.type.DeleteStatusType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentArticleResponse {
	private Integer commentId;
	private AccountResponse account;
	private String text;
	private FileResponse file;
	private List<AccountResponse> mentions;
	private Integer parentId;
	private Timestamp createTime;
	private Timestamp modTime;
	private DeleteStatusType status;
	private List<EmotionCommentResponse> emotions;
}
