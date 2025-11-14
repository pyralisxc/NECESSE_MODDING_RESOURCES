/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.EmpressSlashProjectile;
import necesse.entity.trails.Trail;

public class AscendedSlashProjectile
extends EmpressSlashProjectile {
    public AscendedSlashProjectile() {
    }

    public AscendedSlashProjectile(float x, float y, float angle, GameDamage damage, Mob owner) {
        super(x, y, angle, damage, owner);
    }

    @Override
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(255, 0, 231), 40.0f, 250, 18.0f);
        trail.drawOnTop = true;
        return trail;
    }
}

