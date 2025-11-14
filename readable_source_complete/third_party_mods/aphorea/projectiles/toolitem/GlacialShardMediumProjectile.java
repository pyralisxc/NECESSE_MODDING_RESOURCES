/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.projectile.Projectile
 *  necesse.entity.trails.Trail
 *  necesse.level.maps.Level
 */
package aphorea.projectiles.toolitem;

import aphorea.projectiles.toolitem.GlacialShardBigProjectile;
import aphorea.utils.AphColors;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.level.maps.Level;

public class GlacialShardMediumProjectile
extends GlacialShardBigProjectile {
    public GlacialShardMediumProjectile() {
    }

    public GlacialShardMediumProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, int seed) {
        super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback, seed);
    }

    @Override
    public void init() {
        super.init();
        this.setWidth(10.0f, true);
        this.projectilesAmount = 3;
    }

    @Override
    public Trail getTrail() {
        return new Trail((Projectile)this, this.getLevel(), AphColors.ice, 16.0f, 60, this.getHeight());
    }
}

