/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.lootItem;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.lootItem.LootItem;

public class ConditionLootItem
extends LootItem {
    public final BiFunction<GameRandom, Object[], Boolean> condition;

    public ConditionLootItem(String itemStringID, Function<GameRandom, Integer> amountSupplier, GNDItemMap gndData, BiFunction<GameRandom, Object[], Boolean> condition) {
        super(itemStringID, amountSupplier, gndData);
        this.condition = condition;
    }

    public ConditionLootItem(String itemStringID, Function<GameRandom, Integer> amountSupplier, BiFunction<GameRandom, Object[], Boolean> condition) {
        super(itemStringID, amountSupplier);
        this.condition = condition;
    }

    public ConditionLootItem(String itemStringID, int amount, GNDItemMap gndData, BiFunction<GameRandom, Object[], Boolean> condition) {
        super(itemStringID, amount, gndData);
        this.condition = condition;
    }

    public ConditionLootItem(String itemStringID, int amount, BiFunction<GameRandom, Object[], Boolean> condition) {
        super(itemStringID, amount);
        this.condition = condition;
    }

    public ConditionLootItem(String itemStringID, GNDItemMap gndData, BiFunction<GameRandom, Object[], Boolean> condition) {
        super(itemStringID, gndData);
        this.condition = condition;
    }

    public ConditionLootItem(String itemStringID, BiFunction<GameRandom, Object[], Boolean> condition) {
        super(itemStringID);
        this.condition = condition;
    }

    @Override
    public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
        if (this.condition.apply(random, extra).booleanValue()) {
            super.addItems(list, random, lootMultiplier, extra);
        }
    }

    public static ConditionLootItem between(String itemStringID, int minAmount, int maxAmount, BiFunction<GameRandom, Object[], Boolean> condition) {
        return new ConditionLootItem(itemStringID, r -> r.getIntBetween(minAmount, maxAmount), condition);
    }

    public static ConditionLootItem offset(String itemStringID, int middle, int offset, BiFunction<GameRandom, Object[], Boolean> condition) {
        return new ConditionLootItem(itemStringID, r -> r.getIntOffset(middle, offset), condition);
    }
}

