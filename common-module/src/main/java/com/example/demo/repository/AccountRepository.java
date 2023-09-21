package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>{
    @Query(value = "SELECT * FROM account u WHERE u.email = :username OR u.phone = :username", nativeQuery = true)
    Optional<Account> findByUsername(@Param("username") String username);    
}
