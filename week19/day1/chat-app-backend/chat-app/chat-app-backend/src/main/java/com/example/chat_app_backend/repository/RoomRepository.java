package com.example.chat_app_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.chat_app_backend.entities.Room;

@Repository
public interface RoomRepository extends MongoRepository<Room, String>{

	Room findByRoomId(String roomId);
}
