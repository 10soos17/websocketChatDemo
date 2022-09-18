package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.demo.handler.WebSocketSendHandler;
import com.example.demo.interceptor.HandshakeSessionInterceptor;

//handshake interceptor 활성화

//custom한 handler 활성화 : /ws/chat
//websocket 접속위한 endpoint 설정
//CORS 설정 : 도메인이 다른 서버에서도 접속 가능하도록

//@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

	@Autowired
	HandshakeSessionInterceptor handshakeSessionInterceptor;

	@Autowired
	WebSocketSendHandler webSocketSendHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(webSocketSendHandler, "/ws/chat").addInterceptors(handshakeSessionInterceptor)
		//addHandler(webSocketEnterHandler, "/ws/enter")
		.setAllowedOrigins("*");
//		.setAllowedOriginPatterns("*");
//		.withSockJS();
	}

}
