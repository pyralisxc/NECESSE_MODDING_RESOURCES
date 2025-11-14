/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.summonToolItem;

import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;

public class PirateHookSummonToolItem
extends SummonToolItem {
    public PirateHookSummonToolItem() {
        super("playerghostship", FollowPosition.WIDE_CIRCLE_MOVEMENT, 2.0f, 1050, null);
        this.rarity = Item.Rarity.RARE;
        this.attackDamage.setBaseValue(150.0f).setUpgradedValue(1.0f, 262.50006f);
    }
}

