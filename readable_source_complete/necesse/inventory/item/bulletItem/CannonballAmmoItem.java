/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.bulletItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.CannonBallProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.bulletItem.BulletItem;

public class CannonballAmmoItem
extends BulletItem {
    public CannonballAmmoItem() {
        super(1000);
        this.damage = 60;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "cannonballtip"));
        return tooltips;
    }

    @Override
    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        return new CannonBallProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, owner);
    }

    @Override
    public boolean overrideProjectile() {
        return true;
    }
}

