package com.example.demo.service;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.demo.type.FriendshipStatusType;

@Service
public class FriendshipService {
	@Autowired
	private FriendshipRepository friendshipRepository;

	@Autowired
	private AccountRepository accountRepository;

	public FriendshipResponse createFriendship(Integer receiverId, Integer senderId, FriendshipStatusType status) {
		// check sender match receiver
		if (receiverId == senderId)
			throw new BlockException("Receiver is similar to sender");

		// Check status account

		Optional<AccountEntity> receiverOpt = accountRepository.findById(receiverId);
		if (receiverOpt.isEmpty()) {
			throw new NotFoundException("Not found account " + receiverId);
		}

		AccountEntity receiver = receiverOpt.get();
		if (receiver.getStatus() == DeleteStatusType.INACTIVE) {
			throw new BlockException("Account " + receiverId + " has been lock");
		}

		Optional<AccountEntity> senderOpt = accountRepository.findById(senderId);
		if (receiverOpt.isEmpty()) {
			throw new NotFoundException("Not found account " + receiverId);
		}
		AccountEntity sender = senderOpt.get();

		if (receiverOpt.isEmpty()) {
			throw new NotFoundException("Not found account " + receiverId);
		}

		if (receiver.getStatus() == DeleteStatusType.INACTIVE) {
			throw new BlockException("Your account " + senderId + " has been lock");
		}

		Optional<FriendshipEntity> friendshipOpt = friendshipRepository.customFindByReceiverIdAndSenderId(receiverId,
				senderId);
		FriendshipEntity previousFriendship = null;
		FriendshipEntity newFriendship = null;
		switch (status) {
			case PENDING:
				// Not have friendship
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
						throw new BlockException("Acccount " + receiverId + " was blocked you");
					else
						newFriendship = updateFriendship(previousFriendship, receiver, sender, status);
				}

				// Exist friendship before and waiting for accept or reject
				else if (previousFriendship.getStatus() == FriendshipStatusType.PENDING) {
					if (previousReceiver.getAccountId().equals(senderId))
						throw new ConflictException(
								"Account " + receiverId + " has sent pending request friendship before");
					if (previousSender.getAccountId().equals(senderId))
						throw new ConflictException("Your account has sent pending request friendship before");
				}

				// Pending from receiver
				else if (previousFriendship.getStatus() == FriendshipStatusType.REJECTED
						|| previousFriendship.getStatus() == FriendshipStatusType.CANCEL)
					newFriendship = updateFriendship(previousFriendship, receiver, sender, status);

				// PENDING status ACCEPTED
				else
					throw new ConflictException("Your account has accept request friendship before");
				break;
			case REJECTED, ACCEPTED:
				if (friendshipOpt.isEmpty())
					throw new NotFoundException("Not found friendship with account " + receiverId);

				previousFriendship = friendshipOpt.get();
				if (previousFriendship.getStatus() == FriendshipStatusType.PENDING) {
					if (previousFriendship.getSender().getAccountId().equals(senderId))
						throw new BlockException("You dont have permission " + status);
					else
						newFriendship = updateFriendship(previousFriendship, receiver, sender, status);
				} else
					throw new BlockException("Only status PENDING can " + status);
				break;
			case CANCEL:
				if (friendshipOpt.isEmpty())
					throw new NotFoundException("Not found friendship with account " + receiverId);

				previousFriendship = friendshipOpt.get();
				if (previousFriendship.getStatus() == FriendshipStatusType.ACCEPTED
						|| previousFriendship.getStatus() == FriendshipStatusType.PENDING
						|| previousFriendship.getStatus() == FriendshipStatusType.BLOCK) {
					newFriendship = updateFriendship(previousFriendship, receiver, sender, status);
				} else
					throw new BlockException("Only status ACCEPTED can " + status);

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
		FriendshipResponse response = mappingFriendshipEntityToDTO(newFriendship);
		return response;
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
					throw new BlockException("Receiver " + receiverId + " block your account");
			}
			return false;
		}
	}

	private FriendshipEntity saveFriendship(AccountEntity receiver, AccountEntity sender, FriendshipStatusType status) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		FriendshipEntity friendShip = FriendshipEntity.builder().receiver(receiver).sender(sender).requestTime(now)
				.status(status)
				.build();
		FriendshipEntity newFriendship = friendshipRepository.save(friendShip);

		return newFriendship;
	}

	private FriendshipEntity updateFriendship(FriendshipEntity previousFriendship, AccountEntity receiver,
			AccountEntity sender, FriendshipStatusType status) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		previousFriendship.setReceiver(receiver);
		previousFriendship.setSender(sender);
		previousFriendship.setRequestTime(now);
		previousFriendship.setStatus(status);
		FriendshipEntity newFriendship = friendshipRepository.save(previousFriendship);

		return newFriendship;
	}

	private FriendshipResponse mappingFriendshipEntityToDTO(FriendshipEntity friendship) {
		FriendshipResponse response = new FriendshipResponse();
		BeanUtils.copyProperties(friendship, response);
		response.setFriendshipId(friendship.getFriendshipId());
		response.setReceiverId(friendship.getReceiver().getAccountId());
		return response;
	}
}
