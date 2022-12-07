package ru.ifmo.se.web.model;

import lombok.Data;

@Data
public class GenerationTaskParameters1DRequestDto {
    private EquationSettingsDto equationSettings;
    private int datasetSize;
    private Double drawScale;
    private Integer height;
    private Integer width;
}
