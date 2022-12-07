package ru.ifmo.se.service.impl;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import ru.ifmo.se.service.api.DatasetGeneratorService;
import ru.ifmo.se.service.model.GenerationParameters;
import ru.ifmo.se.utils.MathUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class DatasetGeneratorServiceImpl implements DatasetGeneratorService {

    @Inject
    MathUtils mathUtils;

    @Override
    public void precessTask(UUID generationTaskUuid) {
    }

    public BufferedImage createSingle1DImage(GenerationParameters generationParameters) {
        BufferedImage image = new BufferedImage(generationParameters.getWidth(), generationParameters.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setColor(Color.BLACK);

        int halfWidth = image.getWidth() / 2;
        int halfHeight = image.getHeight() / 2;

        List<Argument> argumentsX = generationParameters
                .getParametersX()
                .entrySet()
                .stream()
                .map(entry -> new Argument(
                        entry.getKey(),
                        mathUtils.getRandomDouble(
                                entry.getValue().getMin(),
                                entry.getValue().getMax()
                        )
                )).collect(Collectors.toList());

        argumentsX.add(new Argument(generationParameters.getXVarName(), generationParameters.getXMin()));
        Expression expressionX = new Expression(generationParameters.getEquationX(), argumentsX.toArray(new Argument[0]));

        int xLast = (int) (generationParameters.getXMin() * generationParameters.getDrawScale()) + halfHeight;
        int yLast = (int) (expressionX.calculate() * generationParameters.getDrawScale()) + halfWidth;

        int xCurrent, yCurrent;

        for (double x = generationParameters.getXMin() + generationParameters.getXStep(); x <= generationParameters.getXMax(); x += generationParameters.getXStep()) {
            expressionX.setArgumentValue(generationParameters.getXVarName(), x);

            xCurrent = (int) (x * generationParameters.getDrawScale()) + halfHeight;
            yCurrent = (int) (expressionX.calculate() * generationParameters.getDrawScale()) + halfWidth;
            g.drawLine(xLast, yLast, xCurrent, yCurrent);
            xLast = xCurrent;
            yLast = yCurrent;
        }

        g.dispose();

        return image;
    }

    public BufferedImage createSingle2DImage(GenerationParameters generationParameters) {
        BufferedImage image = new BufferedImage(generationParameters.getWidth(), generationParameters.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setColor(Color.BLACK);

        int halfWidth = image.getWidth() / 2;
        int halfHeight = image.getHeight() / 2;

        List<Argument> argumentsX = generationParameters
                .getParametersX()
                .entrySet()
                .stream()
                .map(entry -> new Argument(
                        entry.getKey(),
                        mathUtils.getRandomDouble(
                                entry.getValue().getMin(),
                                entry.getValue().getMax()
                        )
                )).collect(Collectors.toList());

        argumentsX.add(new Argument(generationParameters.getXVarName(), generationParameters.getXMin()));
        Expression expressionX = new Expression(generationParameters.getEquationX(), argumentsX.toArray(new Argument[0]));

        List<Argument> argumentsY = generationParameters
                .getParametersY()
                .entrySet()
                .stream()
                .map(entry -> new Argument(
                        entry.getKey(),
                        mathUtils.getRandomDouble(
                                entry.getValue().getMin(),
                                entry.getValue().getMax()
                        )
                )).collect(Collectors.toList());

        argumentsY.add(new Argument(generationParameters.getYVarName(), generationParameters.getYMin()));
        Expression expressionY = new Expression(generationParameters.getEquationY(), argumentsY.toArray(new Argument[0]));

        int xLast = (int) (expressionX.calculate() * generationParameters.getDrawScale()) + halfHeight;
        int yLast = (int) (expressionY.calculate() * generationParameters.getDrawScale()) + halfWidth;

        int xCurrent, yCurrent;

        for (double t = generationParameters.getXMin() + generationParameters.getXStep(); t <= generationParameters.getXMax(); t += generationParameters.getXStep()) {
            expressionX.setArgumentValue(generationParameters.getXVarName(), t);
            expressionY.setArgumentValue(generationParameters.getYVarName(), t);

            xCurrent = (int) (expressionY.calculate() * generationParameters.getDrawScale()) + halfHeight;
            yCurrent = (int) (expressionX.calculate() * generationParameters.getDrawScale()) + halfWidth;
            g.drawLine(xLast, yLast, xCurrent, yCurrent);
            xLast = xCurrent;
            yLast = yCurrent;
        }

        g.dispose();

        return image;
    }
}
