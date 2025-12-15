package com.example.chat_app_backend.controllers;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

// Fixed Import: Matches the corrected package names
import com.example.chat_app_backend.playload.MessageRequest;
import com.example.chat_app_backend.entities.Message;
import com.example.chat_app_backend.entities.MessageType;
import com.example.chat_app_backend.entities.Room;
import com.example.chat_app_backend.repository.RoomRepository;

import java.time.LocalDateTime;

@Controller
@CrossOrigin("http://localhost:5173")
public class ChatController {

    private RoomRepository roomRepository;

    public ChatController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    //for sending and receiving messages
    @MessageMapping("/sendMessage/{roomId}") // /app/sendMessage/roomId
    @SendTo("/topic/room/{roomId}") //subscribe
    public Message sendMessage(
            @DestinationVariable String roomId,
            @RequestBody MessageRequest request
    ) {

        Room room = roomRepository.findByRoomId(request.getRoomId());
        
        Message message = new Message();
        message.setContent(request.getContent());
        message.setSender(request.getSender());
        message.setTimeStamp(LocalDateTime.now());
        
        if (room != null) {
            room.getMessages().add(message);
            roomRepository.save(room);
        } else {
            throw new RuntimeException("room not found !!");
        }

        if(request.getType() != null)
        {
        	message.setType(request.getType());
        }
        else {
        	message.setType(MessageType.TEXT);
        }
        
        
        if(room !=  null)
        {
        	room.getMessages().add(message);
        	roomRepository.save(room);
        }
        else {
        	throw new RuntimeException("room not found!!");
        }
        
        return message;
    }
}