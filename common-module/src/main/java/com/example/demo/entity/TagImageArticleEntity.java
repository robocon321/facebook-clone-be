package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tag_image_article")
public class TagImageArticleEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer tagId;

	@Column(nullable = false)
	private Double xPos;

	@Column(nullable = false)
	private Double yPos;

	@Column(nullable = false)
	private Integer accountId;

	@Column(nullable = false, name = "image_article_id")
	private Integer imageArticleId;
}
