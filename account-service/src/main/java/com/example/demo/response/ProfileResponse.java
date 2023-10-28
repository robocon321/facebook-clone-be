package com.example.demo.response;

import java.sql.Date;
import java.util.List;

import com.example.demo.entity.CommentPostEntity;
import com.example.demo.entity.EmotionCommentEntity;
import com.example.demo.entity.EmotionPostEntity;
import com.example.demo.entity.FriendshipEntity;
import com.example.demo.entity.LoginHistoryEntity;
import com.example.demo.entity.PostEntity;
import com.example.demo.entity.TagImagePostEntity;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.GenderType;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileResponse {
	private Integer accountId;
	private String email;
	private String phone;
	private String firstName;
	private String lastName;
	private Date birthdate;
	private GenderType gender;
	private String profilePictureUrl;
	private String coverPhotoUrl;
	private String bio;
	private String location;
	private String website;
	private DeleteStatusType status;
}
