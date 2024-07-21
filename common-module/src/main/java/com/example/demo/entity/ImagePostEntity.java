package com.example.demo.entity;

import java.sql.Timestamp;
import java.util.List;

import com.example.demo.type.DeleteStatusType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "`image_post`")
@Builder
public class ImagePostEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer imagePostId;

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

	@OneToOne
	@JoinColumn(nullable = false, name = "file_id")
	private FileEntity file;

	@ManyToOne
	@JoinColumn(nullable = false, name = "post_id")
	private PostEntity post;

	@OneToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY, mappedBy = "imagePost")
	private List<TextImagePostEntity> textImagePosts;

	@OneToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY, mappedBy = "imagePost")
	private List<TagImagePostEntity> tagImagePosts;
}
