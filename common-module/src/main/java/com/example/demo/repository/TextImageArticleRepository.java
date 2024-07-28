package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.TextImageArticleEntity;

@Repository
public interface TextImageArticleRepository extends JpaRepository<TextImageArticleEntity, Integer> {

}
