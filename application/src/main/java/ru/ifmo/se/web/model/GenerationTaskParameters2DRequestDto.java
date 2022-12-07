package ru.ifmo.se.web.model;

import lombok.Data;

@Data
public class GenerationTaskParameters2DRequestDto {
    private EquationSettingsDto firstEquationSettings;
    private EquationSettingsDto secondEquationSettings;
    private int datasetSize;
    private Double drawScale;
    private Integer height;
    private Integer width;
}
