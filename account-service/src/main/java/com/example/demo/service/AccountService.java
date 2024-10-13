package com.example.demo.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AccountEntity;
import com.example.demo.entity.ActionHistoryEntity;
import com.example.demo.exception.BlockException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.ActionHistoryRepository;
import com.example.demo.request.AccountFriendshipRequest;
import com.example.demo.request.FriendHistoryRequest;
import com.example.demo.request.RecommendFriendshipRequest;
import com.example.demo.response.AccountResponse;
import com.example.demo.response.ActionHistoryResponse;
import com.example.demo.response.CustomPageResponse;
import com.example.demo.response.HistoryAccountResponse;
import com.example.demo.response.RecommendAccountResponse;
import com.example.demo.type.ActionHistoryStatusType;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.ErrorCodeType;

@Service
public class AccountService {
	private AccountRepository accountRepository;
	private ActionHistoryRepository actionHistoryRepository;

	public AccountService(AccountRepository accountRepository,
			ActionHistoryRepository actionHistoryRepository) {
		this.accountRepository = accountRepository;
		this.actionHistoryRepository = actionHistoryRepository;
	}

	public AccountResponse getSummaryInfo(String headerUserId) {
		Integer id = Integer.parseInt(headerUserId);
		Optional<AccountEntity> accountOpt = accountRepository.findById(id);
		if (accountOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_NOT_FOUND);
		AccountEntity account = accountOpt.get();
		if (account.getStatus() == DeleteStatusType.INACTIVE)
			throw new BlockException(ErrorCodeType.ERROR_ACCOUNT_BLOCKED);
		AccountResponse response = new AccountResponse();
		BeanUtils.copyProperties(account, response);
		return response;
	}

	public CustomPageResponse getFriendHistory(FriendHistoryRequest request, String headerUserId) {
		Integer currentId = Integer.parseInt(headerUserId);
		Optional<AccountEntity> accountOpt = accountRepository.findById(currentId);
		if (accountOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_NOT_FOUND);
		AccountEntity account = accountOpt.get();
		if (account.getStatus() == DeleteStatusType.INACTIVE)
			throw new BlockException(ErrorCodeType.ERROR_ACCOUNT_BLOCKED);

		Pageable pageable = null;
		if (request.getSortBy() == null) {
			pageable = PageRequest.of(request.getPage(), request.getSize());
		} else {
			Sort.Order[] orders = IntStream.range(0, request.getSortBy().length).mapToObj(
					i -> new Sort.Order(Sort.Direction.fromString(request.getSortOrder()[i]), request.getSortBy()[i]))
					.toArray(Sort.Order[]::new);

			Sort sort = Sort.by(orders);
			pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
		}
		Page<AccountEntity> pageEntity = accountRepository.findByCurrentIdAndFriendshipStatus(currentId, 'A', "",
				pageable);

		List<HistoryAccountResponse> data = new ArrayList<>();

		pageEntity.getContent().stream().forEach(item -> {
			HistoryAccountResponse historyAccountResponse = new HistoryAccountResponse();
			Optional<ActionHistoryEntity> actionHistoryOpt = Optional.empty();
			if (request.getType() == null) {
				actionHistoryOpt = actionHistoryRepository
						.findFirstByAccountIdOrderByActionTimeDesc(item.getAccountId());
			} else {
				actionHistoryOpt = actionHistoryRepository
						.findFirstByAccountIdAndStatusValueOrderByActionTimeDesc(item.getAccountId(),
								request.getType().getStatus());
			}

			AccountResponse accountResponse = new AccountResponse();
			BeanUtils.copyProperties(item, accountResponse);
			historyAccountResponse.setAccount(accountResponse);

			if (actionHistoryOpt.isPresent()) {
				ActionHistoryResponse actionHistoryResponse = new ActionHistoryResponse();
				ActionHistoryEntity actionHistory = actionHistoryOpt.get();
				BeanUtils.copyProperties(actionHistory, actionHistoryResponse);
				historyAccountResponse.setCurrentHistory(actionHistoryResponse);
			}
			data.add(historyAccountResponse);
		});

		return CustomPageResponse.builder()
				.data(data)
				.totalItem(pageEntity.getTotalElements())
				.totalPage(pageEntity.getTotalPages())
				.build();
	}

	public CustomPageResponse getListAccountByFriendshipStatus(AccountFriendshipRequest request, String headerUserId) {
		Integer currentId = Integer.parseInt(headerUserId);
		Optional<AccountEntity> accountOpt = accountRepository.findById(currentId);
		if (accountOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_NOT_FOUND);
		Pageable pageable = null;
		if (request.getSortBy() == null) {
			pageable = PageRequest.of(request.getPage(), request.getSize());
		} else {
			Sort.Order[] orders = IntStream.range(0, request.getSortBy().length).mapToObj(
					i -> new Sort.Order(Sort.Direction.fromString(request.getSortOrder()[i]), request.getSortBy()[i]))
					.toArray(Sort.Order[]::new);

			Sort sort = Sort.by(orders);
			pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
		}
		Page<AccountEntity> pageEntity;
		if (request.getExcludeIds().isEmpty()) {
			pageEntity = accountRepository.findByCurrentIdAndFriendshipStatus(currentId,
					request.getStatus().getStatus(), request.getSearch(), pageable);
		} else {
			pageEntity = accountRepository.findByCurrentIdAndFriendshipStatus(currentId,
					request.getStatus().getStatus(), request.getSearch(), request.getExcludeIds(), pageable);
		}
		return pageEntityToPageResponse(pageEntity, currentId);
	}

