/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.trinketItem;

import java.util.Arrays;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class SimpleTrinketItem
extends TrinketItem {
    private String[] buffStringIDs;

    public SimpleTrinketItem(Item.Rarity rarity, String[] buffStringIDs, int enchantCost, OneOfLootItems lootTableCategory) {
        super(rarity, enchantCost, lootTableCategory);
        this.buffStringIDs = buffStringIDs;
    }

    public SimpleTrinketItem(Item.Rarity rarity, String buffStringID, int enchantCost, OneOfLootItems lootTableCategory) {
        this(rarity, new String[]{buffStringID}, enchantCost, lootTableCategory);
    }

    @Override
    public TrinketBuff[] getBuffs(InventoryItem item) {
        return (TrinketBuff[])Arrays.stream(this.buffStringIDs).map(s -> (TrinketBuff)BuffRegistry.getBuff(s)).toArray(TrinketBuff[]::new);
    }
}

