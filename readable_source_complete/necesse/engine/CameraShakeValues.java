/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.awt.geom.Point2D;
import necesse.engine.util.GameRandom;

public class CameraShakeValues {
    private final GameRandom random = new GameRandom();
    public final int duration;
    public final int frequency;
    private final int samples;
    public final float xIntensity;
    public final float yIntensity;
    private final float[] xValues;
    private final float[] yValues;

    public CameraShakeValues(int duration, int frequency, float xIntensity, float yIntensity, boolean addFallOff, Point2D.Float prevShake) {
        this.duration = duration;
        this.frequency = frequency;
        this.xIntensity = xIntensity;
        this.yIntensity = yIntensity;
        this.samples = duration / frequency + 2;
        this.xValues = this.generateValues(xIntensity, prevShake.x, addFallOff);
        this.yValues = this.generateValues(yIntensity, prevShake.y, addFallOff);
    }

    public CameraShakeValues(int duration, int frequency, float xIntensity, float yIntensity, boolean addFallOff) {
        this(duration, frequency, xIntensity, yIntensity, addFallOff, new Point2D.Float());
    }

    private float[] generateValues(float intensity, float startPosition, boolean addFallOff) {
        float[] values = new float[this.samples];
        for (int i = 1; i < values.length - 1; ++i) {
            float reverseProgress = 1.0f;
            if (addFallOff) {
                reverseProgress = Math.abs((float)i / (float)values.length - 1.0f);
            }
            values[i] = (float)this.random.nextGaussian() * intensity * reverseProgress;
        }
        values[0] = startPosition;
        values[this.samples - 1] = 0.0f;
        return values;
    }

    public boolean isShaking(long timeProgress) {
        return timeProgress < (long)this.duration;
    }

    public Point2D.Float getCurrentShake(long timeProgress) {
        if (timeProgress < 0L || timeProgress >= (long)this.duration) {
            return new Point2D.Float();
        }
        float progress = (float)timeProgress / (float)this.duration;
        int valueIndex = (int)(progress * (float)(this.samples - 1));
        Point2D.Float currentShake = new Point2D.Float(this.xValues[valueIndex], this.yValues[valueIndex]);
        Point2D.Float nextShake = new Point2D.Float(this.xValues[valueIndex + 1], this.yValues[valueIndex + 1]);
        float timePerValue = (float)this.duration / (float)(this.samples - 1);
        float pointProgress = ((float)timeProgress - (float)valueIndex * timePerValue) / timePerValue;
        float cosProgress = (float)((Math.cos(Math.PI * (double)(1.0f + pointProgress)) + 1.0) / 2.0);
        return new Point2D.Float(currentShake.x + (nextShake.x - currentShake.x) * cosProgress, currentShake.y + (nextShake.y - currentShake.y) * cosProgress);
    }

    public Point2D.Float getCurrentShake(long startTime, long currentTime) {
        return this.getCurrentShake(currentTime - startTime);
    }
}

