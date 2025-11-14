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
import necesse.entity.projectile.VulturesBurstProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.inventory.lootTable.presets.BowWeaponsLootTable;
import necesse.level.maps.Level;

public class VulturesBurstProjectileToolItem
extends BowProjectileToolItem {
    public VulturesBurstProjectileToolItem() {
        super(1050, BowWeaponsLootTable.bowWeapons);
        this.attackAnimTime.setBaseValue(500);
        this.rarity = Item.Rarity.RARE;
        this.attackDamage.setBaseValue(55.0f).setUpgradedValue(1.0f, 116.6667f);
        this.velocity.setBaseValue(200);
        this.attackRange.setBaseValue(800);
        this.attackXOffset = 10;
        this.attackYOffset = 24;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    protected void addExtraBowTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        tooltips.add(Localization.translate("itemtooltip", "vulturesbursttip"));
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, GNDItemMap mapContent) {
        return new VulturesBurstProjectile(owner, owner.x, owner.y, x, y, velocity, range, damage, knockback);
    }
}

