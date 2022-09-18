package com.example.demo.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.ChatMessage.MessageType;
import com.example.demo.model.ChatRoom;
import com.example.demo.model.UploadFile;
import com.example.demo.model.UploadFileList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

//Q
//파일전송시, 파일 객체 저장 문제 

@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

	// ChatMessage 저장 - 임시 test용 - roomId : ChatMessage 객체 리스트
	// Map<String, ArrayList<ChatMessage>> chatMessages;

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	ChatRoomRepository chatRoomRepository;

//	@PostConstruct
//	private void init() {
//		chatMessages = new LinkedHashMap<>();
//	}

	// 입장시,
	// 세션 추가
	// sendMessage - 입장메시지
	@Override
	public <T> void sendEnterMessage(WebSocketSession newSession) {

		log.info("sendEnterMessage - sesseion - userId: {}, roomId:{}", 
				newSession.getAttributes().get("userId"),
				newSession.getAttributes().get("roomId"));

		// 세션 속성에 저장된 아이디, roomId, userId 정보
		String sessionId = newSession.getId();
		String roomId = (String) newSession.getAttributes().get("roomId");
		String userId = (String) newSession.getAttributes().get("userId");

//		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String dateFormat = sdf.format(writeDate);
//		Date date;
		// date = sdf.parse(dateFormat);

		ChatMessage chatMessage = new ChatMessage(MessageType.ENTER, userId, "입장");
		// roomId로 해당 chatRoom정보 가져오기
		ChatRoom room = chatRoomRepository.findRoomById(roomId);
		// 해당 chatRoom에 접속한 세션들 가져오기
		Map<String, WebSocketSession> sessions = room.getSessions();

		// 입장시 보낼 메시지 객체 생성

		// 현 세션이 기존 세션에 있는지 여부 체크
		if (!sessions.containsValue(newSession)) { // 없으면
			log.info("add new sessions, size:{}", sessions.size());

			// session 맵에 세션 추가(sessionId : session)
			sessions.put(sessionId, newSession);
			room.setSessions(sessions);

		}

		log.info("sessions, size:{}", sessions.size());
		// 해당 채팅룸의 모든 세션에 메시지전송
		sessions.values().parallelStream().forEach(session -> {
			sendMessage(session, chatMessage);
		});

	}

	// 채팅 메시지 보내기 sendMessage
	@Override
	public <T> void sendChatMessage(WebSocketSession session, TextMessage message) {

		log.info("sendChatMessage - message:{}", message);

		String payload = message.getPayload();
		String roomId = (String) session.getAttributes().get("roomId");
		
		// 해당 채팅룸찾기
		ChatRoom room = chatRoomRepository.findRoomById(roomId);
		// 해당 chatRoom에 접속한 세션들 가져오기
		Map<String, WebSocketSession> sessions = room.getSessions();
		
		log.info("sessions, size:{}", sessions.size());

		ChatMessage chatMessage;

		try {
			
			chatMessage = objectMapper.readValue(payload, ChatMessage.class);
			chatMessage.setType(MessageType.TALK);
			
			// 해당 채팅룸의 모든 세션에 메시지전송
			sessions.values().parallelStream().forEach(thisSession -> {
				sendMessage(thisSession, chatMessage);
			});
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

	}
	
	//send file Message
	//소켓 핸들 없는 과정 
	@Override
	public <T> void sendFileMessage(UploadFileList fileList) {
		
		UploadFile files = fileList.getUploadFileList().get(0);
		String[] splitName = files.getFileId().split("_");
		String roomId = splitName[0];
		log.info("roomId:{}", roomId);
		
		// 해당 채팅룸찾기
		ChatRoom room = chatRoomRepository.findRoomById(roomId);
		// 해당 chatRoom에 접속한 세션들 가져오기
		Map<String, WebSocketSession> sessions = room.getSessions();
				
		for(UploadFile file : fileList.getUploadFileList()) {
			String fileId = file.getFileId();
			String fileName = file.getFileName();
			String fileType = file.getFileType();
			String sender =  file.getSender();
			LocalDateTime date = file.getFileDatetime(); 
			String formatDate = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")); //2022/08/31/
			
			String path = "/static//lib/uploadfiles/";
			String msg = "File\n" 
					+ fileName +"\n"
					+ path + roomId + "/" + sender + "/" + formatDate + "/" + fileId;
			
			ChatMessage chatMessage = new ChatMessage(MessageType.FILE, sender, msg);
			// 해당 채팅룸의 모든 세션에 메시지전송
	     	sessions.values().parallelStream().forEach(thisSession -> {
	     		sendMessage(thisSession, chatMessage);
	     	});
			
		}
	
		
	};
	
	// 세션에 메시지 보내기
	// 채팅메시지 디비 저장
	@Override
	public <T> void sendMessage(WebSocketSession session, T message) {

		log.info("sendMessage - message:{}", message);

		try {
			session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
			
			// 메시지전송 후, test용 map에 저장
			String roomId = (String) session.getAttributes().get("roomId");
			
			// roomId로 해당 chatRoom정보 가져오기
			ChatRoom room = chatRoomRepository.findRoomById(roomId);
			
			// 채팅 메시지 저장
			// ArrayList<ChatMessage> roomChatMessages = new ArrayList<>();
			// 해당 chatRoom 내 메시지 리스트에 메시지 저장
			List<ChatMessage> chatMessages = room.getMessages();
			chatMessages.add((ChatMessage)message);
	
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	
	// ?맵 키
	// chatRoom 퇴장시
	// 본인 세션 지우기
	// 남은 세션(참여)없으면 방 삭제
	@Override
	public <T> void removeSession(WebSocketSession session) {

		log.info("removeSession - sessionId:{}", session.getId());

		String sessionId = session.getId();
		String roomId = (String) session.getAttributes().get("roomId");
		String userId = (String) session.getAttributes().get("userId");
		// 퇴장한 chatRoom 찾기
		ChatRoom room = chatRoomRepository.findRoomById(roomId);
		// 세션들 가져오기
		Map<String, WebSocketSession> sessions = room.getSessions();
		
		// 세션들에 해당 세션id 있는지 여부
		if (sessions.containsKey(sessionId)) { // 있으면,
			sessions.remove(sessionId); // 해당 세션 삭제
		}
		
		if (sessions.size() == 0) { // 삭제 후, 남은 세션 없으면,
			chatRoomRepository.removeRoom(roomId); // chatRoom 삭제
		}
		
		// 남은 세션에 퇴장 메시지 전송 
		ChatMessage chatMessage = new ChatMessage(MessageType.EXIT, userId, "퇴장");
		sessions.values().parallelStream().forEach(thisSession -> {
			sendMessage(thisSession, chatMessage);
		});

	}

}