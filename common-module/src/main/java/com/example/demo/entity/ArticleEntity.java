package com.example.demo.entity;

import java.sql.Timestamp;

import com.example.demo.type.ArticleScopeType;
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
@Builder
@Entity
@Table(name = "article")
public class ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer articleId;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String text;

    private Integer emotionId;

    @Column(nullable = false)
    private Timestamp createTime;

    @Column(nullable = false)
    private Timestamp modTime;

    @Transient
    private ArticleScopeType scope;

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
        this.scope = ArticleScopeType.of(scopeValue);
    }

    @Column(nullable = false, name = "account_id")
    private Integer accountId;

    @Column(name = "checkin_id")
    private Integer checkinId;
}
