package ru.ifmo.se.service.api;

import ru.ifmo.se.web.model.UserRegisterRequestDto;

public interface UserService {
    void registerNewUser(UserRegisterRequestDto userRegisterRequestDto);
}
