/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.itemAttacker;

import java.util.function.BiConsumer;
import java.util.function.Function;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;

public class MobFollower {
    public final String summonType;
    public final Mob mob;
    public final FollowPosition position;
    public final String buffType;
    public final float spaceTaken;
    public final Function<ItemAttackerMob, Integer> maxSpace;
    public final BiConsumer<ItemAttackerMob, Mob> updateMob;

    public MobFollower(String summonType, Mob mob, FollowPosition position, String buffType, float spaceTaken, Function<ItemAttackerMob, Integer> maxSpace, BiConsumer<ItemAttackerMob, Mob> updateMob) {
        this.summonType = summonType;
        this.mob = mob;
        this.position = position;
        this.buffType = buffType;
        this.spaceTaken = spaceTaken;
        this.maxSpace = maxSpace;
        this.updateMob = updateMob;
    }
}

