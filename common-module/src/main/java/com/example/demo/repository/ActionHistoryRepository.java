package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ActionHistoryEntity;
import com.example.demo.type.ActionHistoryStatusType;

@Repository
public interface ActionHistoryRepository extends JpaRepository<ActionHistoryEntity, Integer> {    
	Optional<ActionHistoryEntity> findFirstByAccountAccountIdOrderByActionTimeDesc(Integer accountId);
	Optional<ActionHistoryEntity> findFirstByAccountAccountIdAndStatusValueOrderByActionTimeDesc(Integer accountId, Character statusValue);
}
