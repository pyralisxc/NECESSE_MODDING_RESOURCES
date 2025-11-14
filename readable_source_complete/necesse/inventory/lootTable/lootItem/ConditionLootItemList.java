/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.lootItem;

import java.util.List;
import java.util.function.BiFunction;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.LootItemList;

public class ConditionLootItemList
extends LootItemList {
    public final BiFunction<GameRandom, Object[], Boolean> condition;

    public ConditionLootItemList(BiFunction<GameRandom, Object[], Boolean> condition, LootItemInterface ... items) {
        super(items);
        this.condition = condition;
    }

    @Override
    public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
        if (this.condition.apply(random, extra).booleanValue()) {
            super.addItems(list, random, lootMultiplier, extra);
        }
    }
}

