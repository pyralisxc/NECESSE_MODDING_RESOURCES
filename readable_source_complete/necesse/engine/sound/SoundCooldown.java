/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundTime;

public interface SoundCooldown {
    public boolean isBlockedBy(SoundPlayer var1, SoundTime var2);

    public int getTimeToLive();

    default public SoundCooldown initializeForSound(SoundPlayer sound) {
        return this;
    }
}

