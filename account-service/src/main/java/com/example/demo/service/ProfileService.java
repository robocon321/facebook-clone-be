package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AccountEntity;
import com.example.demo.exception.BlockException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.provider.JwtProvider;
import com.example.demo.repository.AccountRepository;
import com.example.demo.response.ProfileResponse;
import com.example.demo.type.DeleteStatusType;

@Service
public class ProfileService {
	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private AccountRepository accountRepository;

	public ProfileResponse getProfileInfoByAccount(Integer profileId, String token) {
		Integer id = jwtProvider.getAccountIdFromJWT(token);
		Optional<AccountEntity> accountOpt = accountRepository.findById(id);
		if (accountOpt.isEmpty())
			throw new NotFoundException("Your account does not exists");
		AccountEntity account = accountOpt.get();
		if (account.getStatus() == DeleteStatusType.INACTIVE)
			throw new BlockException("Your account is blocked. Please contact us to active your account");
		
		Optional<AccountEntity> profileOpt = accountRepository.findById(profileId);
		if(profileOpt.isEmpty()) throw new NotFoundException("Not found profile with id: " + profileId);
		AccountEntity profile = profileOpt.get();
		
		ProfileResponse response = new ProfileResponse();
		BeanUtils.copyProperties(profile, response);
		return response;
	}

	public ProfileResponse getProfileInfoByAnonymous(Integer profileId) {
		Optional<AccountEntity> profileOpt = accountRepository.findById(profileId);
		if(profileOpt.isEmpty()) throw new NotFoundException("Not found profile with id: " + profileId);
		AccountEntity profile = profileOpt.get();
		
		ProfileResponse response = new ProfileResponse();
		BeanUtils.copyProperties(profile, response);
		return response;
	}
}
