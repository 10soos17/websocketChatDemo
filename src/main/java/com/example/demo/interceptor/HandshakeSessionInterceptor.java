package com.example.demo.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HandshakeSessionInterceptor extends HttpSessionHandshakeInterceptor{// extends HttpSessionHandshakeInterceptor

	// 핸드세이크 전
	// 소켓 처음 연결(=채팅룸 입장시)되면, 세션에 roomId, userId 속성값 개별 설정
	// attributes 에 값을 저장하면, 웹소켓 핸들러 클래스의 WebSocketSession에 전달됨
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		
		log.info("Before handshake:{}",request);

		ServletServerHttpRequest ssreq = (ServletServerHttpRequest) request;
		HttpServletRequest req = ssreq.getServletRequest();

		String userId = req.getParameter("userId");
		String roomId = req.getParameter("roomId");

		log.info("userId:{},roomId:{}",userId,roomId);

		attributes.put("userId", userId);
		attributes.put("roomId", roomId);

		return super.beforeHandshake(request, response, wsHandler, attributes);
	}

	//핸드세이크 후
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception ex) {

		log.info("after Handshake:{}", request);
		
		super.afterHandshake(request, response, wsHandler, ex);
	}
}
