/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.glaiveToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.glaiveToolItem.GlaiveToolItem;
import necesse.inventory.lootTable.presets.GlaiveWeaponsLootTable;
import necesse.level.maps.incursion.IncursionData;

public class QuartzGlaiveToolItem
extends GlaiveToolItem {
    public QuartzGlaiveToolItem() {
        super(1000, GlaiveWeaponsLootTable.glaiveWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(49.0f).setUpgradedValue(1.0f, 95.666695f);
        this.attackRange.setBaseValue(140);
        this.knockback.setBaseValue(100);
        this.width = 20.0f;
        this.attackXOffset = 50;
        this.attackYOffset = 50;
        this.canBeUsedForRaids = true;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
    }
}

