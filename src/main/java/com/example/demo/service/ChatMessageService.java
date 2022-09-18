package com.example.demo.service;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.example.demo.model.UploadFileList;

public interface ChatMessageService {

    public <T> void sendEnterMessage(WebSocketSession session);
    public <T> void sendChatMessage(WebSocketSession session, TextMessage message);
    public <T> void sendFileMessage(UploadFileList fileList);
    
    public <T> void sendMessage(WebSocketSession session,T message);

	public <T> void removeSession(WebSocketSession session);
}
