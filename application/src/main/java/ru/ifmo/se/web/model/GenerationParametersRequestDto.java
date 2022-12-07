package ru.ifmo.se.web.model;

import lombok.Data;

import java.util.Map;

@Data
public class GenerationParametersRequestDto {
    private String equation;

    private Integer height;
    private Integer width;

    private Double xMin;
    private Double xMax;
    private Double xStep;

    private Double drawScale;

    private Map<String, ParameterSettings> parameters;

    @Data
    public static class ParameterSettings {
        private Double min;
        private Double max;
    }
}
