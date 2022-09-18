package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class FluxChatroomController {

/*
	@RequestMapping(method = RequestMethod.GET, value="/fluxChat/roomlist")
	public Mono<ResponseEntity<ChatroomList>> getChatroomlist(){//@RequestParam String mail){
		log.debug("test");

		Mono<Long> delay = Mono.delay(Duration.ofMillis(500));
		Flux<Integer> integerFlux = delay.flatMapMany(t->{
			return Flux.range(1,5);
		});
		Flux<Chatroom> chatroomFlux = integerFlux.map(i->{
			return Chatroom.builder().roomId("room"+i).userId("email"+i).build();
		});


		Mono<List<Chatroom>> chatroomListMono = chatroomFlux.collectList();
		Mono<ResponseEntity<ChatroomList>> ret = chatroomListMono.map(mapper->{
			return ResponseEntity.ok().body(ChatroomList.builder().chatroomList(mapper).build());
		});

//		Mono.just(args)
//		.flatMap(param->{
//			Flux<Data> data = service.select(param);
//			return data;
//		})
//		.collectList()	//Mono<List<Data>>
//		.map(mapper->{
//			return ResponseEntity.ok.body(mapper);
//		})

		return ret;

//		 return Mono.delay(Duration.ofMillis(500))
//			.flatMapMany(t->Flux.range(1,5))
//			.map(i->Chatroom.builder().roomId("room"+i).email("email"+i).build())
//			.collectList()
//			.map(mapper->ResponseEntity.ok().body(ChatroomList.builder().chatroomList(mapper).build()));
	}
	*/
}
