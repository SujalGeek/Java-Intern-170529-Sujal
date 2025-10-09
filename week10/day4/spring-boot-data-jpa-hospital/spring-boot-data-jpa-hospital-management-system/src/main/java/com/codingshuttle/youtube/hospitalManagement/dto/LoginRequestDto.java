package com.codingshuttle.youtube.hospitalManagement.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    private String username;
    private String password;
}
