/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.BuffRegistry$Debuffs
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.projectile.Projectile
 *  necesse.entity.trails.Trail
 *  necesse.level.maps.Level
 *  necesse.level.maps.LevelObjectHit
 */
package aphorea.projectiles.toolitem;

import aphorea.projectiles.toolitem.SlingStoneProjectile;
import aphorea.utils.AphColors;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class FrozenSlingStoneProjectile
extends SlingStoneProjectile {
    public FrozenSlingStoneProjectile() {
    }

    public FrozenSlingStoneProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback);
    }

    @Override
    public Trail getTrail() {
        return new Trail((Projectile)this, this.getLevel(), AphColors.ice, 26.0f, 100, this.getHeight());
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer() && mob != null) {
            mob.addBuff(new ActiveBuff(BuffRegistry.Debuffs.FREEZING, mob, 10000, (Attacker)this), true);
        }
    }
}

