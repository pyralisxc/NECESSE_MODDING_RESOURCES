/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.lootItem;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;

public class LootItem
implements LootItemInterface {
    public final String itemStringID;
    public final Function<GameRandom, Integer> amountSupplier;
    public final GNDItemMap itemGNDData;
    protected int minItemsPerStack = 1;
    protected int maxSplitStacks = 1;
    protected boolean preventLootMultiplier;

    public LootItem(String itemStringID, Function<GameRandom, Integer> amountSupplier, GNDItemMap gndData) {
        Objects.requireNonNull(itemStringID);
        this.itemStringID = itemStringID;
        Objects.requireNonNull(amountSupplier);
        this.amountSupplier = amountSupplier;
        this.itemGNDData = gndData;
    }

    public LootItem(String itemStringID, Function<GameRandom, Integer> amountSupplier) {
        this(itemStringID, amountSupplier, null);
    }

    public LootItem(String itemStringID, int amount, GNDItemMap gndData) {
        this(itemStringID, r -> amount, gndData);
    }

    public LootItem(String itemStringID, int amount) {
        this(itemStringID, amount, null);
    }

    public LootItem(String itemStringID, GNDItemMap gndData) {
        this(itemStringID, 1, gndData);
    }

    public LootItem(String itemStringID) {
        this(itemStringID, 1);
    }

    public InventoryItem getItem(GameRandom random) {
        Item item = ItemRegistry.getItem(this.itemStringID);
        if (item == null) {
            System.err.println("Could not find loot item with stringID " + this.itemStringID);
            return null;
        }
        InventoryItem invItem = item.getDefaultLootItem(random, this.amountSupplier.apply(random));
        if (this.itemGNDData != null) {
            invItem.setGndData(this.itemGNDData.copy());
        }
        return invItem;
    }

    @Override
    public void addPossibleLoot(LootList list, Object ... extra) {
        list.add(this.itemStringID);
    }

    @Override
    public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
        InventoryItem item;
        if (this.preventLootMultiplier) {
            lootMultiplier = 1.0f;
        }
        if ((item = this.getItem(random)) == null) {
            return;
        }
        int itemAmount = LootTable.getLootAmount(random, item.getAmount(), lootMultiplier);
        if (itemAmount > 0) {
            if (this.maxSplitStacks > 1) {
                int totalStacks = Math.max(Math.min(this.maxSplitStacks, itemAmount / this.minItemsPerStack), 1);
                int itemsPerStack = itemAmount / totalStacks;
                int stacksWithOneMore = itemAmount - itemsPerStack * totalStacks;
                for (int i = 0; i < totalStacks; ++i) {
                    int itemsInStack = itemsPerStack + (i < stacksWithOneMore ? 1 : 0);
                    list.add(item.copy(itemsInStack));
                }
            } else {
                list.add(item.copy(itemAmount));
            }
        }
    }

    public LootItem preventLootMultiplier() {
        this.preventLootMultiplier = true;
        return this;
    }

    public LootItem splitItems(int minItemsPerStack, int maxSplitStacks) {
        if (minItemsPerStack < 1) {
            throw new IllegalArgumentException("minItemsPerStack must be more than one");
        }
        if (maxSplitStacks < 1) {
            throw new IllegalArgumentException("maxSplitStacks must be more than one");
        }
        this.minItemsPerStack = minItemsPerStack;
        this.maxSplitStacks = maxSplitStacks;
        return this;
    }

    public LootItem splitItems(int maxSplitStacks) {
        return this.splitItems(this.minItemsPerStack, maxSplitStacks);
    }

    public static LootItem between(String itemStringID, int minAmount, int maxAmount, GNDItemMap gndData) {
        return new LootItem(itemStringID, r -> r.getIntBetween(minAmount, maxAmount), gndData);
    }

    public static LootItem between(String itemStringID, int minAmount, int maxAmount) {
        return LootItem.between(itemStringID, minAmount, maxAmount, null);
    }

    public static LootItem offset(String itemStringID, int middle, int offset, GNDItemMap gndData) {
        return new LootItem(itemStringID, r -> r.getIntOffset(middle, offset), gndData);
    }

    public static LootItem offset(String itemStringID, int middle, int offset) {
        return LootItem.offset(itemStringID, middle, offset, null);
    }

    public String toString() {
        return "LootItem@" + Integer.toHexString(this.hashCode()) + "[" + this.itemStringID + "]";
    }
}

