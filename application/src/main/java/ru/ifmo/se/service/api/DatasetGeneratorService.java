package ru.ifmo.se.service.api;

import ru.ifmo.se.service.model.GenerationParameters;

import java.awt.image.BufferedImage;
import java.util.UUID;

public interface DatasetGeneratorService {
    void precessTask(UUID generationTaskUuid);

    BufferedImage createSingle1DImage(GenerationParameters generationParameters);

    BufferedImage createSingle2DImage(GenerationParameters generationParameters);
}
