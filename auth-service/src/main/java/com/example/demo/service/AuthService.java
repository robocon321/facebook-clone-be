package com.example.demo.service;

import java.security.Key;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.BeanUtils;
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
import com.example.demo.type.ErrorCodeType;
import com.example.demo.utils.HandleStringUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    private AccountRepository repository;
    private JwtProvider jwtProvider;
    private ActionHistoryRepository actionHistoryRepository;

    public AuthService(AccountRepository repository, JwtProvider jwtProvider,
            ActionHistoryRepository actionHistoryRepository) {
        this.repository = repository;
        this.jwtProvider = jwtProvider;
        this.actionHistoryRepository = actionHistoryRepository;
    }

    /**
     * @param request
     * @return CreateAccountResponse
     */
    public CreateAccountResponse saveAccount(CreateAccountRequest request) {
        AccountEntity entity = new AccountEntity();
        BeanUtils.copyProperties(request, entity);
        entity.setStatus(DeleteStatusType.ACTIVE);
        entity.setWebsite(HandleStringUtils.generateRandomString(10));
        repository.save(entity);
        // TODO: check exist account before save if exist then throw conflict exception
        updateHistory(entity, ActionHistoryStatusType.REGISTER);
        CreateAccountResponse response = new CreateAccountResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    public String generateToken(CustomUserDetails userDetails) {
        if (userDetails.getAccount().getStatus() == DeleteStatusType.INACTIVE)
            throw new BlockException(ErrorCodeType.ERROR_ACCOUNT_BLOCKED);
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtExpiration, ChronoUnit.MILLIS);

        updateHistory(userDetails.getAccount(), ActionHistoryStatusType.LOGIN);

        Key secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(Long.toString(userDetails.getAccount().getAccountId()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(secretKey)
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
