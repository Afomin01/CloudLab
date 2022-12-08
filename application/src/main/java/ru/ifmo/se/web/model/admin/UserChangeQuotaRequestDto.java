package ru.ifmo.se.web.model.admin;

import lombok.Data;

@Data
public class UserChangeQuotaRequestDto {
    private String username;
    private int newQuota;
}
