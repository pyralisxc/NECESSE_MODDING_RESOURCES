/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.lootItem;

import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItemList;

public class ChanceLootItemList
extends LootItemList {
    public final float chance;

    public ChanceLootItemList(float chance) {
        super(new LootItemInterface[0]);
        this.chance = chance;
    }

    public ChanceLootItemList(float chance, LootItemInterface ... items) {
        super(items);
        this.chance = chance;
    }

    @Override
    public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
        LootTable.runChance(random, this.chance, lootMultiplier, remainingLootMultiplier -> super.addItems(list, random, remainingLootMultiplier.floatValue(), extra));
    }
}

