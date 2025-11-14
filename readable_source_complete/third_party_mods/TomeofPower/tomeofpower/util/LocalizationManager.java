/*
 * Decompiled with CFR 0.152.
 */
package tomeofpower.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import tomeofpower.util.ErrorHandler;
import tomeofpower.util.TomeLogger;

public class LocalizationManager {
    private static final Map<String, String> translations = new HashMap<String, String>();
    private static boolean initialized = false;
    private static String currentLanguage = "en";

    public static void initialize() {
        if (initialized) {
            return;
        }
        try {
            LocalizationManager.loadLanguageFile(currentLanguage);
            initialized = true;
            TomeLogger.info("Localization initialized for language: " + currentLanguage);
        }
        catch (Exception e) {
            ErrorHandler.handleConfigError(e, "localization_init");
            LocalizationManager.loadFallbackStrings();
            initialized = true;
        }
    }

    public static String get(String key) {
        String translation;
        if (!initialized) {
            LocalizationManager.initialize();
        }
        if ((translation = translations.get(key)) != null) {
            return translation;
        }
        TomeLogger.debug("Missing translation for key: " + key);
        return "[" + key + "]";
    }

    public static String get(String key, Object ... args) {
        String template = LocalizationManager.get(key);
        try {
            return String.format(template, args);
        }
        catch (Exception e) {
            ErrorHandler.handleConfigError(e, "localization_format:" + key);
            return template + " " + Arrays.toString(args);
        }
    }

    public static boolean hasTranslation(String key) {
        return translations.containsKey(key);
    }

    private static void loadLanguageFile(String languageCode) {
        if ("en".equals(languageCode)) {
            LocalizationManager.loadEnglishStrings();
        } else {
            TomeLogger.warn("Language " + languageCode + " not supported, falling back to English");
            LocalizationManager.loadEnglishStrings();
        }
    }

    private static void loadEnglishStrings() {
        translations.put("ui.tome.name", "Tome of Power");
        translations.put("ui.tome.tooltip", "A mystical trinket that channels enchantment scrolls");
        translations.put("ui.tome.empty", "Empty tome - add enchantment scrolls");
        translations.put("ui.container.title", "Tome of Power");
        translations.put("chat.enchantment.cap_exceeded", "Tome of Power: Returned %d excess %s (cap: %d per type)");
        translations.put("chat.enchantment.single", "enchantment");
        translations.put("chat.enchantment.plural", "enchantments");
        translations.put("chat.inventory.full", "Warning: Inventory full - could not return excess enchantment scroll");
        translations.put("chat.loyal.warning", "Loyal enchantments do not work with the Tome of Power trinket.");
        translations.put("error.buff.application", "Failed to apply enchantment effects");
        translations.put("error.inventory.operation", "Inventory operation failed");
        translations.put("error.enchantment.processing", "Could not process enchantment");
        translations.put("error.cache.failure", "Cache system error - performance may be reduced");
        translations.put("error.config.invalid", "Invalid configuration detected - using defaults");
        translations.put("config.trinket_slots", "Number of enchantment slots in trinket");
        translations.put("config.enchant_cap", "Maximum enchantments per type");
        translations.put("config.broker_value", "Trinket value at item broker");
        translations.put("config.recipe_cost", "Iron ingots required for crafting");
        translations.put("config.enable_caching", "Enable performance caching");
        translations.put("config.cache_duration", "Cache invalidation time (ticks)");
        translations.put("status.mod.loaded", "Tome of Power mod loaded successfully");
        translations.put("status.mod.error", "Tome of Power mod encountered errors during loading");
        translations.put("status.cache.cleared", "Buff cache cleared");
        translations.put("status.config.validated", "Configuration validated");
        translations.put("recipe.tome.name", "Tome of Power Recipe");
        translations.put("item.tome.craft_tooltip", "Crafted at Anvil with %d Iron Ingots");
        translations.put("debug.buff.applied", "Applied %d enchantments to trinket");
        translations.put("debug.cache.hit", "Cache hit for trinket calculation");
        translations.put("debug.cache.miss", "Cache miss - recalculating enchantments");
        translations.put("debug.reflection.success", "Successfully accessed %s via reflection");
        translations.put("debug.reflection.failed", "Reflection access failed for %s");
    }

    private static void loadFallbackStrings() {
        translations.clear();
        translations.put("ui.tome.name", "Tome of Power");
        translations.put("ui.tome.tooltip", "Enchantment trinket");
        translations.put("error.generic", "An error occurred");
        translations.put("chat.enchantment.cap_exceeded", "Enchantment cap exceeded - items returned");
        TomeLogger.warn("Using minimal fallback localization strings");
    }

    public static void reload(String newLanguage) {
        currentLanguage = newLanguage;
        translations.clear();
        initialized = false;
        LocalizationManager.initialize();
    }

    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    public static String getStats() {
        return String.format("Localization: %d strings loaded for language '%s'", translations.size(), currentLanguage);
    }

    public static String exportTranslations() {
        StringBuilder export = new StringBuilder("# Tome of Power Translations (" + currentLanguage + ")\n\n");
        for (Map.Entry<String, String> entry : translations.entrySet()) {
            export.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        return export.toString();
    }
}

