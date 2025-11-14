/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.ReanimationBoltProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.NecroticBowProjectileToolItem;
import necesse.level.maps.Level;

public class ReanimationBowProjectileToolItem
extends NecroticBowProjectileToolItem {
    public ReanimationBowProjectileToolItem() {
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackYOffset = 32;
    }

    @Override
    protected void addExtraBowTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        tooltips.add(Localization.translate("itemtooltip", "reanimationbowtip1"), 400);
        tooltips.add(Localization.translate("itemtooltip", "reanimationbowtip2"), 400);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, GNDItemMap mapContent) {
        return new ReanimationBoltProjectile(level, owner, owner.x, owner.y, x, y, velocity, range, damage);
    }
}

