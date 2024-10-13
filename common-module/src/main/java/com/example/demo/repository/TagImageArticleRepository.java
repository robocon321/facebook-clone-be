package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.AccountEntity;
import com.example.demo.entity.TagImageArticleEntity;

@Repository
public interface TagImageArticleRepository extends JpaRepository<TagImageArticleEntity, Integer> {
    public List<TagImageArticleEntity> findAllByImageArticleId(Integer imageArticleId);
}
