/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.tween;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.GameLog;
import necesse.engine.GameRandomNoise;
import necesse.engine.util.GameRandom;
import necesse.engine.util.tween.Tween;

public class ShakeTween
extends Tween<ShakeTween> {
    private static final GameRandom random = new GameRandom();
    private static final GameRandomNoise noise = new GameRandomNoise(random.nextInt());
    private final double[] values;
    private final double[] randomOffsets;
    private final ArrayList<Supplier<Double>> getters;
    private final ArrayList<Consumer<Double>> setters;
    private double frequency;
    private double amplitude;
    private double randomness;
    private boolean fadeOut = true;
    private boolean fadeIn = false;

    public ShakeTween(double duration, double frequency, double amplitude, double randomness, Supplier<Double> getter, Consumer<Double> setter) {
        super(duration);
        this.setFrequency(frequency);
        this.setAmplitude(amplitude);
        this.setRandomness(randomness);
        this.getters = new ArrayList(1);
        this.getters.add(getter);
        this.setters = new ArrayList(1);
        this.setters.add(setter);
        this.values = new double[this.getters.size()];
        this.randomOffsets = new double[this.getters.size()];
        this.initialize();
    }

    public ShakeTween(double duration, double frequency, double amplitude, double randomness, Point2D.Double value) {
        super(duration);
        this.setFrequency(frequency);
        this.setAmplitude(amplitude);
        this.setRandomness(randomness);
        this.getters = new ArrayList(2);
        this.setters = new ArrayList(2);
        this.getters.add(value::getX);
        this.setters.add(x -> {
            value.x = x;
        });
        this.getters.add(value::getY);
        this.setters.add(y -> {
            value.y = y;
        });
        this.values = new double[this.getters.size()];
        this.randomOffsets = new double[this.getters.size()];
        this.initialize();
    }

    private void initialize() {
        if (this.getters.size() != this.setters.size()) {
            throw new IllegalArgumentException("Getters and setters must have the same size");
        }
        for (int i = 0; i < this.randomOffsets.length; ++i) {
            this.randomOffsets[i] = random.nextDouble() * 100000.0;
        }
    }

    @Override
    protected void preparePlay() {
        for (int i = 0; i < this.values.length; ++i) {
            this.values[i] = this.getters.get(i).get();
        }
    }

    @Override
    protected void prepareBackwardsPlay() {
        this.preparePlay();
    }

    @Override
    protected void tween(double percent) {
        double tweenedTime = percent * this.duration;
        double fadedAmplitude = this.amplitude;
        if (this.fadeIn && percent < 0.5) {
            fadedAmplitude *= percent * 2.0;
        } else if (this.fadeOut && percent > 0.5) {
            fadedAmplitude = fadedAmplitude * 1.0 - (percent - 0.5) * 2.0;
        }
        for (int i = 0; i < this.values.length; ++i) {
            double x = (tweenedTime + this.randomOffsets[i]) * this.frequency;
            double shake = Math.sin(x) * fadedAmplitude;
            double perlinShake = noise.perlin1New(x) * fadedAmplitude;
            shake = (1.0 - this.randomness) * shake + this.randomness * perlinShake;
            this.shakeValue(i, shake);
        }
    }

    @Override
    protected void progressToCompletion() {
        for (int i = 0; i < this.values.length; ++i) {
            this.setters.get(i).accept(this.values[i]);
        }
    }

    @Override
    protected void progressToBeginning() {
        this.progressToCompletion();
    }

    private void shakeValue(int index, double shake) {
        double newValue = shake + this.values[index];
        this.setters.get(index).accept(newValue);
    }

    public double getFrequency() {
        return this.frequency;
    }

    public ShakeTween setFrequency(double frequency) {
        if (this.isRunning()) {
            GameLog.warn.println("Cannot change frequency while tween is running");
            return this;
        }
        this.frequency = Math.abs(frequency);
        return this;
    }

    public double getAmplitude() {
        return this.amplitude;
    }

    public ShakeTween setAmplitude(double amplitude) {
        if (this.isRunning()) {
            GameLog.warn.println("Cannot change amplitude while tween is running");
            return this;
        }
        this.amplitude = amplitude;
        return this;
    }

    public double getRandomness() {
        return this.randomness;
    }

    public ShakeTween setRandomness(double randomness) {
        if (this.isRunning()) {
            GameLog.warn.println("Cannot change randomness while tween is running");
            return this;
        }
        if (randomness < 0.0 || randomness > 1.0) {
            GameLog.warn.println("Randomness must be between 0 and 1");
            return this;
        }
        this.randomness = randomness;
        return this;
    }

    public boolean isFadeOut() {
        return this.fadeOut;
    }

    public ShakeTween setFadeOut(boolean fadeOut) {
        if (this.isRunning()) {
            GameLog.warn.println("Cannot change fade out while tween is running");
            return this;
        }
        this.fadeOut = fadeOut;
        return this;
    }

    public boolean isFadeIn() {
        return this.fadeIn;
    }

    public ShakeTween setFadeIn(boolean fadeIn) {
        if (this.isRunning()) {
            GameLog.warn.println("Cannot change fade in while tween is running");
            return this;
        }
        this.fadeIn = fadeIn;
        return this;
    }
}

