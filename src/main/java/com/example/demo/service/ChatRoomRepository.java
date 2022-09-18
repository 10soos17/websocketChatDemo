package com.example.demo.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.ChatRoom;
import com.example.demo.model.ChatRoomList;
import com.example.demo.model.UploadFileList;

public interface ChatRoomRepository {

	public ChatRoom createRoom(ChatRoom chatroom);
	public ChatRoom findRoomById(String roomId);
	public ChatRoomList findAllRoom();
	public void removeRoom(String roomId);
	
	public UploadFileList uploadFiles(List<MultipartFile> files, String roomId, String sender);
	public ResponseEntity<byte[]> downloadFiles(String path);

}
