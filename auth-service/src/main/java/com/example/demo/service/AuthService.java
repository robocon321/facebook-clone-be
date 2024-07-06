package com.example.demo.service;


import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.config.CustomUserDetails;
import com.example.demo.dto.request.CreateAccountRequest;
import com.example.demo.dto.response.ActionHistoryResponse;
import com.example.demo.dto.response.CreateAccountResponse;
import com.example.demo.entity.AccountEntity;
import com.example.demo.entity.ActionHistoryEntity;
import com.example.demo.exception.BlockException;
import com.example.demo.provider.JwtProvider;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.ActionHistoryRepository;
import com.example.demo.type.ActionHistoryStatusType;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.utils.HandleStringUtils;

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
    
    @Autowired
    private ActionHistoryRepository actionHistoryRepository;
    
    public CreateAccountResponse saveAccount(CreateAccountRequest request) throws RuntimeException {
    	AccountEntity entity = new AccountEntity();
    	BeanUtils.copyProperties(request, entity);
    	entity.setStatus(DeleteStatusType.ACTIVE);
    	entity.setWebsite(HandleStringUtils.generateRandomString(10));
        repository.save(entity);
        updateHistory(entity, ActionHistoryStatusType.REGISTER);
        CreateAccountResponse response = new CreateAccountResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    public String generateToken(CustomUserDetails userDetails) throws RuntimeException {
		if(userDetails.getAccount().getStatus() == DeleteStatusType.INACTIVE) throw new BlockException("Your account is blocked");
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpiration);
		updateHistory(userDetails.getAccount(), ActionHistoryStatusType.LOGIN);
		return Jwts.builder()
				.setSubject(Long.toString(userDetails.getAccount().getAccountId()))
				.setIssuedAt(expiryDate)
				.signWith(SignatureAlgorithm.HS256, jwtSecret)		
				.compact();    
	}

    public boolean validateToken(String token) {
       return jwtProvider.validateToken(token);
    }

    private void updateHistory(AccountEntity account, ActionHistoryStatusType type) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		ActionHistoryEntity actionHistory = ActionHistoryEntity.builder()
				.account(account)
				.actionTime(now)
				.status(type)
				.build();
		
		ActionHistoryEntity newActionHistory = actionHistoryRepository.save(actionHistory);
		ActionHistoryResponse historyResponse = new ActionHistoryResponse();
		BeanUtils.copyProperties(newActionHistory, historyResponse);    	
    }

}
