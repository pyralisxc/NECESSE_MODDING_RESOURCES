/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 */
package necesse.engine.sound;

import java.nio.FloatBuffer;
import necesse.engine.Settings;
import necesse.engine.sound.GlobalSoundEffect;
import necesse.engine.sound.PositionSoundEffect;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.sound.SoundPlayer;
import org.lwjgl.BufferUtils;

public abstract class SoundEffect {
    protected float volume = 1.0f;

    public abstract void updateSound(SoundPlayer var1, int var2, float var3, SoundEmitter var4);

    public SoundEffect volume(float volume) {
        this.volume = volume;
        return this;
    }

    public float getVolume() {
        return this.volume;
    }

    public abstract PrimitiveSoundEmitter getEmitter();

    protected static FloatBuffer getSoundPosition(float[] soundArray) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer((int)3);
        buffer.put(soundArray);
        buffer.rewind();
        return buffer;
    }

    public static GlobalSoundEffect music() {
        return new GlobalSoundEffect(() -> Float.valueOf(Settings.masterVolume * Settings.musicVolume * 0.75f));
    }

    public static GlobalSoundEffect ui() {
        return new GlobalSoundEffect(() -> Float.valueOf(Settings.masterVolume * Settings.UIVolume));
    }

    public static GlobalSoundEffect globalEffect() {
        return new GlobalSoundEffect(() -> Float.valueOf(Settings.masterVolume * Settings.effectsVolume));
    }

    public static PositionSoundEffect effect(PrimitiveSoundEmitter emitter) {
        return new PositionSoundEffect(() -> Float.valueOf(Settings.masterVolume * Settings.effectsVolume), emitter);
    }

    public static PositionSoundEffect effect(float soundX, float soundY) {
        return SoundEffect.effect(SoundPlayer.SimpleEmitter(soundX, soundY));
    }

    public static GlobalSoundEffect globalWeather() {
        return new GlobalSoundEffect(() -> Float.valueOf(Settings.masterVolume * Settings.weatherVolume));
    }

    public static PositionSoundEffect weather(PrimitiveSoundEmitter emitter) {
        return new PositionSoundEffect(() -> Float.valueOf(Settings.masterVolume * Settings.weatherVolume), emitter);
    }

    public static PositionSoundEffect weather(float soundX, float soundY) {
        return SoundEffect.effect(SoundPlayer.SimpleEmitter(soundX, soundY));
    }
}

