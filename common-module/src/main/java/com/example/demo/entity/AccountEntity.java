package com.example.demo.entity;

import java.sql.Date;
import java.util.List;

import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.GenderType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account")
public class AccountEntity {	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer accountId;
	
	@Column(unique = true, length = 100, nullable = false)
	private String email;

	@Column(unique = true, length = 15, nullable = false)
	private String phone;

	@Column(nullable = false, length = 100)
	private String password;
	
	@Column(nullable = false, length = 200)
	private String firstName;
	
	@Column(nullable = false, length = 200)
	private String lastName;
	
	@Column(nullable = false)
	private Date birthdate;
	
	@Transient
	private GenderType gender;
	
	@Column(nullable = false, name = "gender", columnDefinition = "CHAR(1)")
    private Character genderValue;
    
	@Column(length = 2000)
	private String profilePictureUrl;
	
	@Column(length = 2000)
	private String coverPhotoUrl;
	
	@Column(length = 100)
	private String bio;
	
	@Column(length = 2)
	private String location;
	
	@Column(nullable = false, length = 100, unique = true)
	private String website;
	
	@Transient
	private DeleteStatusType status;
	
	@Column(nullable = false, name = "status", columnDefinition = "CHAR(1)")	
	private Character statusValue;

    @PrePersist
    void fillGenderPersistent() {
        this.genderValue = gender.getGender();
        this.statusValue = status.getStatus();
    }

    @PostLoad
    void fillGenderTransient() {
        this.gender = GenderType.of(genderValue);
        this.status = DeleteStatusType.of(statusValue);
    }
    
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<LoginHistoryEntity> loginHistories;
    
    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    private List<FriendshipEntity> senders;

    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private List<FriendshipEntity> receiver;
    
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<PostEntity> tagPosts;
    
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<TagImagePostEntity> tagImagePosts;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<CommentPostEntity> comments;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<EmotionPostEntity> emotionPosts;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<EmotionCommentEntity> emotionComments;

}
