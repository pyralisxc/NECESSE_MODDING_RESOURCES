/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import necesse.engine.sound.SoundCooldown;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundTime;

public class SimpleSoundCooldown
implements SoundCooldown {
    private final int withinMillis;

    public SimpleSoundCooldown() {
        this.withinMillis = 20;
    }

    public SimpleSoundCooldown(int withinMillis) {
        this.withinMillis = withinMillis;
    }

    @Override
    public boolean isBlockedBy(SoundPlayer me, SoundTime other) {
        return me.gameSound == other.player.gameSound && other.getAge() <= (long)this.withinMillis;
    }

    @Override
    public int getTimeToLive() {
        return this.withinMillis;
    }
}

