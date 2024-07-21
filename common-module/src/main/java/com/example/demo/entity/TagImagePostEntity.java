package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "tag_image_post")
public class TagImagePostEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer tagId;

	@Column(nullable = false)
	private Double xPos;

	@Column(nullable = false)
	private Double yPos;

	@ManyToOne
	@JoinColumn(nullable = false, name = "account_id")
	private AccountEntity account;

	@ManyToOne
	@JoinColumn(nullable = false, name = "image_post_id")
	private ImagePostEntity imagePost;
}
