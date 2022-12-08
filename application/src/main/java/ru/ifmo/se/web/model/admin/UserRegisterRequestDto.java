package ru.ifmo.se.web.model.admin;

import lombok.Data;

@Data
public class UserRegisterRequestDto {
    private String username;
    private String password;
    private Integer quota;
}
