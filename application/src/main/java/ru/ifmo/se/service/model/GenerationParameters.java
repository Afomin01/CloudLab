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
    private String equationX;
    private String equationY;

    private String xVarName;
    private String yVarName;

    private int count;

    private int height;
    private int width;

    private double xMin;
    private double xMax;
    private double xStep;

    private double yMin;
    private double yMax;
    private double yStep;

    private double drawScale;

    private Map<String, ParameterSettings> parametersX;
    private Map<String, ParameterSettings> parametersY;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParameterSettings {
        private double min;
        private double max;
    }
}
