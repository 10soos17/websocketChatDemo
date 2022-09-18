package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.service.ChatMessageService;
import com.example.demo.service.ChatMessageServiceImpl;
import com.example.demo.service.ChatRoomRepository;
import com.example.demo.service.ChatRoomRepositoryImpl;

@Configuration
public class AppConfig{

	@Bean
	public ChatRoomRepository chatRoomRespository(){
		return new ChatRoomRepositoryImpl();
	}

	@Bean
	public ChatMessageService chatMessageService() {
		return new ChatMessageServiceImpl();
	}

}
