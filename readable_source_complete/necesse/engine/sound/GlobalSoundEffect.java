/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import java.util.function.Supplier;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.sound.SoundPlayer;

public class GlobalSoundEffect
extends SoundEffect {
    protected Supplier<Float> settingVolumeMod;
    protected float pitch = 1.0f;

    GlobalSoundEffect(Supplier<Float> settingVolumeMod) {
        this.settingVolumeMod = settingVolumeMod;
    }

    public GlobalSoundEffect pitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    @Override
    public void updateSound(SoundPlayer player, int source, float sourceVolumeMod, SoundEmitter listener) {
        float volume = this.settingVolumeMod.get().floatValue() * this.volume * sourceVolumeMod;
        player.alSetPitch(this.pitch);
        player.alSetGain(volume);
        player.alSetPosition(GlobalSoundEffect.getSoundPosition(new float[]{0.0f, 0.0f, 0.0f}));
    }

    @Override
    public GlobalSoundEffect volume(float volume) {
        this.volume = volume;
        return this;
    }

    @Override
    public PrimitiveSoundEmitter getEmitter() {
        return null;
    }
}

