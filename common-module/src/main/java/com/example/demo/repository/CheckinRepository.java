package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CheckinEntity;

@Repository
public interface CheckinRepository extends JpaRepository<CheckinEntity, Integer> {
	@Query(value = "SELECT * FROM checkin WHERE country LIKE CONCAT('%', :search, '%') OR address LIKE CONCAT('%', :search, '%') OR city LIKE CONCAT('%', :search, '%')", nativeQuery = true)
	Page<CheckinEntity> searchCheckin(@Param("search") String search, Pageable pageable);
}