	public CustomPageResponse getReceiverByFriendshipStatus(AccountFriendshipRequest request, String headerUserId) {
		Integer currentId = Integer.parseInt(headerUserId);
		Optional<AccountEntity> accountOpt = accountRepository.findById(currentId);
		if (accountOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_NOT_FOUND);
		Pageable pageable = null;
		if (request.getSortBy() == null) {
			pageable = PageRequest.of(request.getPage(), request.getSize());
		} else {
			Sort.Order[] orders = IntStream.range(0, request.getSortBy().length).mapToObj(
					i -> new Sort.Order(Sort.Direction.fromString(request.getSortOrder()[i]), request.getSortBy()[i]))
					.toArray(Sort.Order[]::new);

			Sort sort = Sort.by(orders);
			pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
		}
		Page<AccountEntity> pageEntity;
		if (request.getExcludeIds().isEmpty()) {
			pageEntity = accountRepository.findBySenderIdAndFriendshipStatus(currentId, request.getStatus().getStatus(),
					request.getSearch(), pageable);
		} else {
			pageEntity = accountRepository.findBySenderIdAndFriendshipStatus(currentId, request.getStatus().getStatus(),
					request.getSearch(), request.getExcludeIds(), pageable);
		}
		return pageEntityToPageResponse(pageEntity, currentId);
	}

	public CustomPageResponse getSenderByFriendshipStatus(AccountFriendshipRequest request, String headerUserId) {
		Integer currentId = Integer.parseInt(headerUserId);
		Optional<AccountEntity> accountOpt = accountRepository.findById(currentId);
		if (accountOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_NOT_FOUND);
		Pageable pageable = null;
		if (request.getSortBy() == null) {
			pageable = PageRequest.of(request.getPage(), request.getSize());
		} else {
			Sort.Order[] orders = IntStream.range(0, request.getSortBy().length).mapToObj(
					i -> new Sort.Order(Sort.Direction.fromString(request.getSortOrder()[i]), request.getSortBy()[i]))
					.toArray(Sort.Order[]::new);

			Sort sort = Sort.by(orders);
			pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
		}
		Page<AccountEntity> pageEntity;
		if (request.getExcludeIds().isEmpty()) {
			pageEntity = accountRepository.findByReceiverIdAndFriendshipStatus(currentId,
					request.getStatus().getStatus(),
					request.getSearch(), pageable);
		} else {
			pageEntity = accountRepository.findByReceiverIdAndFriendshipStatus(currentId,
					request.getStatus().getStatus(),
					request.getSearch(), request.getExcludeIds(), pageable);
		}
		return pageEntityToPageResponse(pageEntity, currentId);
	}

	public CustomPageResponse recommendFriend(RecommendFriendshipRequest request, String headerUserId) {
		Integer currentId = Integer.parseInt(headerUserId);
		Optional<AccountEntity> accountOpt = accountRepository.findById(currentId);
		if (accountOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_NOT_FOUND);
		Pageable pageable = null;
		if (request.getSortBy() == null) {
			pageable = PageRequest.of(request.getPage(), request.getSize());
		} else {
			Sort.Order[] orders = IntStream.range(0, request.getSortBy().length).mapToObj(
					i -> new Sort.Order(Sort.Direction.fromString(request.getSortOrder()[i]), request.getSortBy()[i]))
					.toArray(Sort.Order[]::new);

			Sort sort = Sort.by(orders);
			pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
		}
		Page<AccountEntity> pageEntity;
		if (request.getExcludeIds().isEmpty()) {
			pageEntity = accountRepository.recommendAccount(currentId,
					request.getSearch(), pageable);
		} else {
			pageEntity = accountRepository.recommendAccount(currentId, request.getSearch(), request.getExcludeIds(),
					pageable);
		}

		return pageEntityToPageResponse(pageEntity, currentId);
	}

	public ActionHistoryResponse updateHistory(ActionHistoryStatusType type, String headerUserId) {
		Integer currentId = Integer.parseInt(headerUserId);
		Optional<AccountEntity> accountOpt = accountRepository.findById(currentId);
		if (accountOpt.isEmpty())
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_NOT_FOUND);

		Timestamp now = new Timestamp(System.currentTimeMillis());
		ActionHistoryEntity actionHistory = ActionHistoryEntity.builder()
				.accountId(accountOpt.get().getAccountId())
				.actionTime(now)
				.status(type)
				.build();

		ActionHistoryEntity newActionHistory = actionHistoryRepository.save(actionHistory);
		ActionHistoryResponse historyResponse = new ActionHistoryResponse();
		BeanUtils.copyProperties(newActionHistory, historyResponse);
		return historyResponse;
	}

	private CustomPageResponse pageEntityToPageResponse(Page<AccountEntity> pageEntity, Integer currentId) {
		List<RecommendAccountResponse> pageDTO = pageEntity.stream().map(account -> {
			RecommendAccountResponse response = new RecommendAccountResponse();
			BeanUtils.copyProperties(account, response);

			List<AccountEntity> manualFriends = accountRepository.findManualFriends(currentId, account.getAccountId());
			List<AccountResponse> manualFriendsResponse = manualFriends.stream().map(item -> {
				AccountResponse accountResponse = new AccountResponse();
				BeanUtils.copyProperties(item, accountResponse);
				return accountResponse;
			}).toList();
			response.setManualFriends(manualFriendsResponse);

			return response;
		}).toList();

		return CustomPageResponse.builder().data(pageDTO)
				.totalItem(pageEntity.getTotalElements()).totalPage(pageEntity.getTotalPages()).build();
	}

}
