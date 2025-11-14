/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.lootItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameRandom;
import necesse.gfx.fairType.TypeParsers;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;

public class LootItemList
extends ArrayList<LootItemInterface>
implements LootItemInterface {
    protected GameMessage customName;

    public LootItemList(LootItemInterface ... items) {
        this.addAll(Arrays.asList(items));
    }

    public LootItemList setCustomListName(GameMessage name) {
        this.customName = name;
        return this;
    }

    public LootItemList setCustomListName(String category, String key) {
        return this.setCustomListName(new LocalMessage(category, key));
    }

    @Override
    public void addPossibleLoot(LootList list, Object ... extra) {
        if (this.customName != null) {
            LootList items = new LootList();
            for (LootItemInterface item : this) {
                item.addPossibleLoot(items, extra);
            }
            ArrayList<InventoryItem> itemsInLootList = items.getCombinedItemsAndCustomItems();
            if (!itemsInLootList.isEmpty()) {
                list.addCustomRewardString(addDisplayNames -> {
                    String itemString = TypeParsers.getItemsParseString(itemsInLootList);
                    if (addDisplayNames) {
                        itemString = itemString + " " + this.customName.translate();
                    }
                    return itemString;
                });
            }
        } else {
            for (LootItemInterface item : this) {
                item.addPossibleLoot(list, extra);
            }
        }
    }

    @Override
    public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
        for (LootItemInterface item : this) {
            item.addItems(list, random, lootMultiplier, extra);
        }
    }
}

