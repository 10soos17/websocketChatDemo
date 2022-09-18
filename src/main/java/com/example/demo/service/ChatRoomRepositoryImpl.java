package com.example.demo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.ChatRoom;
import com.example.demo.model.ChatRoomList;
import com.example.demo.model.UploadFile;
import com.example.demo.model.UploadFileList;

import lombok.extern.slf4j.Slf4j;


@Slf4j
//@RequiredArgsConstructor
//@Component
//@Service
public class ChatRoomRepositoryImpl implements ChatRoomRepository{

	//Test용 룸 저장맵 -> 로그인시, key값 고려하기

	//룸 생성시마다, 저장 (roomId : chatRoom)
	//roomId : chatRoom -> ChatRoom에서 sessions맵(sessionId : session(userId, roomId 세팅되있음))
	private Map<String, ChatRoom> chatRooms ;

	@PostConstruct
	private void init() {
		chatRooms = new LinkedHashMap<>();
	}

	// 방 생성
	@Override
	public ChatRoom createRoom(ChatRoom chatroom) {
		// roomId는 랜덤UUID로 만들고 저장
		String randomRoomId = UUID.randomUUID().toString();
		chatroom.setRoomId(randomRoomId);
		// test용 맵에 저장
		chatRooms.put(randomRoomId, chatroom);

		log.info("createRoom: {}: {}:{}", chatroom.getOwnerId(), chatroom.getRoomId(), chatroom.getWriteDate());

		return chatroom;
	}

	// roomId로 방 정보 가져오기
	@Override
	public ChatRoom findRoomById(String roomId) {
		log.debug("findRoomById");
		return chatRooms.get(roomId);
	}

	// 모든 방 가져오기
	// Q? userRoom만 보여줄때 -> roomId로 조회후, 각 챗룸에 본인 세션있는지 확인(지금걸로는)
	@Override
	public ChatRoomList findAllRoom() {

		log.debug("findAllRoom");
		
		List<ChatRoom> list = new ArrayList<>();//(chatRooms.values());
		for(String s : chatRooms.keySet()) {
			list.add(chatRooms.get(s));
		}
		//log.info("room:{}",list.get(0).getRoomId());
		ChatRoomList chatRoomList = new ChatRoomList(list);


		return chatRoomList;
	}

	// 방 삭제
	@Override
	public void removeRoom(String roomId) {
		
		log.debug("removeRoom");
		
		chatRooms.remove(roomId);
	}
	
	// upload file 시,
	// restApi(fe) -> 
	// 지정 경로에 파일 저장 
	// UploadFile 객체 저장
	// ChatRoom 안에 저장
	
	// 이후:
	// (response 받으면(fe) -> socket에 메시지 요청 ->) 과정 생략 
	// 컨트롤러에서 바로 message Service 호출 -> 세션들에 메시지 전송 -> 이후 동일
	
	@Override
	public UploadFileList uploadFiles(List<MultipartFile> files, String roomId, String sender) {
		log.debug("files:{}, sender:{}",files.size(), sender);
		//해당쳇룸
		ChatRoom room = findRoomById(roomId);
		//디비 저장하기위한 리스트
		List<UploadFile> list = room.getFiles();
		
		//리털할 리스트
		List<UploadFile> responseList = new ArrayList<>();
		//업로드파일
        for (MultipartFile file : files) {
            log.info("{},{},{}",file.getOriginalFilename(), file.getName(), file.getContentType());
			
            //저장경로만들기
			String fileName = file.getOriginalFilename();  //파일 저장
			String rootFolderName="/Users/soos/Desktop/ChatroomDemo2/src/main/resources/static/lib/uploadfiles/"
					+roomId+"/"+sender+"/";
			
					//"C:\\baplie_hub_project\\workspaces\\ChatroomDemo\\src\\main\\resources\\static\\lib\\uploadfiles\\"
					//				+roomId+"\\"+sender+"\\";
			
			// .jpg ( 확장자 뽑아냄! )
			//String[] names = fileName.split(".");
			//String name = names[0];
			String ext = fileName.substring(fileName.lastIndexOf("."));//"."+names[1];	
			String uuidName = UUID.randomUUID().toString();
			long currentTimeMillis = System.currentTimeMillis();
			String randomFileName = roomId + "_" + uuidName + "_" + currentTimeMillis + ext; //fileID로 
			
			// 날짜별로 폴더(오늘 날짜 폴더)
			Date today = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd"); 													
			String todayFolderName = sdf.format(today);
			String uploadFolderName = rootFolderName + todayFolderName;
			// 폴더 만드는 api
			File uploadFolder = new File(uploadFolderName);

			if (!uploadFolder.exists()) {
				uploadFolder.mkdirs();

			}
			// 최종 파일 경로+파일명은 saveFilePathName
			// rootFolderName+오늘날짜 + / + uuidName + _ + 지금시간 + 확장자
			String saveFilePathName = uploadFolderName + "//" + randomFileName;
			
			File saveFile = new File(saveFilePathName);   //저장할 파일
			
			try {
				file.transferTo(saveFile);

				//업로드파일객체 생성, 저장
				//디비 챗룸안에 저장
				UploadFile uploadFile = new UploadFile(randomFileName, fileName, ext, sender);
				list.add(uploadFile);
				room.setFiles(list);
				//리턴할 리스트
				responseList.add(uploadFile);
	
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            
        }
        
        UploadFileList uploadFileList = new UploadFileList(responseList);
        

       return uploadFileList;
		
	}
	
	@Override
	public ResponseEntity<byte[]> downloadFiles(String path){
		
		//localhost:8080/chat/downloadFiles?path=/static//lib/uploadfiles/
		// /static//lib/uploadfiles/88d01741-a6e2-4308-b2e6-28ced3f59852/ste/2022/09/01/88d01741-a6e2-4308-b2e6-28ced3f59852_aea51796-701a-46ba-be06-e068b0dc02cd_1661995135960.png

		String absPath = "/Users/soos/Desktop/ChatroomDemo2/src/main/resources/" +path;
				//"C:/baplie_hub_project/workspaces/ChatroomDemo/src/main/resources" + path;

		File file = new File(absPath);
		
		ResponseEntity<byte[]> result = null;

		HttpHeaders headers = new HttpHeaders();
		
		try {
			
			headers.add("Content-Type",Files.probeContentType(file.toPath()));
			
			result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file),
						headers,HttpStatus.OK);
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}

		return result;
	}
}
