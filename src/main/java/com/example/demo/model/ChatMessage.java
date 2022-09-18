package com.example.demo.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//채팅 메시지

@Getter
@Setter
public class ChatMessage {

	public enum MessageType{
		ENTER, TALK, EXIT, FILE
	}
	private MessageType type; // 메시지 타입
	private String sender; // 메시지 보낸사람
	private String message; // 메시지

	@Builder.Default
	private LocalDateTime writeDate = LocalDateTime.now();

	public ChatMessage(MessageType type, String sender, String message) {
		this.type = type;
		this.sender = sender;
		this.message = message;
	}



}
