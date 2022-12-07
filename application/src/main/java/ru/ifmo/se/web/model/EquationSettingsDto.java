package ru.ifmo.se.web.model;

import lombok.Data;

import java.util.Map;

@Data
public class EquationSettingsDto {
    private String equation;

    private String varName;
    private Double min;
    private Double max;
    private Double step;

    private Map<String, EquationParameterDto> parameters;
}

