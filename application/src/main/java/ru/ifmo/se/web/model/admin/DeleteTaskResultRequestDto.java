package ru.ifmo.se.web.model.admin;

import lombok.Data;

import java.util.UUID;

@Data
public class DeleteTaskResultRequestDto {
    private UUID generationTaskId;
    private String username;
}
