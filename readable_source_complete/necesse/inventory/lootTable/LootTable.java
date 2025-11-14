/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.level.maps.Level;

public class LootTable
implements LootItemInterface {
    public final List<LootItemInterface> items = new ArrayList<LootItemInterface>();

    public LootTable() {
    }

    public LootTable(LootItemInterface ... items) {
        this();
        this.items.addAll(Arrays.asList(items));
    }

    @Override
    public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
        for (LootItemInterface items : this.items) {
            items.addItems(list, random, lootMultiplier, extra);
        }
    }

    @Override
    public void addPossibleLoot(LootList list, Object ... extra) {
        for (LootItemInterface items : this.items) {
            items.addPossibleLoot(list, extra);
        }
    }

    public void addItemsToInventory(GameRandom random, float lootMultiplier, Inventory inventory, String purpose, Object ... extra) {
        ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
        this.addItems(items, random, lootMultiplier, extra);
        for (InventoryItem item : items) {
            inventory.addItem(null, null, item, purpose, null);
        }
    }

    public final ArrayList<InventoryItem> getNewList(GameRandom random, float lootMultiplier, Object ... extra) {
        ArrayList<InventoryItem> list = new ArrayList<InventoryItem>();
        this.addItems(list, random, lootMultiplier, extra);
        return list;
    }

    public final void applyToLevel(GameRandom random, float lootMultiplier, Level level, int tileX, int tileY, Object ... extra) {
        try {
            ObjectEntity objEnt = level.entityManager.getObjectEntity(tileX, tileY);
            if (objEnt != null && objEnt.implementsOEInventory()) {
                this.addItemsToInventory(random, lootMultiplier, ((OEInventory)((Object)objEnt)).getInventory(), "addloot", extra);
            } else if (level.isServer()) {
                throw new NullPointerException("LootTable.class | Could not find an objectEntity with inventory for loot table at " + tileX + ", " + tileY);
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static <T> T expectExtra(Class<T> expectedClass, Object[] extra, int index) {
        if (index >= extra.length) {
            return null;
        }
        try {
            return expectedClass.cast(extra[index]);
        }
        catch (ClassCastException e) {
            return null;
        }
    }

    public static boolean isExtraEquals(Object[] extra, int index, Object equalsObject) {
        if (index >= extra.length) {
            return false;
        }
        return Objects.equals(extra[index], equalsObject);
    }

    public static int getLootAmount(GameRandom random, int baseAmount, float multiplier) {
        float chance;
        float floatAmount = (float)baseAmount * multiplier;
        int finalAmount = (int)floatAmount;
        if (floatAmount > (float)finalAmount && random.getChance(chance = floatAmount - (float)finalAmount)) {
            ++finalAmount;
        }
        return finalAmount;
    }

    public static void runChance(GameRandom random, float chance, float multiplier, Consumer<Float> remainingMultiplier) {
        block2: {
            while (multiplier >= 1.0f) {
                if (random.getChance(chance)) {
                    remainingMultiplier.accept(Float.valueOf(multiplier));
                    break block2;
                }
                multiplier -= 1.0f;
            }
            if (!random.getChance(multiplier) || !random.getChance(chance)) break block2;
            remainingMultiplier.accept(Float.valueOf(1.0f));
        }
    }

    public static void runChanceTimes(GameRandom random, float chance, float times, Runnable runnable) {
        block1: {
            while (times >= 1.0f) {
                times -= 1.0f;
                if (!random.getChance(chance)) continue;
                runnable.run();
            }
            if (!random.getChance(times) || !random.getChance(chance)) break block1;
            runnable.run();
        }
    }

    public static void runMultiplied(GameRandom random, float multiplier, Runnable runnable) {
        block1: {
            while (multiplier >= 1.0f) {
                multiplier -= 1.0f;
                runnable.run();
            }
            if (!random.getChance(multiplier)) break block1;
            runnable.run();
        }
    }
}

