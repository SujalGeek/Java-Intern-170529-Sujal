package com.example.Spring_Mock.controller;

import com.example.Spring_Mock.model.User;
import com.example.Spring_Mock.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import static org.mockito.Mockito.when;

@WebMvcTest(UserController.class)
public class UserControllerTest {


    @Autowired
    private MockMvc mockmvc;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private UserService userService;


    @Test
    void testGetUserEndpoint() throws Exception{
        when(userService.getUserById(1)).thenReturn(new User(1,"Sujal"));
        mockmvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sujal"));
    }
}
