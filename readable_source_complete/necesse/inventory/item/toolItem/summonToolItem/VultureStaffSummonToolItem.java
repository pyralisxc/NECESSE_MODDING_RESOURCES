/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.summonToolItem;

import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.lootTable.presets.SummonWeaponsLootTable;

public class VultureStaffSummonToolItem
extends SummonToolItem {
    public VultureStaffSummonToolItem() {
        super("playervulturehatchling", FollowPosition.FLYING, 1.0f, 1050, SummonWeaponsLootTable.summonWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackDamage.setBaseValue(18.0f).setUpgradedValue(1.0f, 38.50001f);
    }
}

