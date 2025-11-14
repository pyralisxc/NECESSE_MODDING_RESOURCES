/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.glaiveToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.glaiveToolItem.GlaiveToolItem;
import necesse.inventory.lootTable.presets.GlaiveWeaponsLootTable;

public class FrostGlaiveToolItem
extends GlaiveToolItem {
    public FrostGlaiveToolItem() {
        super(500, GlaiveWeaponsLootTable.glaiveWeapons);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(26.0f).setUpgradedValue(1.0f, 81.66669f);
        this.attackRange.setBaseValue(140);
        this.knockback.setBaseValue(75);
        this.width = 20.0f;
        this.attackXOffset = 40;
        this.attackYOffset = 40;
        this.canBeUsedForRaids = true;
    }
}

