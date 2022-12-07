package ru.ifmo.se.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import ru.ifmo.se.configuration.model.DiskShareWrapper;
import ru.ifmo.se.database.model.GenerationStatus;
import ru.ifmo.se.database.model.GenerationTaskEntity;
import ru.ifmo.se.database.repository.GenerationTaskRepository;
import ru.ifmo.se.service.api.DatasetGeneratorService;
import ru.ifmo.se.service.api.storage.TempImageStorageService;
import ru.ifmo.se.service.model.GenerationParameters;
import ru.ifmo.se.utils.MathUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ApplicationScoped
@Slf4j
public class DatasetGeneratorServiceImpl implements DatasetGeneratorService {

    @Inject
    MathUtils mathUtils;

    @Inject
    GenerationTaskRepository generationTaskRepository;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    TempImageStorageService tempImageStorageService;

    @Inject
    ManagedExecutor managedExecutor;

    @Inject
    DiskShareWrapper diskShareWrapper;

    @Scheduled(cron = "{app.scheduled.dataset-generation-task-start}")
    public void processNextTask() {
        List<GenerationTaskEntity> createdTasks = generationTaskRepository.findCreatedTasks();
        if (CollectionUtils.isNotEmpty(createdTasks)) {
            for (GenerationTaskEntity entity : createdTasks) {
                precessTask(entity.getId());
            }
            log.info("Started dataset generation for " + createdTasks.size() + " tasks.");
        }

        log.info("No new tasks for generation.");
    }

    @Override
    public void precessTask(UUID generationTaskUuid) {
        AtomicReference<GenerationParameters> generationParametersReference = new AtomicReference<>(null);
        AtomicReference<UUID> userIdReference = new AtomicReference<>(null);

        QuarkusTransaction.run(() -> {
            GenerationTaskEntity generationTask = generationTaskRepository.findById(generationTaskUuid, LockModeType.PESSIMISTIC_WRITE);

            if (generationTask != null) {
                if (generationTask.getStatus() != GenerationStatus.CREATED) {
                    return;
                }
                generationTask.setStatus(GenerationStatus.PRECESSING);
            }

            try {
                generationParametersReference.set(objectMapper.readValue(generationTask.getParameters(), GenerationParameters.class));
                userIdReference.set(generationTask.getUser().getId());
            } catch (Exception e) {
                generationTask.setStatus(GenerationStatus.ERROR);
                log.error("Unable to generate dataset for task: " + generationTask.getId() + "\n" + e.getMessage(), e);
            }
        });

        GenerationParameters generationParameters = generationParametersReference.get();

        if (generationParameters == null) {
            return;
        }

        UUID userId = userIdReference.get();

        boolean is1D = StringUtils.isEmpty(generationParameters.getEquationY());
        UUID storageId = tempImageStorageService.createStorage();

        managedExecutor.runAsync(() -> {
            try {
                log.info("Started dataset generation for task " + generationTaskUuid + ". Dataset size: " + generationParameters.getCount());
                if (is1D) {
                    for (int i = 0; i < generationParameters.getCount(); i++) {
                        BufferedImage bufferedImage = createSingle1DImage(generationParameters);
                        tempImageStorageService.storeImage(storageId, bufferedImage);
                    }
                } else {
                    for (int i = 0; i < generationParameters.getCount(); i++) {
                        BufferedImage bufferedImage = createSingle2DImage(generationParameters);
                        tempImageStorageService.storeImage(storageId, bufferedImage);
                    }
                }

                log.info("Finished dataset generation for task " + generationTaskUuid + ". Dataset size: " + generationParameters.getCount());
                List<BufferedImage> images = tempImageStorageService.getAllAndDeleteStorage(storageId);

                DiskShare diskShare = diskShareWrapper.getDiskShare();
                File datasetZip = diskShare.openFile(
                        userId.toString() + "/" + generationTaskUuid + ".zip",
                        EnumSet.of(AccessMask.FILE_WRITE_DATA),
                        null,
                        SMB2ShareAccess.ALL,
                        SMB2CreateDisposition.FILE_OPEN_IF,
                        null
                );

                ZipOutputStream zipOut = new ZipOutputStream(datasetZip.getOutputStream());

                for (int i = 0; i < images.size(); i++) {
                    ZipEntry zipEntry = new ZipEntry(i + ".png");
                    zipOut.putNextEntry(zipEntry);

                    ImageIO.write(images.get(i), "png", zipOut);
                }

                zipOut.flush();
                zipOut.close();
                datasetZip.flush();
                datasetZip.close();

                log.info("Saved dataset for task " + generationTaskUuid + ". Dataset size: " + generationParameters.getCount());

                QuarkusTransaction.run(() -> {
                    GenerationTaskEntity generationTask = generationTaskRepository.findById(generationTaskUuid);
                    generationTask.setStatus(GenerationStatus.COMPLETED);
                });

            } catch (Exception e) {
                QuarkusTransaction.run(() -> {
                    GenerationTaskEntity generationTask = generationTaskRepository.findById(generationTaskUuid);
                    generationTask.setStatus(GenerationStatus.ERROR);
                });
                log.error(e.getMessage(), e);
            }
        });
    }

