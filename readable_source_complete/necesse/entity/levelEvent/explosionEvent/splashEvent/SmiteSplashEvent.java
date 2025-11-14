/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent.splashEvent;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.explosionEvent.splashEvent.SplashEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SmiteLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.ThemeColorRegistry;

public class SmiteSplashEvent
extends SplashEvent {
    public SmiteSplashEvent() {
        this(0.0f, 0.0f, 96, new GameDamage(0.0f), 0.0f, null);
    }

    public SmiteSplashEvent(float x, float y, int range, GameDamage damage, float toolTier, Mob owner) {
        super(x, y, range, damage, false, toolTier, owner);
        this.isLiquid = false;
    }

    @Override
    protected boolean canHitMob(Mob target) {
        return super.canHitMob(target) && !target.isBoss();
    }

    @Override
    protected void onMobWasHit(Mob target, float distance) {
        SmiteLevelEvent event = new SmiteLevelEvent(this.ownerMob, new GameRandom(this.ownerMob.getUniqueID()), target, this.damage);
        this.getLevel().entityManager.addLevelEvent(event);
    }

    @Override
    protected Color getInnerSplashColor() {
        return ThemeColorRegistry.LIGHTNING.getRandomColor();
    }
}

