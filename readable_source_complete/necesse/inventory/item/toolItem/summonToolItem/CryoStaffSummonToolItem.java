/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.summonToolItem;

import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.lootTable.presets.SummonWeaponsLootTable;

public class CryoStaffSummonToolItem
extends SummonToolItem {
    public CryoStaffSummonToolItem() {
        super("playercryoflake", FollowPosition.FLYING_CIRCLE, 1.0f, 1450, SummonWeaponsLootTable.summonWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackDamage.setBaseValue(29.0f).setUpgradedValue(1.0f, 38.50001f);
    }
}

