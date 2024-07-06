package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AccountEntity;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;

@Service
public class OnlineFriendService {
	@Autowired
	private AccountRepository accountRepository;

	public List<Integer> getAllFriends(Integer accountId) {
		Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
		if (accountOpt.isEmpty())
			throw new NotFoundException("Your account does not exists");
		Pageable pageable = PageRequest.of(0, 1000);

		Page<AccountEntity> pageEntity = accountRepository.findByCurrentIdAndFriendshipStatus(accountId, 'A', "",
				pageable);
		List<Integer> friendIds = pageEntity.getContent().stream().map(item -> item.getAccountId()).toList();
		return friendIds;
	}

}
