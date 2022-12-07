package ru.ifmo.se.service.api.storage;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

public interface TempImageStorageService {
    UUID createStorage();
    void storeImage(UUID storageId, BufferedImage bufferedImage);
    List<BufferedImage> getAllAndDeleteStorage(UUID storageId);
}
