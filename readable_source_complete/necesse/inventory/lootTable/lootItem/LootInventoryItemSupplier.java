/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.lootItem;

import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;

public abstract class LootInventoryItemSupplier
implements LootItemInterface {
    @Override
    public void addPossibleLoot(LootList list, Object ... extra) {
        InventoryItem item = this.getNewItem(null, 1.0f, extra);
        if (item != null) {
            list.add(item.item);
        }
    }

    @Override
    public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
        InventoryItem item = this.getNewItem(random, lootMultiplier, extra);
        if (item != null) {
            list.add(item);
        }
    }

    public abstract InventoryItem getNewItem(GameRandom var1, float var2, Object ... var3);
}

