/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModEntry
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.registries.ItemRegistry
 *  necesse.engine.registries.RecipeTechRegistry
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.inventory.item.Item
 *  necesse.inventory.recipe.Ingredient
 *  necesse.inventory.recipe.Recipe
 *  necesse.inventory.recipe.Recipes
 */
package tomeofpower;

import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import tomeofpower.buffs.TomeOfPowerBuff;
import tomeofpower.config.TomeConfig;
import tomeofpower.items.TomeOfPowerTrinket;
import tomeofpower.registry.TomeOfPowerContainers;
import tomeofpower.util.ErrorHandler;
import tomeofpower.util.LocalizationManager;
import tomeofpower.util.TomeLogger;

@ModEntry
public class TomeOfPowerMod {
    public TomeOfPowerMod() {
        try {
            TomeLogger.info("Starting Tome of Power mod...");
        }
        catch (Exception e) {
            System.out.println("Critical error in mod constructor: " + e.getMessage());
        }
    }

    public void init() {
        try {
            TomeLogger.info("Initializing mod components...");
            LocalizationManager.initialize();
            TomeLogger.info(LocalizationManager.get("chat.mod_loaded"));
            TomeConfig.validateConfig();
            if (TomeConfig.ENABLE_DEBUG_LOGGING) {
                TomeLogger.setLevel(TomeLogger.Level.DEBUG);
                TomeConfig.logConfiguration();
            }
            try {
                Class.forName("aphorea.registry.AphModifiers");
                TomeLogger.info(LocalizationManager.get("debug.aphorea_detected"));
            }
            catch (ClassNotFoundException e) {
                TomeLogger.info(LocalizationManager.get("debug.aphorea_not_found"));
            }
            TomeOfPowerContainers.registerCore();
            BuffRegistry.registerBuff((String)"tomeofpowerbuff", (Buff)new TomeOfPowerBuff());
            ItemRegistry.registerItem((String)"tomeofpower", (Item)new TomeOfPowerTrinket(), (float)TomeConfig.BROKER_VALUE, (boolean)true);
            Recipes.registerModRecipe((Recipe)new Recipe("tomeofpower", 1, RecipeTechRegistry.NONE, new Ingredient[]{new Ingredient("goldbar", TomeConfig.RECIPE_GOLD_BARS)}).showAfter("ironchestplate"));
            TomeLogger.info("Mod initialization complete");
        }
        catch (Exception e) {
            ErrorHandler.handleUnexpectedError(e, "mod_initialization");
            TomeLogger.error("Mod initialization failed, some features may not work");
        }
    }

    public void initResources() {
        try {
            TomeLogger.info("Loading mod resources...");
            GameTexture.fromFile((String)"items/tomeofpower");
            TomeLogger.debug("Loaded texture: items/tomeofpower");
            TomeLogger.info("Resource loading complete");
        }
        catch (Exception e) {
            ErrorHandler.handleUnexpectedError(e, "resource_loading");
            TomeLogger.error("Resource loading failed - mod may have visual issues");
        }
    }

    public void postInit() {
        try {
            TomeLogger.debug("TomeOfPowerMod postInit phase...");
            if (TomeConfig.ENABLE_PERFORMANCE_METRICS) {
                TomeLogger.info("Error statistics: " + ErrorHandler.getErrorSummary());
                TomeLogger.info("Localization status: " + LocalizationManager.getStats());
            }
            TomeLogger.info("Tome of Power mod fully loaded and ready!");
        }
        catch (Exception e) {
            ErrorHandler.handleUnexpectedError(e, "post_initialization");
        }
    }
}

