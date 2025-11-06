package com.example.Spring_Mock.repository;

import com.example.Spring_Mock.model.User;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UserRepository {

    public Optional<User> findById(int id) {
        if (id == 1) {
            return Optional.of(new User(1, "Sujal"));
        } else if (id == 2) {
            return Optional.of(new User(2, "Rahul"));
        }
        return Optional.empty();
    }
}
