/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.arrowItem;

import necesse.engine.localization.Localization;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;

public class BouncingArrowItem
extends ArrowItem {
    public BouncingArrowItem() {
        this.damage = 7;
        this.rarity = Item.Rarity.COMMON;
    }

    @Override
    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, ItemAttackerMob owner) {
        return ProjectileRegistry.getProjectile("bouncingarrow", owner.getLevel(), x, y, targetX, targetY, velocity, range * 3, damage, knockback, (Mob)owner);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "bouncingammotip"));
        return tooltips;
    }
}

