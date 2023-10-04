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
		if (receiverId == senderId)
			throw new BlockAccountException("Receiver is similar to sender");
		Optional<Friendship> friendshipOpt = friendshipRepository.customFindByReceiverIdAndSenderId(receiverId,
				senderId);
		Friendship currentFriendship = null;
		Friendship newFriendship = null;
		switch (status) {
		case PENDING:
			// Not have friendship
			if (friendshipOpt.isEmpty()) {
				newFriendship = saveFriendship(receiverId, senderId, status);
				break;
			}

			currentFriendship = friendshipOpt.get();
			Account currentReceiver = currentFriendship.getReceiver();
			Account currentSender = currentFriendship.getSender();

			// Exist block from receiver
			if (currentFriendship.getStatus() == FriendshipStatus.BLOCK) {
				if (currentReceiver.getAccountId().equals(senderId))
					throw new BlockAccountException("Acccount " + receiverId + " was blocked you");
				else
					newFriendship = saveFriendship(receiverId, senderId, status);
			}

			// Exist friendship before and waiting for accept or reject
			if (currentFriendship.getStatus() == FriendshipStatus.PENDING) {
				if (currentReceiver.getAccountId().equals(senderId))
					throw new ConflictAccountException("Account " + receiverId + " has sent request friendship before");
				if (currentSender.getAccountId().equals(senderId))
					throw new ConflictAccountException("Your account has sent request friendship before");
			}

			// Pending from receiver
			if (currentFriendship.getStatus() == FriendshipStatus.REJECTED
					|| currentFriendship.getStatus() == FriendshipStatus.CANCEL)
				newFriendship = saveFriendship(receiverId, senderId, status);

			break;
		case REJECTED, ACCEPTED:
			if (friendshipOpt.isEmpty())
				throw new NotFoundAccountException("Not found friendship with account " + receiverId);

			currentFriendship = friendshipOpt.get();
			if (currentFriendship.getStatus() == FriendshipStatus.PENDING) {
				if (currentFriendship.getSender().getAccountId().equals(senderId))
					throw new BlockAccountException("You dont have permission " + status);
				else
					newFriendship = saveFriendship(receiverId, senderId, status);
			} else
				throw new BlockAccountException("Only status PENDING can " + status);
			break;
		case CANCEL:
			if (friendshipOpt.isEmpty())
				throw new NotFoundAccountException("Not found friendship with account " + receiverId);

			currentFriendship = friendshipOpt.get();
			if (currentFriendship.getStatus() == FriendshipStatus.ACCEPTED
					|| currentFriendship.getStatus() == FriendshipStatus.PENDING
					|| currentFriendship.getStatus() == FriendshipStatus.BLOCK) {
				newFriendship = saveFriendship(receiverId, senderId, status);
			} else
				throw new BlockAccountException("Only status ACCEPTED can " + status);

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

	private Friendship saveFriendship(Integer receiverId, Integer senderId, FriendshipStatus status) {
		// Validate receiver
		Optional<Account> receiverOpt = accountRepository.findById(receiverId);
		if (receiverOpt.isEmpty()) {
			throw new NotFoundAccountException("Not found account " + receiverId);
		}

		Account receiver = receiverOpt.get();
		if (receiver.getStatus() == DeleteStatusType.INACTIVE) {
			throw new BlockAccountException("Account " + receiverId + " has been lock");
		}

		Account sender = accountRepository.findById(senderId).get();
		if (receiver.getStatus() == DeleteStatusType.INACTIVE) {
			throw new BlockAccountException("Your account " + senderId + " has been lock");
		}

		// Create request friendship
		Timestamp now = new Timestamp(System.currentTimeMillis());
		Friendship friendShip = Friendship.builder().receiver(receiver).sender(sender).requestTime(now).status(status)
				.build();
		Friendship newFriendship = friendshipRepository.save(friendShip);

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
