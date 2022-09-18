package com.example.demo.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


// 업로드된 파일
@Getter
@Setter
public class UploadFile {
	
	private String fileId; // UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + fileType
	
	private String fileName;
	
	private String fileType;
	
	private String sender; // 메시지 보낸사람
	
	@Builder.Default
	private LocalDateTime fileDatetime = LocalDateTime.now();
	
	public UploadFile(String fileId, String fileName, String fileType, String sender) {
		this.fileId = fileId;
		this.fileName = fileName;
		this.fileType = fileType;
		this.sender = sender;
	}

}
