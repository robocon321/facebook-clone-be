package com.example.demo.config;

import java.util.Optional;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AccountEntity;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.type.ErrorCodeType;

import reactor.core.publisher.Mono;

@Service
public class CustomUserDetailService implements ReactiveUserDetailsService {

	private AccountRepository accountRepository;

	public CustomUserDetailService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public UserDetails loadUserById(int accountId) {
		Optional<AccountEntity> optional = accountRepository.findById(accountId);
		if (optional.isEmpty()) {
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_NOT_FOUND);
		}
		return new CustomUserDetails(optional.get());
	}

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		Optional<AccountEntity> accountOpt = accountRepository.findByUsername(username);
		if (accountOpt.isEmpty()) {
			return Mono.empty();
		}
		return Mono.just(User.withUsername(username)
				.password(accountOpt.get().getPassword())
				.build());
	}

}
