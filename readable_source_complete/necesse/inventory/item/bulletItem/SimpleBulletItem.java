/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.bulletItem;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.HandGunBulletProjectile;
import necesse.inventory.item.bulletItem.BulletItem;

public class SimpleBulletItem
extends BulletItem {
    public SimpleBulletItem() {
        this.damage = 5;
    }

    @Override
    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        return new HandGunBulletProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, owner);
    }
}

