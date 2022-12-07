package ru.ifmo.se.mapper;

import org.mapstruct.Mapper;
import ru.ifmo.se.service.model.GenerationParameters;
import ru.ifmo.se.web.model.GenerationParametersRequestDto;

@Mapper
public interface GenerationParametersMapper {
    GenerationParameters from(GenerationParametersRequestDto requestDto);
}
