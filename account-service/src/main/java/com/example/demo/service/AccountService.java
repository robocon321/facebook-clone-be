package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AccountEntity;
import com.example.demo.exception.BlockException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.provider.JwtProvider;
import com.example.demo.repository.AccountRepository;
import com.example.demo.request.AccountFriendshipRequest;
import com.example.demo.response.AccountSummaryInfoResponse;
import com.example.demo.response.PageResponse;
import com.example.demo.type.DeleteStatusType;

@Service
public class AccountService {
	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private AccountRepository accountRepository;

	public AccountSummaryInfoResponse getSummaryInfo(String token) {
		Integer id = jwtProvider.getAccountIdFromJWT(token);
		Optional<AccountEntity> accountOpt = accountRepository.findById(id);
		if (accountOpt.isEmpty())
			throw new NotFoundException("Your account does not exists");
		AccountEntity account = accountOpt.get();
		if (account.getStatus() == DeleteStatusType.INACTIVE)
			throw new BlockException("Your account is blocked. Please contact us to active your account");
		AccountSummaryInfoResponse response = new AccountSummaryInfoResponse();
		BeanUtils.copyProperties(account, response);
		return response;
	}

	public PageResponse getListAccountByFriendshipStatus(AccountFriendshipRequest request,
			String token) {
		Integer currentId = jwtProvider.getAccountIdFromJWT(token);
		Optional<AccountEntity> accountOpt = accountRepository.findById(currentId);
		if (accountOpt.isEmpty())
			throw new NotFoundException("Your account does not exists");
		Pageable pageable = null;
		if(request.getSortBy() == null) {
			pageable = PageRequest.of(request.getPage(), request.getSize());
		} else {
			Sort.Order[] orders = IntStream.range(0, request.getSortBy().length).mapToObj(
					i -> new Sort.Order(Sort.Direction.fromString(request.getSortOrder()[i]), request.getSortBy()[i]))
					.toArray(Sort.Order[]::new);

			Sort sort = Sort.by(orders);
			pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
		}
		Page<AccountEntity> pageEntity;
		if(request.getExcludeIds().size() == 0) {
			pageEntity = accountRepository.findByCurrentIdAndFriendshipStatus(
					currentId,
					request.getStatus().getStatus(),
					request.getSearch(),
					pageable
			);						
		} else {
			pageEntity = accountRepository.findByCurrentIdAndFriendshipStatus(
					currentId,
					request.getStatus().getStatus(),
					request.getSearch(),
					request.getExcludeIds(),
					pageable
			);			
		}
		List<AccountSummaryInfoResponse> pageDTO = pageEntity.stream()
				.map(account -> {
					AccountSummaryInfoResponse response = new AccountSummaryInfoResponse();
					BeanUtils.copyProperties(account, response);
					return response;
				}).toList();
		PageResponse pageResponse = PageResponse.builder()
				.data(pageDTO)
				.totalItem(pageEntity.getTotalElements())
				.totalPage(pageEntity.getTotalPages()).build();
		return pageResponse;
	}
}
