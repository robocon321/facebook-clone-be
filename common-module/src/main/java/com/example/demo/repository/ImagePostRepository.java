package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ImagePostEntity;

@Repository
public interface ImagePostRepository extends JpaRepository<ImagePostEntity, Integer> {

}
