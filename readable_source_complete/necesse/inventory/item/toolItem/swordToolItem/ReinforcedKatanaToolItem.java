/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.KatanaToolItem;
import necesse.level.maps.incursion.IncursionData;

public class ReinforcedKatanaToolItem
extends KatanaToolItem {
    public ReinforcedKatanaToolItem() {
        super(1300);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(200);
        this.attackDamage.setBaseValue(62.0f).setUpgradedValue(1.0f, 110.83337f);
        this.attackRange.setBaseValue(100);
        this.knockback.setBaseValue(75);
        this.resilienceGain.setBaseValue(1.0f);
        this.maxDashStacks.setBaseValue(15);
        this.dashRange.setBaseValue(400);
        this.attackXOffset = 4;
        this.attackYOffset = 4;
        this.canBeUsedForRaids = true;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
    }
}

