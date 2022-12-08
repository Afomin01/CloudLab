package ru.ifmo.se.service.api;

import ru.ifmo.se.web.model.admin.UserBlockRequestDto;
import ru.ifmo.se.web.model.admin.UserChangeQuotaRequestDto;
import ru.ifmo.se.web.model.admin.UserRegisterRequestDto;

public interface AdminService {
    void registerNewUser(UserRegisterRequestDto userRegisterRequestDto);

    void blockUser(UserBlockRequestDto userRegisterRequestDto);

    public void unblockUser(UserBlockRequestDto requestDto);

    void changeQuota(UserChangeQuotaRequestDto userRegisterRequestDto);
}
