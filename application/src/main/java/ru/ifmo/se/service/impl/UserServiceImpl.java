package ru.ifmo.se.service.impl;

import ru.ifmo.se.database.repository.UserRepository;
import ru.ifmo.se.service.api.UserService;
import ru.ifmo.se.web.model.UserRegisterRequestDto;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    UserRepository userRepository;

    @Override
    public void registerNewUser(UserRegisterRequestDto userRegisterRequestDto) {

    }
}
