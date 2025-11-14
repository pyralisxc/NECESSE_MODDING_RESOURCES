/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.bulletItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.CrystalBulletProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;

public class CrystalBulletItem
extends BulletItem {
    public CrystalBulletItem() {
        this.damage = 15;
        this.rarity = Item.Rarity.RARE;
    }

    @Override
    public boolean overrideProjectile() {
        return true;
    }

    @Override
    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        return new CrystalBulletProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, owner);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "crystalammotip"));
        return tooltips;
    }
}

