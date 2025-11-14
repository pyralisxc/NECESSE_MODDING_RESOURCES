/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import java.util.function.Supplier;
import necesse.engine.sound.GlobalSoundEffect;
import necesse.engine.sound.PositionSoundEffect;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.sound.SoundPlayer;

public class RainSoundEffect
extends GlobalSoundEffect {
    private final float dx;
    private final float dy;
    private final float distance;
    private final float intensity;

    public RainSoundEffect(Supplier<Float> settingVolumeMod, float dx, float dy, float distance, float intensity) {
        super(settingVolumeMod);
        this.dx = dx;
        this.dy = dy;
        this.distance = distance;
        this.intensity = intensity;
    }

    @Override
    public void updateSound(SoundPlayer player, int source, float sourceVolumeMod, SoundEmitter listener) {
        player.alSetPitch(this.pitch);
        float volumeMod = ((Float)this.settingVolumeMod.get()).floatValue() * this.volume * this.intensity * 0.5f * sourceVolumeMod;
        float gain = PositionSoundEffect.getGain(0.02f, this.distance, 20, 200);
        player.alSetGain(volumeMod * gain);
    }
}

