package ru.ifmo.se.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ifmo.se.database.model.GenerationStatus;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationTaskResponseDto {
    private UUID id;
    private Instant creationTime;
    private GenerationStatus status;
}
