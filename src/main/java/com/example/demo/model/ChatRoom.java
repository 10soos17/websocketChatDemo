package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//채팅방

@Getter
@Setter
public class ChatRoom {

	private String roomId;
	private String ownerId;

	@Builder.Default
	private LocalDateTime writeDate = LocalDateTime.now();

	// 입장하는 sessionId : session(userId, roomId개별세팅)
	@JsonIgnore
	private Map<String, WebSocketSession> sessions = new HashMap<>();
	
	// chatmessage

	// ChatMessage 저장 - 임시 test용 - roomId : ChatMessage 객체 리스트
	// Map<String, ArrayList<ChatMessage>> chatMessages;

	// 순서대로 datetime : list{sender,message}...
	// 불러올때, 시간순으로 목록뽑아서뿌리기
	// private Map<String, List<String>> messages = new TreeMap<>(); //순서대로 datetime : list{sender,message}...

	// 순서대로 list에 넣기 {sender,message,datetime}
	// 불러올때, 차례대로 목록뽑아서 뿌리기
	private List<ChatMessage> messages = new ArrayList<>();
	
	// 순서대로 list에 넣기 {fileId, fileName, fileType, sender}
	private List<UploadFile> files = new ArrayList<>();
	
	
	@Builder
	public ChatRoom(String roomId, String ownerId) {
		this.roomId = roomId;
		this.ownerId =ownerId;
	}

}
