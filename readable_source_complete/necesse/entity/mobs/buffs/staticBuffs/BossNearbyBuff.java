/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.VicinityBuff;
import necesse.level.maps.Level;

public class BossNearbyBuff
extends VicinityBuff {
    public BossNearbyBuff() {
        this.isVisible = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setMaxModifier(BuffModifiers.MOB_SPAWN_RATE, Float.valueOf(0.0f));
        buff.setMaxModifier(BuffModifiers.MOB_SPAWN_CAP, Float.valueOf(0.0f));
    }

    public static void applyAround(Level level, float x, float y, int range) {
        level.entityManager.players.streamInRegionsShape(GameUtils.rangeBounds(x, y, range), 0).filter(p -> p.getDistance(x, y) <= (float)range).forEach(p -> {
            ActiveBuff ab = new ActiveBuff(BuffRegistry.BOSS_NEARBY, (Mob)p, 100, null);
            p.buffManager.addBuff(ab, false);
        });
    }

    public static void applyAround(Mob mob, int range) {
        BossNearbyBuff.applyAround(mob.getLevel(), mob.x, mob.y, range);
    }

    public static void applyAround(Mob mob) {
        BossNearbyBuff.applyAround(mob, 1600);
    }
}

