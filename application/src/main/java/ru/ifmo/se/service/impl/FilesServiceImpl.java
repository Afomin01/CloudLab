package ru.ifmo.se.service.impl;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import ru.ifmo.se.configuration.model.DiskShareWrapper;
import ru.ifmo.se.database.model.GenerationStatus;
import ru.ifmo.se.database.model.GenerationTaskEntity;
import ru.ifmo.se.database.model.UserEntity;
import ru.ifmo.se.database.repository.GenerationTaskRepository;
import ru.ifmo.se.database.repository.UserRepository;
import ru.ifmo.se.exception.InputValidationException;
import ru.ifmo.se.service.api.FilesService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class FilesServiceImpl implements FilesService {

    @Inject
    UserRepository userRepository;

    @Inject
    GenerationTaskRepository generationTaskRepository;

    @Inject
    DiskShareWrapper diskShareWrapper;

    @Override
    public InputStream getDatasetStream(String username, UUID taskUuid) {
        UserEntity user = userRepository.findByName(username);
        GenerationTaskEntity generationTask = generationTaskRepository.findById(taskUuid);

        if (generationTask == null || !Objects.equals(generationTask.getUser().getId(), user.getId())) {
            throw new InputValidationException("No such task.");
        }

        if (GenerationStatus.ERROR.equals(generationTask.getStatus())) {
            throw new InputValidationException("Error occurred while generating dataset. No dataset file can be downloaded. ");
        }

        if (!GenerationStatus.COMPLETED.equals(generationTask.getStatus())) {
            throw new InputValidationException("Generation is still in progress. ");
        }

        DiskShare diskShare = diskShareWrapper.getDiskShare();

        File datasetZip = diskShare.openFile(
                user.getId().toString() + "/" + generationTask.getId().toString() + ".zip",
                EnumSet.of(AccessMask.FILE_READ_DATA),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null
        );

        return datasetZip.getInputStream();
    }
}
