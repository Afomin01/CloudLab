package ru.ifmo.se.utils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.security.SecureRandom;
import java.util.Random;

@ApplicationScoped
public class MathUtils {

    private Random random;

    @PostConstruct
    public void init() {
        random  = new SecureRandom();
    }

    public double getRandomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
