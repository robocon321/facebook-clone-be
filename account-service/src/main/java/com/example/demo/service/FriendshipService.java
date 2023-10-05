package com.example.demo.service;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Account;
import com.example.demo.entity.Friendship;
import com.example.demo.exception.BlockAccountException;
import com.example.demo.exception.ConflictAccountException;
import com.example.demo.exception.NotFoundAccountException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.response.FriendshipResponse;
import com.example.demo.type.DeleteStatusType;
import com.example.demo.type.FriendshipStatus;

@Service
public class FriendshipService {
	@Autowired
	private FriendshipRepository friendshipRepository;

	@Autowired
	private AccountRepository accountRepository;

	public FriendshipResponse createFriendship(Integer receiverId, Integer senderId, FriendshipStatus status) {
		// check sender match receiver
		if (receiverId == senderId)
			throw new BlockAccountException("Receiver is similar to sender");

		// Check status account
		
		Optional<Account> receiverOpt = accountRepository.findById(receiverId);
		if (receiverOpt.isEmpty()) {
			throw new NotFoundAccountException("Not found account " + receiverId);
		}

		Account receiver = receiverOpt.get();
		if (receiver.getStatus() == DeleteStatusType.INACTIVE) {
			throw new BlockAccountException("Account " + receiverId + " has been lock");
		}

		Optional<Account> senderOpt = accountRepository.findById(senderId);
		if (receiverOpt.isEmpty()) {
			throw new NotFoundAccountException("Not found account " + receiverId);
		}
		Account sender = senderOpt.get();

		if (receiverOpt.isEmpty()) {
			throw new NotFoundAccountException("Not found account " + receiverId);
		}

		if (receiver.getStatus() == DeleteStatusType.INACTIVE) {
			throw new BlockAccountException("Your account " + senderId + " has been lock");
		}
		

		Optional<Friendship> friendshipOpt = friendshipRepository.customFindByReceiverIdAndSenderId(receiverId,
				senderId);
		Friendship previousFriendship = null;
		Friendship newFriendship = null;
		switch (status) {
		case PENDING:
			// Not have friendship
			if (friendshipOpt.isEmpty()) {
				newFriendship = saveFriendship(receiver, sender, status);
				break;
			}

			previousFriendship = friendshipOpt.get();
			Account previousReceiver = previousFriendship.getReceiver();
			Account previousSender = previousFriendship.getSender();

			// Exist block from receiver
			if (previousFriendship.getStatus() == FriendshipStatus.BLOCK) {
				if (previousReceiver.getAccountId().equals(senderId))
					throw new BlockAccountException("Acccount " + receiverId + " was blocked you");
				else
					newFriendship = updateFriendship(previousFriendship, receiver, sender, status);
			}

			// Exist friendship before and waiting for accept or reject
			else if (previousFriendship.getStatus() == FriendshipStatus.PENDING) {
				if (previousReceiver.getAccountId().equals(senderId))
					throw new ConflictAccountException("Account " + receiverId + " has sent pending request friendship before");
				if (previousSender.getAccountId().equals(senderId))
					throw new ConflictAccountException("Your account has sent pending request friendship before");
			}

			// Pending from receiver
			else if (previousFriendship.getStatus() == FriendshipStatus.REJECTED
					|| previousFriendship.getStatus() == FriendshipStatus.CANCEL)
				newFriendship = updateFriendship(previousFriendship, receiver, sender, status);

			// PENDING status ACCEPTED
			else throw new ConflictAccountException("Your account has accept request friendship before");
			break;
		case REJECTED, ACCEPTED:
			if (friendshipOpt.isEmpty())
				throw new NotFoundAccountException("Not found friendship with account " + receiverId);

			previousFriendship = friendshipOpt.get();
			if (previousFriendship.getStatus() == FriendshipStatus.PENDING) {
				if (previousFriendship.getSender().getAccountId().equals(senderId))
					throw new BlockAccountException("You dont have permission " + status);
				else
					newFriendship = updateFriendship(previousFriendship, receiver, sender, status);
			} else
				throw new BlockAccountException("Only status PENDING can " + status);
			break;
		case CANCEL:
			if (friendshipOpt.isEmpty())
				throw new NotFoundAccountException("Not found friendship with account " + receiverId);

			previousFriendship = friendshipOpt.get();
			if (previousFriendship.getStatus() == FriendshipStatus.ACCEPTED
					|| previousFriendship.getStatus() == FriendshipStatus.PENDING
					|| previousFriendship.getStatus() == FriendshipStatus.BLOCK) {
				newFriendship = updateFriendship(previousFriendship, receiver, sender, status);
			} else
				throw new BlockAccountException("Only status ACCEPTED can " + status);

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
		Optional<Friendship> friendshipOpt = friendshipRepository.customFindByReceiverIdAndSenderId(receiverId,
				senderId);
		if (friendshipOpt.isEmpty()) {
			return false;
		} else {
			Friendship friendship = friendshipOpt.get();
			if (friendship.getStatus() == FriendshipStatus.BLOCK) {
				if (friendship.getSender().getAccountId().equals(senderId)) {
					return true;
				} else
					throw new BlockAccountException("Receiver " + receiverId + " block your account");
			}
			return false;
		}
	}

	private Friendship saveFriendship(Account receiver, Account sender, FriendshipStatus status) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		Friendship friendShip = Friendship.builder().receiver(receiver).sender(sender).requestTime(now).status(status)
				.build();
		Friendship newFriendship = friendshipRepository.save(friendShip);

		return newFriendship;
	}
	
	private Friendship updateFriendship(Friendship previousFriendship, Account receiver, Account sender, FriendshipStatus status) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		previousFriendship.setReceiver(receiver);
		previousFriendship.setSender(sender);
		previousFriendship.setRequestTime(now);
		previousFriendship.setStatus(status);
		Friendship newFriendship = friendshipRepository.save(previousFriendship);

		return newFriendship;
	}

	private FriendshipResponse mappingFriendshipEntityToDTO(Friendship friendship) {
		FriendshipResponse response = new FriendshipResponse();
		BeanUtils.copyProperties(friendship, response);
		response.setFriendshipId(friendship.getFriendshipId());
		response.setReceiverId(friendship.getReceiver().getAccountId());
		return response;
	}
}
