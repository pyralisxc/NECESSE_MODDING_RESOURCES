/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.ArrayList;
import java.util.Collection;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.ProtectedTicketSystemList;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.lootTable.LootItemInterface;

public class GameLootUtils {
    public static LootValueMap<InventoryItem> inventoryItemMapper = new LootValueMap<InventoryItem>(){

        @Override
        public float getValuePerCount(InventoryItem object) {
            return object.item.getBrokerValue(object);
        }

        @Override
        public int getRemainingCount(InventoryItem object) {
            return object.getAmount();
        }

        @Override
        public void setRemainingCount(InventoryItem object, int count) {
            object.setAmount(count);
        }

        @Override
        public boolean canCombine(InventoryItem object, InventoryItem other) {
            return object.canCombine(null, null, other, "lootcombine");
        }

        @Override
        public void onCombine(InventoryItem object, InventoryItem other) {
            object.item.onCombine(null, null, null, 0, object, other, Integer.MAX_VALUE, other.getAmount(), false, "lootcombine", null);
        }

        @Override
        public InventoryItem copy(InventoryItem object, int amount) {
            return object.copy(amount);
        }
    };

    private static Item[] convertStringIDs(String ... itemStringIDs) {
        Item[] items = new Item[itemStringIDs.length];
        for (int i = 0; i < items.length; ++i) {
            items[i] = ItemRegistry.getItem(itemStringIDs[i]);
        }
        return items;
    }

    private static InventoryItem[] convertItems(Item ... items) {
        InventoryItem[] invItems = new InventoryItem[items.length];
        for (int i = 0; i < items.length; ++i) {
            invItems[i] = new InventoryItem(items[i]);
        }
        return invItems;
    }

    public static ArrayList<InventoryItem> getItemsValuedAt(GameRandom random, int totalValue, double uniqueVariance, LootItemInterface lootTable, Object ... extra) {
        boolean addedItem;
        ArrayList<InventoryItem> input = new ArrayList<InventoryItem>();
        lootTable.addItems(input, random, 1.0f, extra);
        ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
        do {
            addedItem = false;
            ArrayList<InventoryItem> tempInput = new ArrayList<InventoryItem>(input);
            while (!tempInput.isEmpty()) {
                int inputSize = tempInput.size();
                InventoryItem item = tempInput.remove(random.nextInt(inputSize));
                if (item.getAmount() <= 0) {
                    input.remove(item);
                    continue;
                }
                InventoryItem singleItem = item.copy(1);
                double thisValue = random.getDoubleBetween(1.0 - uniqueVariance, 1.0 + uniqueVariance) / (double)inputSize * (double)totalValue;
                int itemCount = Math.min((int)(thisValue / (double)Math.max(singleItem.getBrokerValue(), 0.01f)), item.getAmount());
                if (itemCount <= 0) continue;
                addedItem = true;
                InventoryItem selectedItem = item.copy(itemCount);
                float selectedValue = selectedItem.getBrokerValue();
                totalValue = (int)((float)totalValue - selectedValue);
                item.setAmount(item.getAmount() - itemCount);
                GameLootUtils.addObject(items, selectedItem, inventoryItemMapper);
            }
        } while (addedItem);
        return items;
    }

    public static ArrayList<InventoryItem> getItemsValuedAt(GameRandom random, int totalValue, float uniqueVariance, ProtectedTicketSystemList<InventoryItem> input) {
        return GameLootUtils.getObjectsValuedAt(random, totalValue, uniqueVariance, input, inventoryItemMapper);
    }

    public static <T> ArrayList<T> getObjectsValuedAt(GameRandom random, int totalValue, float uniqueVariance, ProtectedTicketSystemList<T> input, LootValueMap<T> mapper) {
        uniqueVariance = GameMath.limit(uniqueVariance, 0.0f, 1.0f);
        ArrayList output = new ArrayList();
        while (!input.isEmpty()) {
            int objectCount;
            T object = input.getRandomObject(random);
            if (mapper.getRemainingCount(object) <= 0) {
                input.removeObject(object);
                continue;
            }
            T singleObject = mapper.copy(object, 1);
            float value = Math.max(mapper.getValuePerCount(singleObject) * (float)mapper.getRemainingCount(singleObject), 0.01f);
            if (value > (float)totalValue) {
                input.removeObject(object);
                continue;
            }
            if (input.getTotalElements() == 1) {
                objectCount = Math.min((int)((float)totalValue / value), mapper.getRemainingCount(object));
            } else {
                float thisValue = Math.abs(uniqueVariance - 1.0f) * (float)totalValue;
                objectCount = Math.min((int)Math.ceil(thisValue / value), mapper.getRemainingCount(object));
            }
            if (objectCount > 0) {
                T selectedObject = mapper.copy(object, objectCount);
                float selectedValue = mapper.getValuePerCount(selectedObject) * (float)objectCount;
                totalValue = (int)((float)totalValue - selectedValue);
                mapper.setRemainingCount(object, mapper.getRemainingCount(object) - objectCount);
                GameLootUtils.addObject(output, selectedObject, mapper);
                continue;
            }
            input.removeObject(object);
        }
        return output;
    }

    public static <T> void addObject(Collection<T> objects, T object, LootValueMap<T> mapper) {
        for (T cObject : objects) {
            if (!mapper.canCombine(cObject, object)) continue;
            mapper.onCombine(cObject, object);
            return;
        }
        objects.add(object);
    }

    public static interface LootValueMap<T> {
        public float getValuePerCount(T var1);

        public int getRemainingCount(T var1);

        public void setRemainingCount(T var1, int var2);

        public boolean canCombine(T var1, T var2);

        public void onCombine(T var1, T var2);

        public T copy(T var1, int var2);
    }
}

