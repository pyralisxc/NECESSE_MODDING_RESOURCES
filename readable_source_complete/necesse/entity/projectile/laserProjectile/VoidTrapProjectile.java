/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.laserProjectile;

import java.awt.Color;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.laserProjectile.LaserProjectile;
import necesse.entity.trails.Trail;

public class VoidTrapProjectile
extends LaserProjectile {
    public VoidTrapProjectile() {
    }

    public VoidTrapProjectile(float x, float y, float targetX, float targetY, GameDamage damage, Mob owner) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(250);
    }

    @Override
    public void init() {
        super.init();
        this.width = 20.0f;
        this.givesLight = true;
        this.height = 18.0f;
        this.piercing = 1000;
        this.clientHandlesHit = false;
        this.canBreakObjects = true;
    }

    @Override
    protected int getExtraSpinningParticles() {
        return super.getExtraSpinningParticles() + 3;
    }

    @Override
    public Color getParticleColor() {
        return new Color(50, 0, 102);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(50, 0, 102), 20.0f, 500, this.getHeight());
    }

    @Override
    public GameMessage getAttackerName() {
        return new LocalMessage("deaths", "voidtraipname");
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("voidtrap", 3);
    }

    @Override
    public boolean isTrapAttacker() {
        return true;
    }
}

