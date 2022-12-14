package ru.ifmo.se.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hierynomus.smbj.share.DiskShare;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import ru.ifmo.se.configuration.UserRolesConstants;
import ru.ifmo.se.configuration.model.DiskShareWrapper;
import ru.ifmo.se.database.model.GenerationStatus;
import ru.ifmo.se.database.model.GenerationTaskEntity;
import ru.ifmo.se.database.model.UserEntity;
import ru.ifmo.se.database.repository.GenerationTaskRepository;
import ru.ifmo.se.database.repository.UserRepository;
import ru.ifmo.se.exception.InputValidationException;
import ru.ifmo.se.mapper.GenerationParametersMapper;
import ru.ifmo.se.service.api.DatasetGeneratorService;
import ru.ifmo.se.service.api.FilesService;
import ru.ifmo.se.service.api.GenerationService;
import ru.ifmo.se.service.model.GenerationParameters;
import ru.ifmo.se.utils.FileUtils;
import ru.ifmo.se.web.model.GenerationTaskParameters1DRequestDto;
import ru.ifmo.se.web.model.GenerationTaskParameters2DRequestDto;
import ru.ifmo.se.web.model.GenerationTaskResponseDto;
import ru.ifmo.se.web.model.admin.DeleteTaskResultRequestDto;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class GenerationServiceImpl implements GenerationService {

    @Inject
    GenerationParametersMapper generationParametersMapper;

    @Inject
    DatasetGeneratorService datasetGeneratorService;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    GenerationTaskRepository generationTaskRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    FilesService filesService;

    @Override
    @Transactional
    public UUID createGenerationTask1D(GenerationTaskParameters1DRequestDto requestDto, String username) {
        validateParametersInput(requestDto);

        GenerationParameters generationParameters = generationParametersMapper.from(requestDto);

        UserEntity userEntity = userRepository.findByName(username);
        if (userEntity.getQuota() - requestDto.getDatasetSize() < 0) {
            throw new InputValidationException("Generation quota reached. Contact administrator to increase it or lower requested dataset size. You current quota is: " + userEntity.getQuota());
        }

        try {
            GenerationTaskEntity generationTaskEntity = new GenerationTaskEntity();
            generationTaskEntity.setCreationTime(Instant.now());
            generationTaskEntity.setParameters(objectMapper.writeValueAsString(generationParameters));
            generationTaskEntity.setStatus(GenerationStatus.CREATED);
            generationTaskEntity.setUser(userEntity);

            userEntity.setQuota(userEntity.getQuota() - requestDto.getDatasetSize());
            userRepository.persist(userEntity);
            generationTaskRepository.persist(generationTaskEntity);

            return generationTaskEntity.getId();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Unexpected error.");
        }
    }

    @Override
    public BufferedImage createTestImage1D(GenerationTaskParameters1DRequestDto requestDto) {
        validateParametersInput(requestDto);

        return datasetGeneratorService.createSingle1DImage(generationParametersMapper.from(requestDto));
    }

    @Override
    @Transactional
    public UUID createGenerationTask2D(GenerationTaskParameters2DRequestDto requestDto, String username) {
        validateParametersInput(requestDto);

        GenerationParameters generationParameters = generationParametersMapper.from(requestDto);

        UserEntity userEntity = userRepository.findByName(username);
        if (userEntity.isBlocked()) {
            throw new InputValidationException("Your account was blocked and you can not submit new generation requests. Contact the administrator to unblock your account. ");
        }
        if (userEntity.getQuota() - requestDto.getDatasetSize() < 0) {
            throw new InputValidationException("Generation quota reached. Contact administrator to increase it or lower requested dataset size. You current quota is: " + userEntity.getQuota());
        }

        try {
            GenerationTaskEntity generationTaskEntity = new GenerationTaskEntity();
            generationTaskEntity.setId(UUID.randomUUID());
            generationTaskEntity.setCreationTime(Instant.now());
            generationTaskEntity.setParameters(objectMapper.writeValueAsString(generationParameters));
            generationTaskEntity.setStatus(GenerationStatus.CREATED);
            generationTaskEntity.setUser(userEntity);

            userEntity.setQuota(userEntity.getQuota() - requestDto.getDatasetSize());
            userRepository.persist(userEntity);
            generationTaskRepository.persist(generationTaskEntity);

            return generationTaskEntity.getId();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Unexpected error.");
        }
    }

    @Override
    public BufferedImage createTestImage2D(GenerationTaskParameters2DRequestDto requestDto) {
        validateParametersInput(requestDto);

        return datasetGeneratorService.createSingle2DImage(generationParametersMapper.from(requestDto));
    }

    @Override
    public List<GenerationTaskResponseDto> getUserGenerationTasks(String username) {
        UserEntity user = userRepository.findByName(username);

        return generationTaskRepository.findByUser(user)
                .stream()
                .map(task ->
                        GenerationTaskResponseDto
                                .builder()
                                .id(task.getId())
                                .creationTime(task.getCreationTime())
                                .status(task.getStatus())
                                .build()
                ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteTaskResult(DeleteTaskResultRequestDto requestDto, String username) {
        UserEntity user = userRepository.findByName(username);
        GenerationTaskEntity generationTask = generationTaskRepository.findById(requestDto.getGenerationTaskId());

        if (generationTask == null) {
            throw new InputValidationException("Task not found.");
        }

        if (!Objects.equals(generationTask.getUser().getId(), user.getId())) {
            if (!UserRolesConstants.ADMIN.equals(user.getRole())) {
                throw new InputValidationException("Task not found.");
            }
        }

        filesService.deleteDataset(user, generationTask);
        generationTask.setStatus(GenerationStatus.RESULT_DELETED);
    }

    private void validateParametersInput(GenerationTaskParameters1DRequestDto requestDto) throws InputValidationException {
        if (requestDto.getEquationSettings() == null || StringUtils.isEmpty(requestDto.getEquationSettings().getEquation())) {
            throw new InputValidationException("No expression provided.");
        }

        if (requestDto.getEquationSettings().getMin() == null || requestDto.getEquationSettings().getMax() == null || requestDto.getEquationSettings().getStep() == null) {
            throw new InputValidationException("Not enough information for X provided.");
        }

        if (MapUtils.isEmpty(requestDto.getEquationSettings().getParameters())) {
            throw new InputValidationException("No parameters setting provided.");
        }

        List<Argument> arguments = requestDto
                .getEquationSettings()
                .getParameters()
                .keySet()
                .stream()
                .map(argName -> new Argument(argName, 0))
                .collect(Collectors.toList());

        arguments.add(new Argument(requestDto.getEquationSettings().getVarName(), 0));

        Expression expression = new Expression(requestDto.getEquationSettings().getEquation(), arguments.toArray(new Argument[0]));

        if (!expression.checkSyntax()) {
            throw new InputValidationException("Error while validating input expression and parameters. \n" + expression.getErrorMessage());
        }
    }

    private void validateParametersInput(GenerationTaskParameters2DRequestDto requestDto) throws InputValidationException {
        if (requestDto.getFirstEquationSettings() == null
                || requestDto.getSecondEquationSettings() == null
                || StringUtils.isEmpty(requestDto.getFirstEquationSettings().getEquation())
                || StringUtils.isEmpty(requestDto.getSecondEquationSettings().getEquation())
        ) {
            throw new InputValidationException("No expression provided.");
        }

        if (requestDto.getFirstEquationSettings().getMin() == null
                || requestDto.getFirstEquationSettings().getMax() == null
                || requestDto.getFirstEquationSettings().getStep() == null) {
            throw new InputValidationException("Not enough information for " + requestDto.getFirstEquationSettings().getVarName() + " provided.");
        }

        if (requestDto.getSecondEquationSettings().getMin() == null
                || requestDto.getSecondEquationSettings().getMax() == null
                || requestDto.getSecondEquationSettings().getStep() == null) {
            throw new InputValidationException("Not enough information for " + requestDto.getSecondEquationSettings().getVarName() + " provided.");
        }

        if (MapUtils.isEmpty(requestDto.getFirstEquationSettings().getParameters())) {
            throw new InputValidationException("No parameters setting provided.");
        }

        if (MapUtils.isEmpty(requestDto.getSecondEquationSettings().getParameters())) {
            throw new InputValidationException("No parameters setting provided.");
        }

        List<Argument> argumentsFirst = requestDto
                .getFirstEquationSettings()
                .getParameters()
                .keySet()
                .stream()
                .map(argName -> new Argument(argName, 0))
                .collect(Collectors.toList());

        argumentsFirst.add(new Argument(requestDto.getFirstEquationSettings().getVarName(), 0));

        Expression expressionFirst = new Expression(requestDto.getFirstEquationSettings().getEquation(), argumentsFirst.toArray(new Argument[0]));

        if (!expressionFirst.checkSyntax()) {
            throw new InputValidationException("Error while validating input expression and parameters. \n" + expressionFirst.getErrorMessage());
        }

        List<Argument> argumentsSecond = requestDto
                .getSecondEquationSettings()
                .getParameters()
                .keySet()
                .stream()
                .map(argName -> new Argument(argName, 0))
                .collect(Collectors.toList());

        argumentsSecond.add(new Argument(requestDto.getSecondEquationSettings().getVarName(), 0));

        Expression expressionSecond = new Expression(requestDto.getSecondEquationSettings().getEquation(), argumentsSecond.toArray(new Argument[0]));

        if (!expressionSecond.checkSyntax()) {
            throw new InputValidationException("Error while validating input expression and parameters. \n" + expressionSecond.getErrorMessage());
        }
    }
}
