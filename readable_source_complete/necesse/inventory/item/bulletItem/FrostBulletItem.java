/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.bulletItem;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.FrostBulletProjectile;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;

public class FrostBulletItem
extends BulletItem {
    public FrostBulletItem() {
        this.damage = 7;
        this.rarity = Item.Rarity.COMMON;
    }

    @Override
    public boolean overrideProjectile() {
        return true;
    }

    @Override
    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        return new FrostBulletProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, owner);
    }
}

