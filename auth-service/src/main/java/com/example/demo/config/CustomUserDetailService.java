package com.example.demo.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;


@Service
public class CustomUserDetailService implements UserDetailsService {

	@Autowired
	private AccountRepository accountRepository;


	@Override
	public UserDetails loadUserByUsername(String username) {
		Optional<Account> accountOpt = accountRepository.findByUsername(username);
		if (accountOpt.isEmpty()) {
			return null;
		}
		return new CustomUserDetails(accountOpt.get());
	}

	public UserDetails loadAccountById(int accountId) {
		Optional<Account> optional = accountRepository.findById(accountId);
		if (optional.isEmpty()) {
			throw new RuntimeException(accountId + " not found");
		}
		return new CustomUserDetails(optional.get());
	}

}