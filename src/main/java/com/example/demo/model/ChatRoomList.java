package com.example.demo.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChatRoomList {

	List<ChatRoom> chatRoomList;

	@Builder
	public ChatRoomList(List<ChatRoom> chatRoomList) {
		this.chatRoomList = chatRoomList;
	}


}
