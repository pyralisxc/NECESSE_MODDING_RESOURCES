/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.inventory.Inventory
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.enchants.ItemEnchantment
 *  necesse.inventory.item.miscItem.EnchantingScrollItem
 */
package tomeofpower.util;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.item.miscItem.EnchantingScrollItem;
import tomeofpower.config.TomeConfig;
import tomeofpower.util.TomeLogger;

public class BuffCache {
    private static final WeakHashMap<Object, CacheEntry> trinketCaches = new WeakHashMap();

    public static Map<ItemEnchantment, Integer> getCachedEnchantments(InventoryItem trinketItem, Inventory inventory, long currentTick) {
        if (!TomeConfig.ENABLE_BUFF_CACHING) {
            return BuffCache.calculateEnchantments(inventory);
        }
        String currentHash = BuffCache.generateInventoryHash(inventory);
        CacheEntry cached = trinketCaches.get(trinketItem);
        if (cached != null && cached.isValid(currentHash, currentTick)) {
            TomeLogger.debug("Using cached enchantment data");
            return new HashMap<ItemEnchantment, Integer>(cached.enchantmentCounts);
        }
        TomeLogger.debug("Cache miss - recalculating enchantments");
        Map<ItemEnchantment, Integer> newCounts = BuffCache.calculateEnchantments(inventory);
        trinketCaches.put(trinketItem, new CacheEntry(newCounts, currentHash, currentTick));
        return newCounts;
    }

    public static void invalidateCache(InventoryItem trinketItem) {
        trinketCaches.remove(trinketItem);
        TomeLogger.debug("Invalidated cache for trinket");
    }

    private static Map<ItemEnchantment, Integer> calculateEnchantments(Inventory inventory) {
        HashMap<ItemEnchantment, Integer> enchantmentCounts = new HashMap<ItemEnchantment, Integer>();
        if (inventory == null) {
            return enchantmentCounts;
        }
        for (int i = 0; i < inventory.getSize(); ++i) {
            int stackSize;
            EnchantingScrollItem scroll;
            ItemEnchantment enchantment;
            InventoryItem scrollItem = inventory.getItem(i);
            if (scrollItem == null || !(scrollItem.item instanceof EnchantingScrollItem) || (enchantment = (scroll = (EnchantingScrollItem)scrollItem.item).getEnchantment(scrollItem)) == null || (stackSize = Math.max(0, scrollItem.getAmount())) <= 0) continue;
            Integer prev = (Integer)enchantmentCounts.get(enchantment);
            enchantmentCounts.put(enchantment, (prev == null ? 0 : prev) + stackSize);
        }
        return enchantmentCounts;
    }

    private static String generateInventoryHash(Inventory inventory) {
        if (inventory == null) {
            return "empty";
        }
        StringBuilder hash = new StringBuilder();
        hash.append("size:").append(inventory.getSize()).append(";");
        for (int i = 0; i < inventory.getSize(); ++i) {
            EnchantingScrollItem scroll;
            ItemEnchantment enchantment;
            InventoryItem item = inventory.getItem(i);
            if (item == null) continue;
            hash.append("slot").append(i).append(":").append(item.item.getID()).append("x").append(item.getAmount());
            if (item.item instanceof EnchantingScrollItem && (enchantment = (scroll = (EnchantingScrollItem)item.item).getEnchantment(item)) != null) {
                hash.append("e").append(enchantment.hashCode());
            }
            hash.append(";");
        }
        return hash.toString();
    }

    public static String getCacheStats() {
        return String.format("BuffCache: %d entries, caching %s", trinketCaches.size(), TomeConfig.ENABLE_BUFF_CACHING ? "enabled" : "disabled");
    }

    public static void clearCache() {
        trinketCaches.clear();
        TomeLogger.debug("Cleared all buff cache entries");
    }

    private static class CacheEntry {
        final Map<ItemEnchantment, Integer> enchantmentCounts;
        final String inventoryHash;
        final long lastUpdateTick;

        CacheEntry(Map<ItemEnchantment, Integer> counts, String hash, long tick) {
            this.enchantmentCounts = new HashMap<ItemEnchantment, Integer>(counts);
            this.inventoryHash = hash;
            this.lastUpdateTick = tick;
        }

        boolean isValid(String currentHash, long currentTick) {
            if (!this.inventoryHash.equals(currentHash)) {
                return false;
            }
            return currentTick - this.lastUpdateTick <= (long)TomeConfig.CACHE_INVALIDATION_TICKS;
        }
    }
}

