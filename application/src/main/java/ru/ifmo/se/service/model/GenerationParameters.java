package ru.ifmo.se.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationParameters {
    private String equation;

    private int height;
    private int width;

    private double xMin;
    private double xMax;
    private double xStep;

    private double drawScale;

    private Map<String, ParameterSettings> parameters;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParameterSettings {
        private double min;
        private double max;
    }
}
