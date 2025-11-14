/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.lootItem;

import java.util.Arrays;
import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.LootItemList;

public class OneOfLootItems
extends LootItemList {
    public OneOfLootItems(LootItemInterface ... items) {
        super(new LootItemInterface[0]);
        this.addAll(Arrays.asList(items));
    }

    @Override
    public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
        if (!this.isEmpty()) {
            random.getOneOf(this).addItems(list, random, lootMultiplier, extra);
        }
    }
}

