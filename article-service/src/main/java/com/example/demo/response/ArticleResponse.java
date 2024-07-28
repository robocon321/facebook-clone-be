package com.example.demo.response;

import java.sql.Timestamp;
import java.util.List;

import com.example.demo.type.ArticleScopeType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticleResponse {
    private Integer articleId;
    private String text;
    private Integer emotionId;
    private Timestamp createTime;
    private Timestamp modTime;
    private ArticleScopeType scope;
    private AccountResponse account;
    private CheckinResponse checkin;
    private List<AccountResponse> tags;
    private List<ImageArticleResponse> images;
    private List<VideoArticleResponse> videos;
    private List<EmotionArticleResponse> emotions;
    private List<CommentArticleResponse> comments;
}
