/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.summonToolItem;

import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.lootTable.presets.SummonWeaponsLootTable;

public class SpiderStaffSummonToolItem
extends SummonToolItem {
    public SpiderStaffSummonToolItem() {
        super("babyspider", FollowPosition.PYRAMID, 1.0f, 350, SummonWeaponsLootTable.summonWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackDamage.setBaseValue(14.0f).setUpgradedValue(1.0f, 33.833344f);
    }
}

