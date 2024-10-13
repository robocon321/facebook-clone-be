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
@Table(name = "comment")
public class CommentArticleEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer commentId;

	@Column(name = "article_id", nullable = false)
	private Integer articleId;

	@Column(name = "account_id", nullable = false)
	private Integer accountId;

	@Column(nullable = false, columnDefinition = "NVARCHAR(1000)")
	private String text;

	@Column(nullable = false, columnDefinition = "VARCHAR(100)")
	private String mentionedAccounts;

	@Column(name = "parent_id")
	private Integer parentId;

	@Column(nullable = false)
	private Timestamp createTime;

	@Column(nullable = false)
	private Timestamp modTime;

	@Column(name = "file_id")
	private Integer fileId;

	@Transient
	private DeleteStatusType status;

	@Column(nullable = false, name = "status", columnDefinition = "CHAR(1)")
	private Character statusValue;

	@PrePersist
	@PreUpdate
	void fillGenderPersistent() {
		this.statusValue = status.getStatus();
	}

	@PostLoad
	void fillGenderTransient() {
		this.status = DeleteStatusType.of(statusValue);
	}
}
