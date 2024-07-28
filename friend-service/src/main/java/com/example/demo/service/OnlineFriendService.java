package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AccountEntity;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.type.ErrorCodeType;

@Service
public class OnlineFriendService {
	private AccountRepository accountRepository;

	public OnlineFriendService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public List<Integer> getAllFriends(Integer accountId) {
		Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
		if (accountOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_NOT_FOUND);
		Pageable pageable = PageRequest.of(0, 1000);

		Page<AccountEntity> pageEntity = accountRepository.findByCurrentIdAndFriendshipStatus(accountId, 'A', "",
				pageable);
		return pageEntity.getContent().stream().map(AccountEntity::getAccountId).toList();
	}
}
