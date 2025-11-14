/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.arrowItem;

import necesse.engine.registries.ProjectileRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.item.arrowItem.ArrowItem;

public class IronArrowItem
extends ArrowItem {
    public IronArrowItem() {
        this.damage = 10;
    }

    @Override
    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, ItemAttackerMob owner) {
        return ProjectileRegistry.getProjectile("ironarrow", owner.getLevel(), x, y, targetX, targetY, velocity, range, damage, knockback, (Mob)owner);
    }
}

