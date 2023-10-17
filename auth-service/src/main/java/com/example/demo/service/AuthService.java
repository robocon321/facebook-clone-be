package com.example.demo.service;


import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.config.CustomUserDetails;
import com.example.demo.dto.request.CreateAccountRequest;
import com.example.demo.dto.response.CreateAccountResponse;
import com.example.demo.entity.AccountEntity;
import com.example.demo.exception.BlockException;
import com.example.demo.provider.JwtProvider;
import com.example.demo.repository.AccountRepository;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.utils.FBStringUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class AuthService {

    @Autowired
    private AccountRepository repository;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Autowired
    private JwtProvider jwtProvider;
    
    public CreateAccountResponse saveAccount(CreateAccountRequest request) throws RuntimeException {
    	AccountEntity entity = new AccountEntity();
    	BeanUtils.copyProperties(request, entity);
    	entity.setStatus(DeleteStatusType.ACTIVE);
    	entity.setWebsite(FBStringUtils.generateRandomString(10));
        repository.save(entity);
        CreateAccountResponse response = new CreateAccountResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    public String generateToken(CustomUserDetails userDetails) throws RuntimeException {
		if(userDetails.getAccount().getStatus() == DeleteStatusType.INACTIVE) throw new BlockException("Your account is blocked");
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpiration);
		return Jwts.builder()
				.setSubject(Long.toString(userDetails.getAccount().getAccountId()))
				.setIssuedAt(expiryDate)
				.signWith(SignatureAlgorithm.HS256, jwtSecret)		
				.compact();    
	}

    public boolean validateToken(String token) {
       return jwtProvider.validateToken(token);
    }


}
