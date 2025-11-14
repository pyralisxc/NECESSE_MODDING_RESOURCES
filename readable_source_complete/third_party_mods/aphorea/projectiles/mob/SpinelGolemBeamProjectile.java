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
 *  necesse.entity.projectile.laserProjectile.LaserProjectile
 *  necesse.entity.trails.Trail
 *  necesse.level.maps.Level
 *  necesse.level.maps.LevelObjectHit
 */
package aphorea.projectiles.mob;

import aphorea.utils.AphColors;
import java.awt.Color;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.laserProjectile.LaserProjectile;
import necesse.entity.trails.Trail;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class SpinelGolemBeamProjectile
extends LaserProjectile {
    public SpinelGolemBeamProjectile() {
    }

    public SpinelGolemBeamProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
    }

    public void init() {
        super.init();
        this.setWidth(10.0f);
        this.givesLight = true;
        this.height = 24.0f;
        this.piercing = 1000;
    }

    protected int getExtraSpinningParticles() {
        return super.getExtraSpinningParticles() + 3;
    }

    public Color getParticleColor() {
        return AphColors.spinel_darker;
    }

    public Trail getTrail() {
        return new Trail((Projectile)this, this.getLevel(), AphColors.spinel_darker, 15.0f, 500, 18.0f);
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer() && mob != null) {
            ActiveBuff ab = new ActiveBuff(BuffRegistry.Debuffs.BROKEN_ARMOR, mob, 10.0f, (Attacker)this.getOwner());
            mob.addBuff(ab, true);
        }
    }
}

