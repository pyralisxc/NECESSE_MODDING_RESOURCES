/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.inventory.lootTable.presets.BowWeaponsLootTable;
import necesse.level.maps.incursion.IncursionData;

public class IvyBowProjectileToolItem
extends BowProjectileToolItem {
    public IvyBowProjectileToolItem() {
        super(850, BowWeaponsLootTable.bowWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(52.0f).setUpgradedValue(1.0f, 120.1667f);
        this.attackRange.setBaseValue(800);
        this.velocity.setBaseValue(180);
        this.attackXOffset = 8;
        this.attackYOffset = 26;
        this.canBeUsedForRaids = true;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
    }
}

