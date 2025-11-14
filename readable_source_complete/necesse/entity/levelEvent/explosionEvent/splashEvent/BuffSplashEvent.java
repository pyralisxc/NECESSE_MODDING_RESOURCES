/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent.splashEvent;

import necesse.entity.levelEvent.explosionEvent.splashEvent.SplashEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;

public abstract class BuffSplashEvent
extends SplashEvent {
    public BuffSplashEvent() {
        this(0.0f, 0.0f, 96, new GameDamage(0.0f), 0.0f, null);
    }

    public BuffSplashEvent(float x, float y, int range, GameDamage damage, float toolTier, Mob owner) {
        super(x, y, range, damage, false, toolTier, owner);
    }

    @Override
    protected void onMobWasHit(Mob mob, float distance) {
        if (this.damage.damage != 0.0f) {
            super.onMobWasHit(mob, distance);
        }
        mob.buffManager.addBuff(this.getBuff(mob), true);
    }

    protected abstract ActiveBuff getBuff(Mob var1);
}

