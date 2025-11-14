/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.EvilsProtectorAttack1Projectile;

public class FlamelingShooterProjectile
extends EvilsProtectorAttack1Projectile {
    public FlamelingShooterProjectile() {
    }

    public FlamelingShooterProjectile(float x, float y, float angle, float speed, int distance, GameDamage damage, Mob owner) {
        super(x, y, angle, speed, distance, damage, owner);
    }

    @Override
    public void init() {
        super.init();
        this.isSolid = true;
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        Mob owner = this.getOwner();
        if (owner == null) {
            return super.getDeathMessages();
        }
        return owner.getDeathMessages();
    }
}

