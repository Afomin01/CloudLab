package ru.ifmo.se.service.impl;

import ru.ifmo.se.configuration.UserRolesConstants;
import ru.ifmo.se.configuration.model.DiskShareWrapper;
import ru.ifmo.se.database.model.UserEntity;
import ru.ifmo.se.database.repository.UserRepository;
import ru.ifmo.se.service.api.UserService;
import ru.ifmo.se.web.model.UserRegisterRequestDto;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    DiskShareWrapper diskShareWrapper;

    @Override
    @Transactional
    public void registerNewUser(UserRegisterRequestDto requestDto) {
        UserEntity userEntity = new UserEntity();

        userEntity.setId(UUID.randomUUID());
        userEntity.setUsername(requestDto.getUsername());
        userEntity.setPassword(requestDto.getPassword());
        userEntity.setRole(UserRolesConstants.USER);

        userRepository.persistAndFlush(userEntity);

        diskShareWrapper.getDiskShare().mkdir("/" + userEntity.getId());
    }
}
