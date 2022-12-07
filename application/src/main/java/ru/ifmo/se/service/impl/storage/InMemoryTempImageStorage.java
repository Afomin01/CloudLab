package ru.ifmo.se.service.impl.storage;

import ru.ifmo.se.service.api.storage.TempImageStorageService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Named("in-memory")
@ApplicationScoped
public class InMemoryTempImageStorage implements TempImageStorageService {

    private final Map<UUID, List<BufferedImage>> storage = new ConcurrentHashMap<>();

    @Override
    public UUID createStorage() {
        UUID storageId = UUID.randomUUID();
        storage.put(storageId, new ArrayList<>());
        return storageId;
    }

    @Override
    public void storeImage(UUID storageId, BufferedImage bufferedImage) {
        storage.get(storageId).add(bufferedImage);
    }

    @Override
    public List<BufferedImage> getAllAndDeleteStorage(UUID storageId) {
        return storage.remove(storageId);
    }
}
