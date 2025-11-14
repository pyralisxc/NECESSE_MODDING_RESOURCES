/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;
import necesse.level.maps.incursion.IncursionData;

public class CutlassSwordToolItem
extends SwordToolItem {
    public CutlassSwordToolItem() {
        super(1150, CloseRangeWeaponsLootTable.closeRangeWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(58.0f).setUpgradedValue(1.0f, 105.00003f);
        this.attackRange.setBaseValue(65);
        this.knockback.setBaseValue(80);
        this.attackXOffset = 8;
        this.attackYOffset = 8;
        this.canBeUsedForRaids = true;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
    }
}