    public BufferedImage createSingle1DImage(GenerationParameters generationParameters) {
        BufferedImage image = new BufferedImage(generationParameters.getWidth(), generationParameters.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setColor(Color.BLACK);

        int halfWidth = image.getWidth() / 2;
        int halfHeight = image.getHeight() / 2;

        List<Argument> argumentsX = generationParameters
                .getParametersX()
                .entrySet()
                .stream()
                .map(entry -> new Argument(
                        entry.getKey(),
                        mathUtils.getRandomDouble(
                                entry.getValue().getMin(),
                                entry.getValue().getMax()
                        )
                )).collect(Collectors.toList());

        argumentsX.add(new Argument(generationParameters.getXVarName(), generationParameters.getXMin()));
        Expression expressionX = new Expression(generationParameters.getEquationX(), argumentsX.toArray(new Argument[0]));

        int xLast = (int) (generationParameters.getXMin() * generationParameters.getDrawScale()) + halfHeight;
        int yLast = (int) (expressionX.calculate() * generationParameters.getDrawScale()) + halfWidth;

        int xCurrent, yCurrent;

        for (double x = generationParameters.getXMin() + generationParameters.getXStep(); x <= generationParameters.getXMax(); x += generationParameters.getXStep()) {
            expressionX.setArgumentValue(generationParameters.getXVarName(), x);

            xCurrent = (int) (x * generationParameters.getDrawScale()) + halfHeight;
            yCurrent = (int) (expressionX.calculate() * generationParameters.getDrawScale()) + halfWidth;
            g.drawLine(xLast, yLast, xCurrent, yCurrent);
            xLast = xCurrent;
            yLast = yCurrent;
        }

        g.dispose();

        return image;
    }

    public BufferedImage createSingle2DImage(GenerationParameters generationParameters) {
        BufferedImage image = new BufferedImage(generationParameters.getWidth(), generationParameters.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setColor(Color.BLACK);

        int halfWidth = image.getWidth() / 2;
        int halfHeight = image.getHeight() / 2;

        List<Argument> argumentsX = generationParameters
                .getParametersX()
                .entrySet()
                .stream()
                .map(entry -> new Argument(
                        entry.getKey(),
                        mathUtils.getRandomDouble(
                                entry.getValue().getMin(),
                                entry.getValue().getMax()
                        )
                )).collect(Collectors.toList());

        argumentsX.add(new Argument(generationParameters.getXVarName(), generationParameters.getXMin()));
        Expression expressionX = new Expression(generationParameters.getEquationX(), argumentsX.toArray(new Argument[0]));

        List<Argument> argumentsY = generationParameters
                .getParametersY()
                .entrySet()
                .stream()
                .map(entry -> new Argument(
                        entry.getKey(),
                        mathUtils.getRandomDouble(
                                entry.getValue().getMin(),
                                entry.getValue().getMax()
                        )
                )).collect(Collectors.toList());

        argumentsY.add(new Argument(generationParameters.getYVarName(), generationParameters.getYMin()));
        Expression expressionY = new Expression(generationParameters.getEquationY(), argumentsY.toArray(new Argument[0]));

        int xLast = (int) (expressionX.calculate() * generationParameters.getDrawScale()) + halfHeight;
        int yLast = (int) (expressionY.calculate() * generationParameters.getDrawScale()) + halfWidth;

        int xCurrent, yCurrent;

        for (double t = generationParameters.getXMin() + generationParameters.getXStep(); t <= generationParameters.getXMax(); t += generationParameters.getXStep()) {
            expressionX.setArgumentValue(generationParameters.getXVarName(), t);
            expressionY.setArgumentValue(generationParameters.getYVarName(), t);

            xCurrent = (int) (expressionY.calculate() * generationParameters.getDrawScale()) + halfHeight;
            yCurrent = (int) (expressionX.calculate() * generationParameters.getDrawScale()) + halfWidth;
            g.drawLine(xLast, yLast, xCurrent, yCurrent);
            xLast = xCurrent;
            yLast = yCurrent;
        }

        g.dispose();

        return image;
    }
}
