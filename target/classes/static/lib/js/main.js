$( document ).ready(function() {
    console.log( "ready!" );
    
    
	//setInterval(this.init, 3000);
	
	//==============================init
	//========기존 생성된 방 가져오기 
	//window.addEventListener('DOMContentLoaded', init);
	this.init = function(){
	
		$.ajax({
			data: {},
			dataType: 'json',
			contentType: 'application/json; charset=utf-8',
	        type: 'GET',
			url: "http://localhost:8080/chat/findAllRoom",
			success: function(data){
				//console.log("success, data: ",data);
				deleteRoomList();
				initRoomList(data);			
			},
			error: {
				//...
			}
		});
	}

	//=========init room list
	function initRoomList(data){
		
		var addRoomList = $('#addRoomList');
		
		console.log("initRoomList, 방 개수: ", data.chatRoomList.length);
		
		for(var item in data.chatRoomList){
			var roomId = data.chatRoomList[item].roomId;
			var ownerId = data.chatRoomList[item].ownerId + "   방";
			
			var writeDate = data.chatRoomList[item].writeDate;
			var dateList = writeDate.split("T");
			var timeList = dateList[1].split(".")
			writeDate = dateList[0] + " " + timeList[0];

			var room =`<div id="${roomId}" value="${ownerId}, ${writeDate}" onclick="connectRoom(this);"
	                style="width:350px;height:25px;margin-bottom:2%;margin-left: 14%;
	                border:1px solid gold;border-radius:5px;
	                background:rgb(0,0,0,0);color:white;">
	                <span style="width:200px;height:25px;float:left;margin-left:3%;overflow:hidden;">${ownerId}</span>
	                <span style="float:right;margin-right:2%;line-height:300%;font-size:10px;">${writeDate}</span>
	                </div>`
			addRoomList.append(room);
		}
	}
	
	
	//=========create room
	this.createRoom = function(){
		var userId = $('#userId');
		userId.val();//로그인한 사용자 
		console.log("User: ",userId.val());
		//var dt = new Date();
		//var list = dt.toLocaleString('en-CA').split(', ');
		//var timeList = list[1].split(' p.m.');
		//dt = list[0]+timeList[0];
	
		var reqObject = { ownerId: userId.val()};
		var requestData = JSON.stringify(reqObject);
		
		$.ajax({
			data: requestData,
			dataType: 'json',
			contentType: 'application/json; charset=utf-8',
	        type: 'POST',
			url: "http://localhost:8080/chat/createRoom",
			success: function(data){
				addRoom(data);			
			},
			error: {
				//...
			}
		});
	}
	
	//=========add room
	function addRoom(data){
		
		var addRoomList = $('#addRoomList');
		var roomId = data.roomId;
		var ownerId =  data.ownerId + "   방";;
		var writeDate = data.writeDate;
		
		var dateList = writeDate.split("T");
		var timeList = dateList[1].split(".")
		writeDate = dateList[0] + " " + timeList[0];
		
		var userId = $('#userId');
		
			var room =`<div id="${roomId}" value="${ownerId}, ${writeDate}" onclick="connectRoom(this);"
	                style="width:350px;height:25px;margin-bottom:2%;margin-left: 14%;
	                border:1px solid gold;border-radius:5px;
	                background:rgb(0,0,0,0);color:white;">
	                <span style="width:200px;height:25px;float:left;margin-left:3%;overflow:hidden;">${ownerId}</span>
	                <span style="float:right;margin-right:2%;line-height:300%;font-size:10px;">${writeDate}</span>
	                </div>`
	                
	    addRoomList.append(room);
	    
	    //방만들면 본인 아이디 -> 입장시, 그대로 사용
	    userId.attr("disabled", true);
	}
	
	function deleteRoomList(){
		var addRoomList = $('#addRoomList');
		for(var room in addRoomList.children()){
			console.log("room:",room,room.id);
			room.remove;
			
		}
	}
	
	
	//===========connect room
	//===========popup
	
	var popup =null;
	
	this.connectRoom = function(data){
		
		console.log("connectRoom: ",data.id);
		
		var userId = $('#userId');
		var roomTitle = $('#roomTitle');
		
		if(userId.val() == ""){
			alert("아이디를 입력해주세요.");
			return;
		}
		
		roomTitle.val(data.id);//룸스크린 value = 입장한 roomId
		console.log("roomTitle:",roomTitle.val());
		

		//if(!popup || popup.closed){
		var url = "./chatroom.html";
		var windowsName = "chat";
		var options = "width=500, height=730,scrollbar=no";

		popup = window.open(url, windowsName, options);
			
			//popup.close = function(e){
				
				//this.init;
			//}
		//}
	}
	

}); 