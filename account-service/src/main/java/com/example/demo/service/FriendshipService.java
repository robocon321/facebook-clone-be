package com.example.demo.service;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AccountEntity;
import com.example.demo.entity.FriendshipEntity;
import com.example.demo.exception.BlockException;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.response.FriendshipResponse;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.ErrorCodeType;
import com.example.demo.type.FriendshipStatusType;

@Service
public class FriendshipService {
	private FriendshipRepository friendshipRepository;
	private AccountRepository accountRepository;

	public FriendshipService(FriendshipRepository friendshipRepository, AccountRepository accountRepository) {
		this.friendshipRepository = friendshipRepository;
		this.accountRepository = accountRepository;
	}

	public FriendshipResponse createFriendship(Integer receiverId, Integer senderId, FriendshipStatusType status) {
		if (Objects.equals(receiverId, senderId)) {
			throw new BlockException(ErrorCodeType.ERROR_ACCOUNT_MATCHED);
		}

		Optional<AccountEntity> receiverOpt = accountRepository.findById(receiverId);
		if (receiverOpt.isEmpty()) {
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_NOT_FOUND, receiverId);
		}

		AccountEntity receiver = receiverOpt.get();
		if (receiver.getStatus() == DeleteStatusType.INACTIVE) {
			throw new BlockException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_BLOCKED, receiverId);
		}

		Optional<AccountEntity> senderOpt = accountRepository.findById(senderId);
		if (senderOpt.isEmpty()) {
			throw new NotFoundException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_NOT_FOUND, senderId);
		}
		AccountEntity sender = senderOpt.get();
		if (sender.getStatus() == DeleteStatusType.INACTIVE) {
			throw new BlockException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_BLOCKED, senderId);
		}

		Optional<FriendshipEntity> friendshipOpt = friendshipRepository.customFindByReceiverIdAndSenderId(receiverId,
				senderId);
		FriendshipEntity previousFriendship = null;
		FriendshipEntity newFriendship = null;
		switch (status) {
			case PENDING:
				if (friendshipOpt.isEmpty()) {
					newFriendship = saveFriendship(receiver, sender, status);
					break;
				}

				previousFriendship = friendshipOpt.get();
				AccountEntity previousReceiver = previousFriendship.getReceiver();
				AccountEntity previousSender = previousFriendship.getSender();

				// Exist block from receiver
				if (previousFriendship.getStatus() == FriendshipStatusType.BLOCK) {
					if (previousReceiver.getAccountId().equals(senderId))
						throw new BlockException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_BLOCKED_YOU, receiverId);
					else
						newFriendship = updateFriendship(previousFriendship, receiver, sender, status);
				} else if (previousFriendship.getStatus() == FriendshipStatusType.PENDING) {
					// Exist friendship before and waiting for accept or reject
					if (previousReceiver.getAccountId().equals(senderId))
						throw new ConflictException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_MATCH_FRIENDSHIP);
					if (previousSender.getAccountId().equals(senderId))
						throw new ConflictException(ErrorCodeType.ERROR_ACCOUNT_MATCH_FRIENDSHIP);
				} else if (previousFriendship.getStatus() == FriendshipStatusType.REJECTED
						|| previousFriendship.getStatus() == FriendshipStatusType.CANCEL)
					// Pending from receiver
					newFriendship = updateFriendship(previousFriendship, receiver, sender, status);

				// PENDING status ACCEPTED
				else
					throw new ConflictException(ErrorCodeType.ERROR_ACCOUNT_ACCEPT_FRIENDSHIP);
				break;
			case REJECTED, ACCEPTED:
				if (friendshipOpt.isEmpty())
					throw new NotFoundException(ErrorCodeType.ERROR_NOT_FOUND_RELATIONSHIP);

				previousFriendship = friendshipOpt.get();
				if (previousFriendship.getStatus() == FriendshipStatusType.PENDING) {
					if (previousFriendship.getSender().getAccountId().equals(senderId))
						throw new BlockException(ErrorCodeType.ERROR_PERMISSION_DENY);
					else
						newFriendship = updateFriendship(previousFriendship, receiver, sender, status);
				} else
					throw new BlockException(ErrorCodeType.ERROR_PERMISSION_DENY);
				break;
			case CANCEL:
				if (friendshipOpt.isEmpty())
					throw new NotFoundException(ErrorCodeType.ERROR_NOT_FOUND_RELATIONSHIP);

				previousFriendship = friendshipOpt.get();
				if (previousFriendship.getStatus() == FriendshipStatusType.ACCEPTED
						|| previousFriendship.getStatus() == FriendshipStatusType.PENDING
						|| previousFriendship.getStatus() == FriendshipStatusType.BLOCK) {
					newFriendship = updateFriendship(previousFriendship, receiver, sender, status);
				} else
					throw new BlockException(ErrorCodeType.ERROR_PERMISSION_DENY);

				break;
			case BLOCK:
				if (friendshipOpt.isEmpty())
					newFriendship = saveFriendship(receiver, sender, status);
				else {
					previousFriendship = friendshipOpt.get();
					newFriendship = updateFriendship(previousFriendship, receiver, sender, status);
				}
				break;
			default:
				break;
		}
		return mappingFriendshipEntityToDTO(newFriendship);
	}

	public boolean checkBlockFromSender(Integer receiverId, Integer senderId) {
		Optional<FriendshipEntity> friendshipOpt = friendshipRepository.customFindByReceiverIdAndSenderId(receiverId,
				senderId);
		if (friendshipOpt.isEmpty()) {
			return false;
		} else {
			FriendshipEntity friendship = friendshipOpt.get();
			if (friendship.getStatus() == FriendshipStatusType.BLOCK) {
				if (friendship.getSender().getAccountId().equals(senderId)) {
					return true;
				} else
					throw new BlockException(ErrorCodeType.ERROR_ACCOUNT_SPECIFIC_BLOCKED_YOU, receiverId);
			}
			return false;
		}
	}

	private FriendshipEntity saveFriendship(AccountEntity receiver, AccountEntity sender, FriendshipStatusType status) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		final FriendshipEntity friendShip = FriendshipEntity.builder().receiver(receiver).sender(sender)
				.requestTime(now)
				.status(status)
				.build();
		return friendshipRepository.save(friendShip);
	}

	private FriendshipEntity updateFriendship(FriendshipEntity previousFriendship, AccountEntity receiver,
			AccountEntity sender, FriendshipStatusType status) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		previousFriendship.setReceiver(receiver);
		previousFriendship.setSender(sender);
		previousFriendship.setRequestTime(now);
		previousFriendship.setStatus(status);
		return friendshipRepository.save(previousFriendship);
	}

	private static FriendshipResponse mappingFriendshipEntityToDTO(FriendshipEntity friendship) {
		FriendshipResponse response = new FriendshipResponse();
		BeanUtils.copyProperties(friendship, response);
		response.setFriendshipId(friendship.getFriendshipId());
		response.setReceiverId(friendship.getReceiver().getAccountId());
		return response;
	}
}
