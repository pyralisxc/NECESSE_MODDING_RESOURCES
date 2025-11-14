/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.summonToolItem;

import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.lootTable.presets.IncursionSummonWeaponsLootTable;

public class PhantomCallerSummonToolItem
extends SummonToolItem {
    public PhantomCallerSummonToolItem() {
        super("playerchargingphantom", FollowPosition.FLYING_CIRCLE_FAST, 1.0f, 1900, IncursionSummonWeaponsLootTable.incursionSummonWeapons);
        this.rarity = Item.Rarity.EPIC;
        this.attackDamage.setBaseValue(27.0f).setUpgradedValue(1.0f, 35.000008f);
    }
}

