/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.lootItem;

import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.lootItem.LootInventoryItemSupplier;

public class LootInventoryItem
extends LootInventoryItemSupplier {
    public final InventoryItem item;

    public LootInventoryItem(InventoryItem item) {
        this.item = item;
    }

    @Override
    public InventoryItem getNewItem(GameRandom random, float lootMultiplier, Object ... extra) {
        return this.item.copy();
    }
}

