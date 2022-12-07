package ru.ifmo.se.service.impl;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import ru.ifmo.se.exception.InputValidationException;
import ru.ifmo.se.mapper.GenerationParametersMapper;
import ru.ifmo.se.service.api.DatasetGeneratorService;
import ru.ifmo.se.service.api.GenerationService;
import ru.ifmo.se.service.model.GenerationParameters;
import ru.ifmo.se.web.model.GenerationParametersRequestDto;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class GenerationServiceImpl implements GenerationService {

    @Inject
    GenerationParametersMapper generationParametersMapper;

    @Inject
    DatasetGeneratorService datasetGeneratorService;

    @Override
    public BufferedImage createGenerationTask(GenerationParametersRequestDto requestDto) {
        if (StringUtils.isEmpty(requestDto.getEquation())) {
            throw new InputValidationException("No expression provided.");
        }

        if (requestDto.getXMin() == null || requestDto.getXMax() == null || requestDto.getXStep() == null) {
            throw new InputValidationException("Not enough information for X provided.");
        }

        if (MapUtils.isEmpty(requestDto.getParameters())) {
            throw new InputValidationException("No parameters setting provided.");
        }

        List<Argument> arguments = requestDto
                .getParameters()
                .keySet()
                .stream()
                .map(argName -> new Argument(argName, 0))
                .collect(Collectors.toList());

        arguments.add(new Argument("x", 0));

        Expression expression = new Expression(requestDto.getEquation(), arguments.toArray(new Argument[0]));

        if (!expression.checkSyntax()) {
            throw new InputValidationException("Error while validating input expression and parameters. \n" + expression.getErrorMessage());
        }

        GenerationParameters generationParameters = GenerationParameters
                .builder()
                .equation(requestDto.getEquation())
                .parameters(requestDto
                        .getParameters()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> new GenerationParameters.ParameterSettings(entry.getValue().getMin(), entry.getValue().getMax())
                                )
                        )
                )
                .xMin(requestDto.getXMin())
                .xMax(requestDto.getXMax())
                .xStep(requestDto.getXStep())
                .height(requestDto.getHeight())
                .width(requestDto.getWidth())
                .drawScale(requestDto.getDrawScale())
                .build();

        return datasetGeneratorService.createSingleImage(generationParameters);
    }
}
