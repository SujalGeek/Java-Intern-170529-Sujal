package com.example.chat_app_backend.controllers;


import java.util.List; 

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat_backend.entities.Message;
import com.example.chat_backend.entities.Room;


import com.example.chat_app_backend.repository.RoomReposiotory;


@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

	// create room
	private RoomReposiotory roomReposiotory;
	
	
	public RoomController(RoomReposiotory roomReposiotory) {
		super();
		this.roomReposiotory = roomReposiotory;
	}

	@PostMapping
	public ResponseEntity<?> createRoom(@RequestBody String roomId)
	{
		if(roomReposiotory.findByRoomId(roomId)!= null)
		{
			return ResponseEntity.badRequest().body("Room already exists");
		}
		
		Room room = new Room();
		room.setRoomId(roomId);
		Room savedRoom = roomReposiotory.save(room);
		return ResponseEntity.status(HttpStatus.CREATED).body(room);
	}
	// get room
	
	@GetMapping("/{roomId}")
	public ResponseEntity<?> joinRoom(@PathVariable String roomId)
	{
		Room room = roomReposiotory.findByRoomId(roomId);
		if(room == null)
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Room not found!!");
		}
		
		return ResponseEntity.ok(room);
	}
	
	
	
	// get messages of room
	@GetMapping("/{roomId}/message")
	public ResponseEntity<List<Message>> getMessage(@PathVariable String roomId,
			@RequestParam(value = "page", defaultValue = "0",required = false) int page,
			@RequestParam(value = "size", defaultValue = "20", required = false) int size
			)
			{		
			Room room = roomReposiotory.findByRoomId(roomId);
			
			if(room==null)
			{
				return ResponseEntity.badRequest().build();
			}
			
			
			List<Message> messages = room.getMessages();
			int start = Math.max(0,messages.size()-(page+1)*size);
			int end= Math.min(messages.size(), start+size);
			List<Message> paginatedMessages = messages.subList(start, end);
			return ResponseEntity.ok(paginatedMessages);
			
			}

	
}
