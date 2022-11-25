package ru.ifmo.se.web.model;

import lombok.Data;

@Data
public class UserRegisterRequestDto {
    private String username;
    private String password;
}
