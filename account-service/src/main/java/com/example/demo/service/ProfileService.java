package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AccountEntity;
import com.example.demo.exception.BlockException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.response.ProfileResponse;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.ErrorCodeType;

@Service
public class ProfileService {
	private AccountRepository accountRepository;

	public ProfileService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public ProfileResponse getProfileInfoByAccount(Integer profileId, String headerUserId) {
		Integer id = Integer.parseInt(headerUserId);
		Optional<AccountEntity> accountOpt = accountRepository.findById(id);
		if (accountOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_NOT_FOUND);
		AccountEntity account = accountOpt.get();
		if (account.getStatus() == DeleteStatusType.INACTIVE)
			throw new BlockException(ErrorCodeType.ERROR_ACCOUNT_BLOCKED);

		Optional<AccountEntity> profileOpt = accountRepository.findById(profileId);
		if (profileOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_NOT_FOUND, profileId);
		AccountEntity profile = profileOpt.get();

		ProfileResponse response = new ProfileResponse();
		BeanUtils.copyProperties(profile, response);
		return response;
	}

	public ProfileResponse getProfileInfoByAnonymous(Integer profileId) {
		Optional<AccountEntity> profileOpt = accountRepository.findById(profileId);
		if (profileOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_NOT_FOUND, profileId);
		AccountEntity profile = profileOpt.get();

		ProfileResponse response = new ProfileResponse();
		BeanUtils.copyProperties(profile, response);
		return response;
	}
}
