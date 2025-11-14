/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.awt.geom.Point2D;
import necesse.engine.CameraShakeValues;
import necesse.engine.sound.PositionSoundEffect;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEmitter;

public class CameraShake {
    public final long startTime;
    public final CameraShakeValues values;
    public PrimitiveSoundEmitter emitter;
    public int minDistance = 50;
    public int listenDistance = 1500;
    public float falloffExponent = 0.01f;
    private boolean removed;

    public CameraShake(long startTime, int duration, int frequency, float xIntensity, float yIntensity, boolean addFallOff, Point2D.Float prevShake) {
        this.startTime = startTime;
        this.values = new CameraShakeValues(duration, frequency, xIntensity, yIntensity, addFallOff, prevShake);
    }

    public CameraShake(long startTime, int duration, int frequency, float xIntensity, float yIntensity, boolean addFallOff) {
        this(startTime, duration, frequency, xIntensity, yIntensity, addFallOff, new Point2D.Float());
    }

    public CameraShake from(PrimitiveSoundEmitter emitter) {
        this.emitter = emitter;
        return this;
    }

    public CameraShake from(PrimitiveSoundEmitter emitter, int minDistance, int listenDistance) {
        this.emitter = emitter;
        this.minDistance = minDistance;
        this.listenDistance = listenDistance;
        return this;
    }

    public CameraShake from(PrimitiveSoundEmitter emitter, int minDistance, int listenDistance, float falloffExponent) {
        this.emitter = emitter;
        this.minDistance = minDistance;
        this.listenDistance = listenDistance;
        this.falloffExponent = falloffExponent;
        return this;
    }

    public Point2D.Float getCurrentShake(long currentTime) {
        return this.values.getCurrentShake(this.startTime, currentTime);
    }

    public float getDistanceIntensity(SoundEmitter listenerPos) {
        float distance = 0.0f;
        if (this.emitter != null && listenerPos != null) {
            distance = this.emitter.getSoundDistance(listenerPos.getSoundPositionX(), listenerPos.getSoundPositionY());
        }
        return PositionSoundEffect.getGain(this.falloffExponent, distance, this.minDistance, this.listenDistance);
    }

    public Point2D.Float getCurrentShake(long currentTime, SoundEmitter listenerPos) {
        Point2D.Float currentShake = this.getCurrentShake(currentTime);
        float distanceIntensity = this.getDistanceIntensity(listenerPos);
        return new Point2D.Float(currentShake.x * distanceIntensity, currentShake.y * distanceIntensity);
    }

    public void remove() {
        this.removed = true;
    }

    public boolean isOver(long currentTime) {
        return this.removed || !this.values.isShaking(currentTime - this.startTime);
    }
}

