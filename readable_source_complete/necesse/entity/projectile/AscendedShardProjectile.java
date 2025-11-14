/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.CrystalDragonShardProjectile;
import necesse.entity.trails.Trail;
import necesse.level.maps.Level;

public class AscendedShardProjectile
extends CrystalDragonShardProjectile {
    public AscendedShardProjectile() {
    }

    public AscendedShardProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }

    public AscendedShardProjectile(Level level, float x, float y, float angle, float speed, GameDamage damage, Mob owner) {
        super(level, x, y, angle, speed, damage, owner);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(255, 0, 231), 6.0f, 250, 18.0f);
    }
}

