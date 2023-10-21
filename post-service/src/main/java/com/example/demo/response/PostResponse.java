package com.example.demo.response;

import java.sql.Timestamp;
import java.util.List;

import com.example.demo.entity.CheckinEntity;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.PostScopeType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostResponse {
	private Integer postId;
	private String text;	
	private Integer emotionId;
	private Timestamp createTime;
	private Timestamp modTime;
	private PostScopeType scope;
    private AccountResponse account;
    private CheckinResponse checkin;
    private List<AccountResponse> tags;
    private List<ImagePostResponse> images;
    private List<VideoPostResponse> videos;
    private List<EmotionPostResponse> emotions;
}
