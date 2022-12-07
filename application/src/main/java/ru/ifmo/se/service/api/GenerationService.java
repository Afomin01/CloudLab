package ru.ifmo.se.service.api;

import ru.ifmo.se.web.model.GenerationParametersRequestDto;

import java.awt.image.BufferedImage;

public interface GenerationService {
    BufferedImage createGenerationTask(GenerationParametersRequestDto requestDto);
}
