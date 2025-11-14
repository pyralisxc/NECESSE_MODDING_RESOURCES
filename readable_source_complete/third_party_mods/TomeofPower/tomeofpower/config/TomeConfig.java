/*
 * Decompiled with CFR 0.152.
 */
package tomeofpower.config;

import tomeofpower.util.TomeLogger;

public class TomeConfig {
    public static final int MAX_ENCHANTS_PER_SLOT = TomeConfig.getIntConfig("enchantment.max_per_slot", 10);
    public static final int TRINKET_SLOT_COUNT = TomeConfig.getIntConfig("trinket.slot_count", 9);
    public static final int RECIPE_GOLD_BARS = TomeConfig.getIntConfig("recipe.gold_bars", 5);
    public static final int RECIPE_ENCHANTING_SCROLLS = TomeConfig.getIntConfig("recipe.enchanting_scrolls", 9);
    public static final int RECIPE_BOOKS = TomeConfig.getIntConfig("recipe.books", 1);
    public static final float BROKER_VALUE = TomeConfig.getFloatConfig("item.broker_value", 100.0f);
    public static final boolean ENABLE_BUFF_CACHING = TomeConfig.getBooleanConfig("performance.enable_buff_caching", true);
    public static final int CACHE_INVALIDATION_TICKS = TomeConfig.getIntConfig("performance.cache_invalidation_ticks", 20);
    public static final boolean ENABLE_DEBUG_LOGGING = TomeConfig.getBooleanConfig("debug.enable_logging", false);
    public static final boolean ENABLE_PERFORMANCE_METRICS = TomeConfig.getBooleanConfig("debug.enable_metrics", false);

    private static int getIntConfig(String key, int defaultValue) {
        TomeLogger.debug("Config [" + key + "] = " + defaultValue + " (default)");
        return defaultValue;
    }

    private static float getFloatConfig(String key, float defaultValue) {
        TomeLogger.debug("Config [" + key + "] = " + defaultValue + " (default)");
        return defaultValue;
    }

    private static boolean getBooleanConfig(String key, boolean defaultValue) {
        TomeLogger.debug("Config [" + key + "] = " + defaultValue + " (default)");
        return defaultValue;
    }

    public static void validateConfig() {
        TomeLogger.info("Validating Tome of Power configuration...");
        if (MAX_ENCHANTS_PER_SLOT < 1 || MAX_ENCHANTS_PER_SLOT > 100) {
            TomeLogger.warn("Max enchantments per slot (" + MAX_ENCHANTS_PER_SLOT + ") may cause gameplay issues");
        }
        if (TRINKET_SLOT_COUNT < 1 || TRINKET_SLOT_COUNT > 36) {
            TomeLogger.warn("Trinket slot count (" + TRINKET_SLOT_COUNT + ") outside recommended range (1-36)");
        }
        if (RECIPE_GOLD_BARS < 0 || RECIPE_ENCHANTING_SCROLLS < 0 || RECIPE_BOOKS < 0) {
            TomeLogger.error("Recipe ingredients cannot be negative - using defaults");
        }
        TomeLogger.info("Configuration validation complete");
    }

    public static void logConfiguration() {
        TomeLogger.info("=== Tome of Power Configuration ===");
        TomeLogger.info("Max enchantments per slot: " + MAX_ENCHANTS_PER_SLOT);
        TomeLogger.info("Trinket slot count: " + TRINKET_SLOT_COUNT);
        TomeLogger.info("Recipe - Gold bars: " + RECIPE_GOLD_BARS);
        TomeLogger.info("Recipe - Enchanting scrolls: " + RECIPE_ENCHANTING_SCROLLS);
        TomeLogger.info("Recipe - Books: " + RECIPE_BOOKS);
        TomeLogger.info("Broker value: " + BROKER_VALUE);
        TomeLogger.info("Buff caching enabled: " + ENABLE_BUFF_CACHING);
        TomeLogger.info("Debug logging enabled: " + ENABLE_DEBUG_LOGGING);
        TomeLogger.info("===================================");
    }
}

