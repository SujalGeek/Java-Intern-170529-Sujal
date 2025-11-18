package com.fitness.userservice.repository;

import com.fitness.userservice.models.User;
import org.springframework.data.mongodb.repository.MongoRepository; // ADD THIS
//import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User,String> {

    boolean existsByEmail(String email);
}
