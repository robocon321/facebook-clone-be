package com.example.demo.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class UserInterceptor implements ChannelInterceptor {
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		try {
			StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

			if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
				headerAccessor.setUser(new UserPrinciple(headerAccessor.getSessionId()));
			}			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return message;
	}
}