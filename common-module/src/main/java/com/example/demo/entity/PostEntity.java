package com.example.demo.entity;

import java.sql.Timestamp;
import java.util.List;

import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.PostScopeType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Builder
@Entity
@Table(name = "post")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String text;

    private Integer emotionId;

    @Column(nullable = false)
    private Timestamp createTime;

    @Column(nullable = false)
    private Timestamp modTime;

    @Transient
    private PostScopeType scope;

    @Column(nullable = false, name = "scope", columnDefinition = "CHAR(1)")
    private Character scopeValue;

    @Transient
    private DeleteStatusType status;

    @Column(nullable = false, name = "status", columnDefinition = "CHAR(1)")
    private Character statusValue;

    @PrePersist
    void fillGenderPersistent() {
        this.statusValue = status.getStatus();
        this.scopeValue = scope.getScope();
    }

    @PostLoad
    void fillGenderTransient() {
        this.status = DeleteStatusType.of(statusValue);
        this.scope = PostScopeType.of(scopeValue);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "account_id")
    private AccountEntity account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkin_id")
    private CheckinEntity checkin;

    @ManyToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
    @JoinTable(name = "tag_post", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "account_id"))
    private List<AccountEntity> tags;

    @OneToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY, mappedBy = "post")
    private List<ImagePostEntity> imagePosts;

    @OneToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY, mappedBy = "post")
    private List<VideoPostEntity> videoPosts;

    @OneToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY, mappedBy = "post")
    private List<CommentPostEntity> comments;

    @OneToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY, mappedBy = "post")
    private List<EmotionPostEntity> emotions;
}
