/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.lootItem;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class CountOfLootItems
extends OneOfLootItems {
    protected int count;

    public CountOfLootItems(int count, LootItemInterface ... items) {
        super(items);
        this.count = count;
    }

    @Override
    public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
        ArrayList<LootItemInterface> items = new ArrayList<LootItemInterface>(this);
        for (int i = 0; i < this.count; ++i) {
            if (items.isEmpty()) {
                return;
            }
            items.remove(random.nextInt(items.size())).addItems(list, random, lootMultiplier, extra);
        }
    }
}

