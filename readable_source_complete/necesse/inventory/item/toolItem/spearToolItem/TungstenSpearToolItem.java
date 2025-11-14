/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.spearToolItem.SpearToolItem;
import necesse.inventory.lootTable.presets.SpearWeaponsLootTable;
import necesse.level.maps.incursion.IncursionData;

public class TungstenSpearToolItem
extends SpearToolItem {
    public TungstenSpearToolItem() {
        super(1300, SpearWeaponsLootTable.spearWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(400);
        this.attackDamage.setBaseValue(44.0f).setUpgradedValue(1.0f, 70.000015f);
        this.attackRange.setBaseValue(140);
        this.knockback.setBaseValue(25);
        this.canBeUsedForRaids = true;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
    }
}

