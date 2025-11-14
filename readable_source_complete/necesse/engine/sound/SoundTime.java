/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundCooldown;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.sound.SoundPlayer;

public class SoundTime {
    public final SoundPlayer player;
    public final SoundCooldown cooldown;
    public final long spawnTime;

    public SoundTime(SoundPlayer player, SoundCooldown cooldown) {
        this.player = player;
        this.cooldown = cooldown;
        this.spawnTime = System.currentTimeMillis();
    }

    public boolean isNear(SoundPlayer other, int range) {
        PrimitiveSoundEmitter thisEmitter = this.player.effect.getEmitter();
        PrimitiveSoundEmitter otherEmitter = other.effect.getEmitter();
        if (thisEmitter instanceof SoundEmitter && otherEmitter instanceof SoundEmitter) {
            SoundEmitter thisEmitterPos = (SoundEmitter)thisEmitter;
            SoundEmitter otherEmitterPos = (SoundEmitter)otherEmitter;
            float deltaX = Math.abs(thisEmitterPos.getSoundPositionX() - otherEmitterPos.getSoundPositionX());
            float deltaY = Math.abs(thisEmitterPos.getSoundPositionY() - otherEmitterPos.getSoundPositionY());
            return deltaX < (float)range && deltaY < (float)range;
        }
        return thisEmitter == null && otherEmitter == null;
    }

    public long getAge() {
        return System.currentTimeMillis() - this.spawnTime;
    }
}

