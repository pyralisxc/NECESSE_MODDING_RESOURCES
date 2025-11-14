/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootList;

public interface LootItemInterface {
    public void addPossibleLoot(LootList var1, Object ... var2);

    default public void addPossibleCustomLoot(LootList list, Object ... extra) {
        ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
        this.addItems(items, GameRandom.globalRandom, 1.0f, extra);
        for (InventoryItem item : items) {
            list.addCustom(item);
        }
    }

    public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object ... var4);
}

