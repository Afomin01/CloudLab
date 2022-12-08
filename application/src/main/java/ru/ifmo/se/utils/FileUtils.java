package ru.ifmo.se.utils;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class FileUtils {
    public String getDatasetFileName(UUID userId, UUID taskId) {
        return userId.toString() + "/" + taskId.toString() + ".zip";
    }
}
