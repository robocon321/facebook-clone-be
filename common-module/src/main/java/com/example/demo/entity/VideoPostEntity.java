package com.example.demo.entity;

import java.sql.Timestamp;

import com.example.demo.type.DeleteStatusType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "video_post")
public class VideoPostEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer videoId;
	
	private String text;
	
	@Column(nullable = false)
	private Timestamp createTime;
	
	@Column(nullable = false)
	private Timestamp modTime;
	
	@Column(nullable = false)
	private DeleteStatusType status;
	
	@OneToOne
	@JoinColumn(nullable = false, name = "file_id")
	private FileEntity file;	
	
	@ManyToOne
	@JoinColumn(nullable = false, name = "post_id")
	private PostEntity post;	
}
