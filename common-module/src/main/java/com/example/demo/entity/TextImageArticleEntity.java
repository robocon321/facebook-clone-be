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
@Table(name = "text_image_article")
public class TextImageArticleEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer textId;

	@Column(nullable = false)
	private String text;

	@Column(nullable = false)
	private Double xPos;

	@Column(nullable = false)
	private Double yPos;

	@Column(nullable = false)
	private String color;

	@Column(nullable = false)
	private Double size;

	@Column(nullable = false, name = "image_article_id")
	private Integer imageArticleId;
}
