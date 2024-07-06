package com.example.demo.entity;

import java.sql.Timestamp;

import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.EmotionType;
import com.example.demo.type.FileStatusType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
@Table(name = "emotion_post")
public class EmotionPostEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer emotionId;
	
	@ManyToOne
	@JoinColumn(name = "post_id", nullable = false)
	private PostEntity post;
	
	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	private AccountEntity account;
	
	@Column(nullable = false)
	private Timestamp createTime;

	@Column(nullable = false)
	private Timestamp modTime;
		
	@Transient
	private DeleteStatusType status;

	@Column(nullable = false, name = "status", columnDefinition = "CHAR(1)")
	private Character statusValue;

	@Transient
	private EmotionType type;

	@Column(nullable = false, name = "type", columnDefinition = "CHAR(1)")
	private Character typeValue;

	
    @PrePersist
    @PreUpdate
    void fillGenderPersistent() {
        this.statusValue = status.getStatus();
        this.typeValue = type.getEmotion(); 
     }

    @PostLoad
    void fillGenderTransient() {
        this.status = DeleteStatusType.of(statusValue);
        this.type = EmotionType.of(typeValue);
    }
    
}
