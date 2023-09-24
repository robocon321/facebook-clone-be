package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Account;
import com.example.demo.exception.BlockAccountException;
import com.example.demo.exception.NotFoundAccountException;
import com.example.demo.provider.JwtProvider;
import com.example.demo.repository.AccountRepository;
import com.example.demo.response.AccountSummaryInfoResponse;
import com.example.demo.type.DeleteStatusType;

@Service
public class AccountService {
	@Autowired
	private JwtProvider jwtProvider;
	
	@Autowired
	private AccountRepository accountRepository;
	
	public AccountSummaryInfoResponse getSummaryInfo(String token) {		
		Integer id = jwtProvider.getAccountIdFromJWT(token);
		Optional<Account> accountOpt = accountRepository.findById(id);
		if(accountOpt.isEmpty()) throw new NotFoundAccountException("Your account does not exists");
		Account account = accountOpt.get();
		if(account.getStatus() == DeleteStatusType.INACTIVE) throw new BlockAccountException("Your account is blocked. Please contact us to active your account");	
		AccountSummaryInfoResponse response = new AccountSummaryInfoResponse();
		BeanUtils.copyProperties(account, response);
		return response;
	}
}
