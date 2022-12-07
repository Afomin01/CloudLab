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
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class DatasetGeneratorServiceImpl implements DatasetGeneratorService {

    @Inject
    MathUtils mathUtils;

    @Override
    public void precessTask(UUID generationTaskUuid) {
    }

    @Override
    public BufferedImage createSingleImage(GenerationParameters generationParameters) {
        BufferedImage image = new BufferedImage(generationParameters.getWidth(), generationParameters.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setColor(Color.BLACK);

        int halfWidth = image.getWidth() / 2;
        int halfHeight = image.getHeight() / 2;

        List<Argument> arguments = generationParameters
                .getParameters()
                .entrySet()
                .stream()
                .map(entry -> new Argument(
                        entry.getKey(),
                        mathUtils.getRandomDouble(
                                entry.getValue().getMin(),
                                entry.getValue().getMax()
                        )
                )).collect(Collectors.toList());

        arguments.add(new Argument("x", 0));

        Expression expression = new Expression(generationParameters.getEquation(), arguments.toArray(new Argument[0]));
        expression.setArgumentValue("x", generationParameters.getXMin());

        int xLast = (int) (generationParameters.getXMin() * generationParameters.getDrawScale()) + halfHeight;
        int yLast = (int) (expression.calculate() * generationParameters.getDrawScale()) + halfWidth;

        int xCurrent, yCurrent;

        for (double x = generationParameters.getXMin() + generationParameters.getXStep(); x <= generationParameters.getXMax(); x += generationParameters.getXStep()) {
            expression.setArgumentValue("x", x);
            xCurrent = (int) (x * generationParameters.getDrawScale()) + halfHeight;
            yCurrent = (int) (expression.calculate() * generationParameters.getDrawScale()) + halfWidth;
            g.drawLine(xLast, yLast, xCurrent, yCurrent);
            xLast = xCurrent;
            yLast = yCurrent;
        }

        g.dispose();

        File file = new File("E://test.png");

        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return image;
    }


}
