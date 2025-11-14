/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.lootItem;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;

public class UpgradeTierLootItem
implements LootItemInterface {
    protected LootItemInterface child;
    protected float tier;

    public UpgradeTierLootItem(LootItemInterface child, float tier) {
        this.child = child;
        this.tier = tier;
    }

    @Override
    public void addPossibleLoot(LootList list, Object ... extra) {
        LootList childList = new LootList();
        this.child.addPossibleLoot(childList, extra);
        for (Item item : childList.getItems()) {
            list.add(item);
        }
        for (InventoryItem customItem : childList.getCustomItems()) {
            InventoryItem copy = customItem.copy();
            copy.item.setUpgradeTier(copy, this.tier);
            list.addCustom(copy);
        }
    }

    @Override
    public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
        ArrayList<InventoryItem> childList = new ArrayList<InventoryItem>();
        this.child.addItems(childList, random, lootMultiplier, extra);
        for (InventoryItem inventoryItem : childList) {
            inventoryItem.item.setUpgradeTier(inventoryItem, this.tier);
            list.add(inventoryItem);
        }
    }
}

