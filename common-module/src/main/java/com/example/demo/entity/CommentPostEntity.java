package com.example.demo.entity;

import java.sql.Timestamp;
import java.util.List;

import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.FileStatusType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class CommentPostEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer commentId;
	
	@ManyToOne
	@JoinColumn(name = "post_id", nullable = false)
	private PostEntity post;
	
	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	private AccountEntity account;
	
	@Column(nullable = false, columnDefinition = "NVARCHAR(1000)")
	private String text;
	
	@Column(nullable = false, columnDefinition = "VARCHAR(100)")
	private String mentionedAccounts;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	private CommentPostEntity parent;	
	
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<CommentPostEntity> children;
	
	@Column(nullable = false)
	private Timestamp createTime;

	@Column(nullable = false)
	private Timestamp modTime;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "file_id")
	private FileEntity file;
	
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
    
    @OneToMany(mappedBy = "comment")
    private List<EmotionCommentEntity> emotions;
}
