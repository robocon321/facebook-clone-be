package com.example.demo.repository;

import com.example.demo.entity.TagArticleEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagArticleRepository extends JpaRepository<TagArticleEntity, Integer> {
    public List<TagArticleEntity> findAllByArticleId(Integer articleId);
}
