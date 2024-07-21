package com.example.demo.config;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AccountEntity;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.type.ErrorCodeType;

@Service
public class CustomUserDetailService implements UserDetailsService {
	private AccountRepository accountRepository;

	public CustomUserDetailService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		Optional<AccountEntity> accountOpt = accountRepository.findByUsername(username);
		if (accountOpt.isEmpty()) {
			return null;
		}
		return new CustomUserDetails(accountOpt.get());
	}

	public UserDetails loadAccountById(int accountId) {
		Optional<AccountEntity> optional = accountRepository.findById(accountId);
		if (optional.isEmpty()) {
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_NOT_FOUND);
		}
		return new CustomUserDetails(optional.get());
	}

}
