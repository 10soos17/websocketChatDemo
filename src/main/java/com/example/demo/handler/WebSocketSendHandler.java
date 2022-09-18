package com.example.demo.handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.websocket.OnMessage;
import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.demo.service.ChatMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 1:N관계 맺을 때, 여러 클라이언트가 발송한 메시지 받아서 처리해주는 핸들러

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketSendHandler extends TextWebSocketHandler {

	@Autowired
	ChatMessageService chatMessageService;
	
	ByteArrayOutputStream bos;
    Writer writer;
    OutputStream stream;
   
	// socket 연결 성립시 = 방 입장인 경우
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("연결됨, sessionId{}:", session.getId());
		//log.info("userId: {}, roomId: {}", session.getAttributes().get("userId"), session.getAttributes().get("roomId"));
		TextMessage tx = null;
		// 세션서장 및 입장 메시지 전송
		chatMessageService.sendEnterMessage(session);
	}

	//  socket 연결 종료시
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.info("연결 종료됨, sessionId{}:", session.getId());
		//log.info("연결 종료됨, sessionId{}:", session.getAttributes().get("userId"));
		//해당 세션 삭제
		chatMessageService.removeSession(session);

	}
	// 메시지 핸들 
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		log.info("handleMessage: {}로부터 메시지 수신: {}", session.getId(), message.getPayload());
	
		super.handleMessage(session, message);
	}
	
	// 텍스트 메시지 전송시
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		log.info("handleTextMessage: {} 로부터 메시지 수신: {}", session.getId(), message.getPayload());
		//메시지 전송
		chatMessageService.sendChatMessage(session, message);
	}

//	@Override
//	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
//		log.info("handleBinaryMessage: {}로부터 메시지 수신: {}", session.getId(), message.getPayload());
//	}
//	
//	@Override
//	protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
//		log.info("handlePongMessage: {}로부터 메시지 수신: {}", session.getId(), message.getPayload());
//		super.handlePongMessage(session, message);
//	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		log.info("{} 익셉션 발생: {}", session.getId(), exception.getMessage());
	}


}
