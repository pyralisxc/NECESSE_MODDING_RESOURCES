/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import necesse.engine.sound.SoundCooldown;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundTime;

public class SameNearSoundCooldown
implements SoundCooldown {
    private final int withinMillis;
    private final int withinRange;

    public SameNearSoundCooldown(int withinMillis, int withinRange) {
        this.withinMillis = withinMillis;
        this.withinRange = withinRange;
    }

    @Override
    public boolean isBlockedBy(SoundPlayer me, SoundTime other) {
        return me.gameSound == other.player.gameSound && other.getAge() <= (long)this.withinMillis && other.isNear(me, this.withinRange);
    }

    @Override
    public int getTimeToLive() {
        return this.withinMillis;
    }
}

