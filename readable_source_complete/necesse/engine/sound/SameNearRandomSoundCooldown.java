/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import necesse.engine.sound.SameNearSoundCooldown;
import necesse.engine.sound.SoundCooldown;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundTime;
import necesse.engine.util.GameRandom;

public class SameNearRandomSoundCooldown
implements SoundCooldown {
    private final int minWithinMillis;
    private final int maxWithinMillis;
    private final int withinRange;

    public SameNearRandomSoundCooldown(int minWithinMillis, int maxWithinMillis, int withinRange) {
        this.minWithinMillis = minWithinMillis;
        this.maxWithinMillis = maxWithinMillis;
        this.withinRange = withinRange;
    }

    @Override
    public boolean isBlockedBy(SoundPlayer me, SoundTime other) {
        return false;
    }

    @Override
    public int getTimeToLive() {
        return 0;
    }

    @Override
    public SoundCooldown initializeForSound(SoundPlayer sound) {
        return new SameNearSoundCooldown(GameRandom.globalRandom.getIntBetween(this.minWithinMillis, this.maxWithinMillis), this.withinRange);
    }
}

