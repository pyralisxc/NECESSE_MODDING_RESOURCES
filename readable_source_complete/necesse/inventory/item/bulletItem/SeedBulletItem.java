/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.bulletItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.SeedBulletProjectile;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;

public class SeedBulletItem
extends BulletItem {
    public SeedBulletItem() {
        this.damage = 6;
        this.rarity = Item.Rarity.COMMON;
    }

    @Override
    public GameMessage getNewLocalization() {
        return new StaticMessage("NOT_OBTAINABLE: Seed Bullet");
    }

    @Override
    public boolean overrideProjectile() {
        return true;
    }

    @Override
    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        return new SeedBulletProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, owner);
    }
}

