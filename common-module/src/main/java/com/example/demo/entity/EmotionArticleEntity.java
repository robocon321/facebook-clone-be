package com.example.demo.entity;

import java.sql.Timestamp;

import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.EmotionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
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
@Table(name = "emotion_article")
public class EmotionArticleEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer emotionId;

	@Column(name = "article_id", nullable = false)
	private Integer articleId;

	@Column(name = "account_id", nullable = false)
	private Integer accountId;

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
