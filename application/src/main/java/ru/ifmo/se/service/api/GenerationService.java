package ru.ifmo.se.service.api;

import ru.ifmo.se.web.model.GenerationTaskParameters1DRequestDto;
import ru.ifmo.se.web.model.GenerationTaskParameters2DRequestDto;

import java.awt.image.BufferedImage;

public interface GenerationService {
    void createGenerationTask1D(GenerationTaskParameters1DRequestDto requestDto, String username);

    BufferedImage createTestImage1D(GenerationTaskParameters1DRequestDto requestDto);
    void createGenerationTask2D(GenerationTaskParameters2DRequestDto requestDto, String username);

    BufferedImage createTestImage2D(GenerationTaskParameters2DRequestDto requestDto);
}
