/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent.splashEvent;

import java.awt.Color;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.levelEvent.explosionEvent.splashEvent.SplashEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.TrainingDummyMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.FrozenMobImmuneBuff;
import necesse.gfx.ThemeColorRegistry;

public class FreezeSplashEvent
extends SplashEvent {
    protected int duration;

    public FreezeSplashEvent() {
        this(0.0f, 0.0f, 96, new GameDamage(0.0f), 0.0f, null, 5000);
    }

    public FreezeSplashEvent(float x, float y, int range, GameDamage damage, float toolTier, Mob owner, int duration) {
        super(x, y, range, damage, false, toolTier, owner);
        this.duration = duration;
    }

    @Override
    protected boolean canHitMob(Mob target) {
        return super.canHitMob(target) && !target.isBoss() && !(target instanceof TrainingDummyMob);
    }

    @Override
    protected void onMobWasHit(Mob target, float distance) {
        if (this.isClient()) {
            return;
        }
        Mob mount = target.getMount();
        if (mount != null) {
            FreezeSplashEvent.applyFreeze(mount, this.duration);
        }
        FreezeSplashEvent.applyFreeze(target, this.duration);
    }

    public static void applyFreeze(Mob target, int duration) {
        if (FrozenMobImmuneBuff.isBuffValidForTarget(target)) {
            ActiveBuff freezeBuff = new ActiveBuff(BuffRegistry.FROZEN_MOB, target, duration, null);
            target.buffManager.addBuff(freezeBuff, true);
        } else {
            target.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.GENERIC_ICE_SLOW, target, duration, null), true);
        }
    }

    @Override
    protected Color getInnerSplashColor() {
        return ThemeColorRegistry.ICE.getRandomColor();
    }

    @Override
    protected Color getOuterSplashColor() {
        return ThemeColorRegistry.WATER.getRandomColor();
    }
}

