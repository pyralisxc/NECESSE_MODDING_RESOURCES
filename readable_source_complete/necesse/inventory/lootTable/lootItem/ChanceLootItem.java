/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.lootItem;

import java.util.List;
import java.util.function.Function;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class ChanceLootItem
extends LootItem {
    public final float chance;

    public ChanceLootItem(float chance, String itemStringID, Function<GameRandom, Integer> amountSupplier, GNDItemMap gndData) {
        super(itemStringID, amountSupplier, gndData);
        this.chance = chance;
    }

    public ChanceLootItem(float chance, String itemStringID, Function<GameRandom, Integer> amountSupplier) {
        super(itemStringID, amountSupplier);
        this.chance = chance;
    }

    public ChanceLootItem(float chance, String itemStringID, int amount, GNDItemMap gndData) {
        super(itemStringID, amount, gndData);
        this.chance = chance;
    }

    public ChanceLootItem(float chance, String itemStringID, int amount) {
        super(itemStringID, amount);
        this.chance = chance;
    }

    public ChanceLootItem(float chance, String itemStringID, GNDItemMap gndData) {
        super(itemStringID, gndData);
        this.chance = chance;
    }

    public ChanceLootItem(float chance, String itemStringID) {
        super(itemStringID);
        this.chance = chance;
    }

    @Override
    public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
        LootTable.runChance(random, this.chance, lootMultiplier, remainingLootMultiplier -> super.addItems(list, random, remainingLootMultiplier.floatValue(), extra));
    }

    public static ChanceLootItem between(float chance, String itemStringID, int minAmount, int maxAmount) {
        return new ChanceLootItem(chance, itemStringID, r -> r.getIntBetween(minAmount, maxAmount));
    }

    public static ChanceLootItem offset(float chance, String itemStringID, int middle, int offset) {
        return new ChanceLootItem(chance, itemStringID, r -> r.getIntOffset(middle, offset));
    }
}

