package ru.ifmo.se.mapper;

import org.mapstruct.Mapper;
import ru.ifmo.se.service.model.GenerationParameters;
import ru.ifmo.se.web.model.EquationParameterDto;
import ru.ifmo.se.web.model.EquationSettingsDto;
import ru.ifmo.se.web.model.GenerationTaskParameters1DRequestDto;
import ru.ifmo.se.web.model.GenerationTaskParameters2DRequestDto;

import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public abstract class GenerationParametersMapper {
    public GenerationParameters from(GenerationTaskParameters1DRequestDto requestDto) {
        if (requestDto == null || requestDto.getEquationSettings() == null) {
            return null;
        }

        EquationSettingsDto equationSettingsDto = requestDto.getEquationSettings();

        return GenerationParameters
                .builder()
                .equationX(equationSettingsDto.getEquation())
                .parametersX(from(equationSettingsDto.getParameters()))
                .xMin(equationSettingsDto.getMin())
                .xMax(equationSettingsDto.getMax())
                .xStep(equationSettingsDto.getStep())
                .xVarName(equationSettingsDto.getVarName())
                .height(requestDto.getHeight())
                .width(requestDto.getWidth())
                .drawScale(requestDto.getDrawScale())
                .count(requestDto.getDatasetSize())
                .build();
    }

    public GenerationParameters from(GenerationTaskParameters2DRequestDto requestDto) {
        if (requestDto == null || requestDto.getFirstEquationSettings() == null || requestDto.getSecondEquationSettings() == null) {
            return null;
        }

        EquationSettingsDto firstSettings = requestDto.getFirstEquationSettings();
        EquationSettingsDto secondSettings = requestDto.getSecondEquationSettings();

        return GenerationParameters
                .builder()
                .equationX(firstSettings.getEquation())
                .parametersX(from(firstSettings.getParameters()))
                .xMin(firstSettings.getMin())
                .xMax(firstSettings.getMax())
                .xStep(firstSettings.getStep())
                .xVarName(firstSettings.getVarName())
                .equationY(secondSettings.getEquation())
                .parametersY(from(secondSettings.getParameters()))
                .yMin(firstSettings.getMin())
                .yMax(firstSettings.getMax())
                .yStep(firstSettings.getStep())
                .yVarName(secondSettings.getVarName())
                .height(requestDto.getHeight())
                .width(requestDto.getWidth())
                .drawScale(requestDto.getDrawScale())
                .count(requestDto.getDatasetSize())
                .build();
    }

    private Map<String, GenerationParameters.ParameterSettings> from(Map<String, EquationParameterDto> dto) {
        return dto
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> new GenerationParameters.ParameterSettings(entry.getValue().getMin(), entry.getValue().getMax())
                        )
                );
    }
}
