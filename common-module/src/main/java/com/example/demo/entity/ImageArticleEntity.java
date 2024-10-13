package com.example.demo.entity;

import java.sql.Timestamp;

import com.example.demo.type.DeleteStatusType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Entity
@Table(name = "`image_article`")
@Builder
public class ImageArticleEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer imageArticleId;

	private String text;

	@Column(nullable = false)
	private Timestamp createTime;

	@Column(nullable = false)
	private Timestamp modTime;

	@Transient
	private DeleteStatusType status;

	@Column(nullable = false, name = "status", columnDefinition = "CHAR(1)")
	private Character statusValue;

	@PrePersist
	void fillGenderPersistent() {
		this.statusValue = status.getStatus();
	}

	@PostLoad
	void fillGenderTransient() {
		this.status = DeleteStatusType.of(statusValue);
	}

	@Column(nullable = false, name = "file_id")
	private Integer fileId;

	@Column(nullable = false, name = "article_id")
	private Integer articleId;
}
