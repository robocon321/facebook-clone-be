package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.VideoArticleEntity;

@Repository
public interface VideoArticleRepository extends JpaRepository<VideoArticleEntity, Integer> {

}
