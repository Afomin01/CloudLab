package ru.ifmo.se.service.impl;

import ru.ifmo.se.configuration.UserRolesConstants;
import ru.ifmo.se.configuration.model.DiskShareWrapper;
import ru.ifmo.se.database.model.UserEntity;
import ru.ifmo.se.database.repository.UserRepository;
import ru.ifmo.se.exception.InputValidationException;
import ru.ifmo.se.properties.ApplicationProperties;
import ru.ifmo.se.service.api.AdminService;
import ru.ifmo.se.web.model.admin.UserBlockRequestDto;
import ru.ifmo.se.web.model.admin.UserChangeQuotaRequestDto;
import ru.ifmo.se.web.model.admin.UserRegisterRequestDto;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class AdminServiceImpl implements AdminService {

    @Inject
    UserRepository userRepository;

    @Inject
    DiskShareWrapper diskShareWrapper;

    @Inject
    ApplicationProperties applicationProperties;

    @Override
    @Transactional
    public void registerNewUser(UserRegisterRequestDto requestDto) {
        UserEntity userEntity = new UserEntity();

        userEntity.setId(UUID.randomUUID());
        userEntity.setUsername(requestDto.getUsername());
        userEntity.setPassword(requestDto.getPassword());
        userEntity.setRole(UserRolesConstants.USER);
        userEntity.setQuota(requestDto.getQuota() == null ? applicationProperties.user().defaultQuota() : requestDto.getQuota());
        userEntity.setBotToken(UUID.randomUUID().toString());

        userRepository.persistAndFlush(userEntity);

        diskShareWrapper.getDiskShare().mkdir("/" + userEntity.getId());
    }

    @Override
    @Transactional
    public void blockUser(UserBlockRequestDto requestDto) {
        UserEntity userEntity = userRepository.findByName(requestDto.getUsername());
        if (userEntity == null) {
            throw new InputValidationException("No user with username " + requestDto.getUsername() + " found");
        }

        userEntity.setBlocked(true);
    }

    @Override
    @Transactional
    public void unblockUser(UserBlockRequestDto requestDto) {
        UserEntity userEntity = userRepository.findByName(requestDto.getUsername());
        if (userEntity == null) {
            throw new InputValidationException("No user with username " + requestDto.getUsername() + " found");
        }

        userEntity.setBlocked(false);
    }


    @Override
    @Transactional
    public void changeQuota(UserChangeQuotaRequestDto requestDto) {
        UserEntity userEntity = userRepository.findByName(requestDto.getUsername());
        if (userEntity == null) {
            throw new InputValidationException("No user with username " + requestDto.getUsername() + " found");
        }

        if (requestDto.getNewQuota() < 0) {
            throw new InputValidationException("Quota must be greater than 0");
        }

        userEntity.setQuota(requestDto.getNewQuota());
    }
}
