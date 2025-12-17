package com.becoder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.becoder.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
