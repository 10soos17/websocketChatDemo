package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.ChatRoom;
import com.example.demo.model.ChatRoomList;
import com.example.demo.model.UploadFileList;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.ChatRoomRepository;

import lombok.extern.slf4j.Slf4j;

//@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatroomController {

	@Autowired
	public ChatRoomRepository chatRoomRespository;
	
	@Autowired
	ChatMessageService chatMessageService;

	@RequestMapping(method = RequestMethod.POST, value="/chatroom")
	public String main(){
		log.info("chatroom");

		return "/static/chatroom.html";
	}
	// create room
	@RequestMapping(method = RequestMethod.POST, value="/createRoom")
	public ChatRoom createRoom(@RequestBody ChatRoom chatroom){
		log.info("create:{}",chatroom);
		return chatRoomRespository.createRoom(chatroom);
	}

	// get detailed one by RoomId
	@RequestMapping(method = RequestMethod.GET, value="/findRoomById")
	public ChatRoom findRoomById(@RequestParam String roomId){
		return chatRoomRespository.findRoomById(roomId);
	}

	// get detailed all
	@RequestMapping(method = RequestMethod.GET, value="/findAllRoom")
	public ChatRoomList findAllRoom(){
		return chatRoomRespository.findAllRoom();
	}
	
	// upload files
	// chatRoomRespository.uploadFiles 호출
	// return UploadFileList
	// chatMessageService.sendFileMessage 호출
	@RequestMapping(method = RequestMethod.POST, value="/uploadFiles")
	public void uploadFiles(@RequestParam List<MultipartFile> files,@RequestParam String roomId, @RequestParam String sender) {
		log.info("{},{},{}:",files.size(), roomId, sender);
		
		UploadFileList fileList = chatRoomRespository.uploadFiles(files, roomId, sender);
		chatMessageService.sendFileMessage(fileList);
		
	}
	
	//download files
	// ex.localhost:8080/chat/download?path=8de33d7a-0e4f-4ada-b830-3f6d71c5f902/sfd/2022/08/31/34d3f503-c5bd-4bf3-a0f3-90ef1774d243_1661911323535.txt
	@RequestMapping(method = RequestMethod.GET, value="/downloadFiles")
	public ResponseEntity<byte[]> downloadFiles(@RequestParam("path") String path) {
		log.info("{}:",path);
		return chatRoomRespository.downloadFiles(path);
	}
}
