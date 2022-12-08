package ru.ifmo.se.service.api;

import com.hierynomus.smbj.share.File;
import ru.ifmo.se.database.model.GenerationTaskEntity;
import ru.ifmo.se.database.model.UserEntity;

import java.util.UUID;

public interface FilesService {
    File getDatasetFile(String username, UUID taskUuid);

    void deleteDataset(UserEntity user, GenerationTaskEntity generationTask);
}
