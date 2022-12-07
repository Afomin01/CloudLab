package ru.ifmo.se.service.api;

import java.io.InputStream;
import java.util.UUID;

public interface FilesService {
    InputStream getDatasetStream(String username, UUID taskUuid);
}
