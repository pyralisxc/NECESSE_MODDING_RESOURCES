/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;
import necesse.inventory.lootTable.presets.GreatswordWeaponsLootTable;
import necesse.level.maps.incursion.IncursionData;

public class GlacialGreatswordToolItem
extends GreatswordToolItem {
    public GlacialGreatswordToolItem() {
        super(1450, GreatswordWeaponsLootTable.greatswordWeapons, GlacialGreatswordToolItem.getThreeChargeLevels(500, 600, 700));
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackDamage.setBaseValue(180.0f).setUpgradedValue(1.0f, 221.66673f);
        this.attackRange.setBaseValue(130);
        this.knockback.setBaseValue(150);
        this.attackXOffset = 12;
        this.attackYOffset = 14;
        this.canBeUsedForRaids = true;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
    }
}

