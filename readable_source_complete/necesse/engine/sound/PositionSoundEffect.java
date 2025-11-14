/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import java.awt.geom.Point2D;
import java.util.function.Supplier;
import necesse.engine.sound.GlobalSoundEffect;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.sound.SoundPlayer;

public class PositionSoundEffect
extends GlobalSoundEffect {
    protected int minDistance = 75;
    protected int listenDistance = 750;
    protected float falloffExponent = 0.01f;
    protected PrimitiveSoundEmitter emitter;

    PositionSoundEffect(Supplier<Float> settingVolumeMod, PrimitiveSoundEmitter emitter) {
        super(settingVolumeMod);
        this.emitter = emitter;
    }

    @Override
    public PositionSoundEffect pitch(float pitch) {
        super.pitch(pitch);
        return this;
    }

    @Override
    public PositionSoundEffect volume(float volume) {
        super.volume(volume);
        return this;
    }

    public PositionSoundEffect minFalloffDistance(int distance) {
        this.minDistance = distance;
        return this;
    }

    public PositionSoundEffect falloffDistance(int distance) {
        this.listenDistance = distance;
        return this;
    }

    public PositionSoundEffect falloffExponent(float falloffExponent) {
        this.falloffExponent = falloffExponent;
        return this;
    }

    public PositionSoundEffect emitter(PrimitiveSoundEmitter emitter) {
        this.emitter = emitter;
        return this;
    }

    public static float getGain(float falloffExponent, float distance, int minDistance, int listenDistance) {
        if (distance < 0.0f) {
            return 0.0f;
        }
        return (float)Math.pow(falloffExponent, Math.max(0.0f, (distance - (float)minDistance) / (float)listenDistance));
    }

    @Override
    public void updateSound(SoundPlayer player, int source, float sourceVolumeMod, SoundEmitter listener) {
        PrimitiveSoundEmitter emitter = this.getEmitter();
        float volumeMod = ((Float)this.settingVolumeMod.get()).floatValue() * this.volume * sourceVolumeMod;
        player.alSetPitch(this.pitch);
        float distance = emitter.getSoundDistance(listener.getSoundPositionX(), listener.getSoundPositionY());
        float gain = PositionSoundEffect.getGain(this.falloffExponent, distance, this.minDistance, this.listenDistance);
        player.alSetGain(volumeMod * gain);
        Point2D.Float dir = emitter.getSoundDirection(listener.getSoundPositionX(), listener.getSoundPositionY());
        if (dir != null && distance > (float)this.minDistance) {
            float dirMod = 1.0f;
            if (distance - (float)this.minDistance < 150.0f) {
                dirMod = (distance - (float)this.minDistance) / 150.0f;
            }
            player.alSetPosition(PositionSoundEffect.getSoundPosition(new float[]{dir.x * dirMod, dir.y * dirMod, 1.0f}));
        } else {
            player.alSetPosition(PositionSoundEffect.getSoundPosition(new float[]{0.0f, 0.0f, 1.0f}));
        }
    }

    @Override
    public PrimitiveSoundEmitter getEmitter() {
        return this.emitter;
    }
}

