/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Shape;
import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobDashLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;

public class BrutesBattleaxeDashLevelEvent
extends MobDashLevelEvent {
    public BrutesBattleaxeDashLevelEvent() {
    }

    public BrutesBattleaxeDashLevelEvent(Mob owner, int seed, float dirX, float dirY, float distance, int animTime, GameDamage damage) {
        super(owner, seed, dirX, dirY, distance, animTime, damage);
    }

    @Override
    public Shape getHitBox() {
        Point2D.Float dir = this.owner.getDir() == 3 ? GameMath.getPerpendicularDir(-this.dirX, -this.dirY) : GameMath.getPerpendicularDir(this.dirX, this.dirY);
        float width = 40.0f;
        float frontOffset = 20.0f;
        float range = 120.0f;
        float rangeOffset = -80.0f;
        return new LineHitbox(this.owner.x + dir.x * rangeOffset + this.dirX * frontOffset, this.owner.y + dir.y * rangeOffset + this.dirY * frontOffset, dir.x, dir.y, range, width);
    }
}

