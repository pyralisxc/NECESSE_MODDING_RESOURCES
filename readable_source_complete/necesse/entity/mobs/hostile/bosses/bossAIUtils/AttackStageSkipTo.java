/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.bossAIUtils;

import necesse.entity.mobs.Mob;

public interface AttackStageSkipTo<T extends Mob> {
    public boolean shouldSkipTo(T var1, boolean var2);
}

