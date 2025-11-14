/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent.splashEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.explosionEvent.splashEvent.SplashEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.ThemeColorRegistry;

public class DamageSplashEvent
extends SplashEvent {
    public DamageSplashEvent() {
        this(0.0f, 0.0f, 96, new GameDamage(20.0f), 0.0f, null);
    }

    public DamageSplashEvent(float x, float y, int range, GameDamage damage, float toolTier, Mob owner) {
        super(x, y, range, damage, false, toolTier, owner);
        this.knockback = 100;
    }

    @Override
    protected void onMobWasHit(Mob target, float distance) {
        Point2D.Float normalDir = GameMath.normalize(target.x - this.x, target.y - this.y);
        target.isServerHit(this.damage, normalDir.x, normalDir.y, this.knockback, this);
    }

    @Override
    protected Color getInnerSplashColor() {
        return ThemeColorRegistry.BLOOD.getRandomColor();
    }

    @Override
    protected Color getOuterSplashColor() {
        return this.getInnerSplashColor().brighter().brighter().brighter();
    }
}

