package com.example.Spring_Mock.service;

import com.example.Spring_Mock.model.User;
import com.example.Spring_Mock.repository.UserRepository;
import com.example.Spring_Mock.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private UserService userService;

    @Test
    void testGetUserById(){
        when(userRepository.findById(1)).thenReturn(Optional.of(new User(1,"Sujal")));

        User result = userService.getUserById(1);

        assertEquals("Sujal",result.getName());
    }
}
