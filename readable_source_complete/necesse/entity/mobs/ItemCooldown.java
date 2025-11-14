/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.engine.util.GameMath;

public class ItemCooldown {
    public final long startTime;
    public final int cooldown;

    public ItemCooldown(long startTime, int cooldown) {
        this.startTime = startTime;
        this.cooldown = cooldown;
    }

    public int getTimeRemaining(long currentTime) {
        return (int)GameMath.limit(this.startTime + (long)this.cooldown - currentTime, 0L, (long)this.cooldown);
    }

    public float getPercentRemaining(long currentTime) {
        return (float)this.getTimeRemaining(currentTime) / (float)this.cooldown;
    }
}

