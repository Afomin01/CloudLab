package ru.ifmo.se.service.api;

import ru.ifmo.se.service.model.GenerationParameters;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.UUID;

public interface DatasetGeneratorService {
    void precessTask(UUID generationTaskUuid);

    BufferedImage createSingleImage(GenerationParameters generationParameters);
}
