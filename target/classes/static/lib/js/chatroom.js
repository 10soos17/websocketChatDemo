//===

var ws = null;

$( document ).ready(function() {
    console.log( "ready!" );
	
	//=================SOCKET
	//========방 입장시 
	function openSocket() {
		//BE interceptor에서 handshake전에, userId 값과 roomId 세션에 저장하기위해 param 넣어줌
		var chat_userId = $('#chat_userId').val();
		var chat_roomTitle = $('#chat_roomTitle').val();
		
		var serverUrl = "ws://localhost:8080/ws/chat";//10.10.36.54
		
		ws = new WebSocket(serverUrl+"?roomId="+chat_roomTitle + "&userId="+chat_userId);
		
		console.log("enter, ws state:", ws.readyState);
		console.log("chat_userId: ", chat_userId,", chat_roomId: ",chat_roomTitle);
		
		ws.addEventListener('open', (e)=>{
			console.log('OPENED');
			
			//
			ws.onclose = onClose;
	        ws.onmessage = onMessage;
	        ws.onerror = onError;
	        
			$('#sendMessage').removeAttr('disabled');
		   	$('#sendBtn').removeAttr('disabled');
			
		});
		
		
        // 서버에 접속이 되면 호출되는 콜백함수
        ws.onopen = function () {
            console.log('Info: WebSocket connection opened.\n');
            // 채팅입력창에 메시지를 입력하기 위해 키를 누르면 호출되는 콜백함수
            document.getElementById('sendMessage').onkeydown = function(event) {
                // 엔터키가 눌린 경우, 서버로 메시지를 전송함
                if (event.keyCode == 13) {
                   submitMessage;
                }
            };
        };

		//
		ws.addEventListener('onclose', (e)=>{
			ws.close();
			});
		
		}
	
	   var close = function() {
	        if (ws) {
	            console.log('CLOSING ...');
	            ws.close();
	        }
	    };
	
	    var onClose = function() {
	        console.log('CLOSED');
	        ws = null;
	        // 채팅 입력창 이벤트를 제거함
	        document.getElementById('sendMessage').onkeydown = null;
	        //reset();
	    };
		
	    var onMessage = function(event) {
						console.log("onMessage");
	        var data = event.data;
	        addMessage(data);
	    };
	
	    var onError = function(event) {
	        alert(event.type);
	    };


	//===========sendBtn 클릭시-> 메시지나 파일 보낼때
	$('#sendBtn').click(function(e) {
		console.log("sendClick");
		submitMessage();
	});
		    
	//===========메세지 소켓 전송 전에 (ws.send() 전에) 
	var submitMessage = function() {
				console.log("submitMessage");
		var sendMessage = $('#sendMessage');
		var chat_roomTitle = $('#chat_roomTitle');
		var chat_userId = $('#chat_userId');
		
		msg = sendMessage.val();
		var dt = new Date();
		
		console.log("sender:",chat_userId.val());
		
		var uploadfile = $('#uploadfile');
		var file = document.getElementById('uploadfile').files[0];
		
		console.log("filename:", uploadfile.val(),"file:", file);
		
		//메시지 내용 없고, 파일첨부도 없을경우
		if(sendMessage.val() == "" && uploadfile.val() == ""){
			alert("write message");
		}
		//파일첨부가 있을 경우
	    if(uploadfile.val() != ""){
			fetchFile();
		}
		//메시지 내용 있을 경우
		if(sendMessage.val() != ""){
			
			var obj = JSON.stringify(
					{
						roomId: chat_roomTitle.val(), //입장한 방ID
						sender: chat_userId.val(), //userID
						message: msg, //sendMessage
						timestamp: dt.valueOf()
					})
					
		    ws.send(obj);
		    sendMessage.val("");
		}
	    
	}

	//============파일 전송
	function fetchFile(){
		const formData = new FormData();
		
		var chat_roomTitle = $('#chat_roomTitle').val();
        var chat_userId = $('#chat_userId').val();
        
		formData.append('roomId', chat_roomTitle);
		formData.append('sender', chat_userId);
		
		// 파일 여러 개 넘길 때
		let uploadfiles = document.getElementById("uploadfile").files;
		for (let i = 0; i < uploadfiles.length; i++) {
			formData.append("files", uploadfiles[i]);

		}

		fetch('/chat/uploadFiles', {
		  method: 'POST',
		  headers: {"X-Requested-With": "XMLHttpRequest"},
		  body: formData,
		})
		.then((response) => response)
		.then((result) => {
		  alert("the File has been transferred.");
		  console.log('성공:', result);
		})
		.catch((error) => {
		  console.error('실패:', error);
		  //파일 전송 실패 후, 메시지 전송
		});
	}	

	//====================메시지 전송 후, 화면에 그리기 
 	var addMessage = function(data) {
			
		console.log("addMessage:",data);
		var chat_roomTitle = $('#chat_roomTitle').val();
		var chat_userId = $('#chat_userId').val();
		
		var jsonData = JSON.parse(data);
        
		console.log("sender:",jsonData.sender,": user:",chat_userId, "message:",jsonData.message);

		var messages = $('#messages');

		var type = jsonData.type;
		var sender = jsonData.sender;
		var msg = jsonData.message;
		var writeDate = jsonData.writeDate;
		var dateList = writeDate.split("T");
		//var timeList = dateList[1].split(".")
		writeDate = dateList[0];

		var textAlign = "right";
		var text = msg +", " + writeDate;
		
		var sendMesssage = null;

		if(chat_userId != sender){
			textAlign = "left";
			text = sender + ": "+ text;
			
		}

        messages.append(`<br>`);
        
		if(type == "ENTER" || type == "EXIT"){
			textAlign = "center";
			sendMessage = 
			`<div>
				<input type="button" value="${text}"; 
				style="float:${textAlign};text-align:${textAlign};border:0px;border-radius:7px;solid; 
		        background:rgb(255, 215, 0,0.7);color:black;margin-left:5px;margin-right:5px;">
		        </input>
	        </div>`;
	        
			messages.append(sendMessage);
		}
		if(type == "TALK"){
			sendMessage = 
			`<div>
				<input type="button" value="${text}"; 
				style="float:${textAlign};text-align:${textAlign};border:0px;border-radius:7px;solid; 
		        background:rgb(255, 215, 0,0.7);color:black;margin-left:5px;margin-right:5px;">
		        </input>
	        </div>`;
	        
	        
       		messages.append(sendMessage);
		}
		if(type == "FILE"){
			var lines = msg.split("\n");
			console.log(lines[0]+"============"+lines[1]+"========"+lines[2]);
			//text = sender + "파일전송, " + writeDate;
			
			//test - 링크로열기
			//messages.append('<br>');
			//var link = `<a href="${lines[2]}" target="_blank">`+"file link"+`</a>`
			//messages.append(link);
			
			//restApi download
			console.log("path:"+lines[2]);
			var fileName = lines[1];
			var filePath = lines[2];
			var path = `http://localhost:8080/chat/downloadFiles?path=${filePath}`;
			
			sendMessage = 
			`<div>
				<a style="float:${textAlign};text-align:${textAlign};border:0px;border-radius:3px;solid; 
		        background:rgb(255, 215, 0,0.7);color:black;margin-left:5px;margin-right:5px;font-size:13px;" 
		        href="${path}" download="${fileName}">`+fileName+"download"+`
	        	</a>
	        </div>`;
	        
			messages.append(sendMessage);
				
		}
		
/*
        var msgBox = messages.get(0);

        while (msgBox.childNodes.length > 1000) {
            msgBox.removeChild(msgBox.firstChild);
        }
        */
        //msgBox.scrollTop = msgBox.scrollHeight;
        // $("#messageTop").scrollTop($("#messageTop").scrollHeight);
         $("#messageTop").scrollTop = $("#messageTop").scrollHeight;

    };
	
	//###나중에 수정
	//==========exit room - socket close && FE 나온방 화면에서 사라지도록 
	this.exitRoom = function(){
		close();
		//window.opener.location.reload(true);
		mainReset();
		window.close();
	}
	//==========
	function mainReset(){
		//var userId = $('#chat_userId');
		//window.opener.document.getElementById("userId").value = userId.val();
		//window.opener.document.getElementById("userId").disabled = true;
		
		var messages = $('#messages');
		messages.html('');
	}
	
	//========= pop 창 열었을 때, 세팅 
	//========= socket open
	this.receiver = function(){
		var chat_userId = $('#chat_userId');
		var chat_roomTitle = $('#chat_roomTitle');	
		
		var prevUserId = chat_userId.val();
		var prevRoomTitle = chat_roomTitle.val();
		
		var nextUserId = window.opener.document.getElementById("userId").value;
		var nextRoomTitle = window.opener.document.getElementById("roomTitle").value;
	
		$("#chat_userId").val(nextUserId);
		$("#chat_roomTitle").val(nextRoomTitle);
	
		console.log("chat__________",$("#chat_userId").val());
		console.log("chat__________",$("#chat_roomTitle").val());
				
		if(ws == null){
			console.log("open");
			openSocket();
		}
	}


}); 



	/*
	//=========delete room
	function deleteRoom(){
		var roomId = $('#chat_roomTitle').val();
		
		//window.opener.document.getElementById("roomTitle").value = roomId;
		console.log("deleteroom: "+roomId);
		//document.querySelector("#"+roomId).remove();
		window.opener.document.getElementById(roomId).remove();
	}*/
