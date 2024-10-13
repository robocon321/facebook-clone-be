package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ImageArticleEntity;
import java.util.List;

@Repository
public interface ImageArticleRepository extends JpaRepository<ImageArticleEntity, Integer> {
    List<ImageArticleEntity> findByArticleId(Integer articleId);
}
