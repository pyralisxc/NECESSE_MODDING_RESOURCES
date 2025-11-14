/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModEntry
 *  necesse.gfx.GameColor
 */
package aphorea;

import aphorea.AphResources;
import aphorea.journal.AphJournalChallenges;
import aphorea.registry.AphBiomes;
import aphorea.registry.AphBuffs;
import aphorea.registry.AphContainers;
import aphorea.registry.AphControls;
import aphorea.registry.AphDamageType;
import aphorea.registry.AphData;
import aphorea.registry.AphEnchantments;
import aphorea.registry.AphGlobalIngredients;
import aphorea.registry.AphItemCategories;
import aphorea.registry.AphItems;
import aphorea.registry.AphJournal;
import aphorea.registry.AphLevelEvents;
import aphorea.registry.AphLevels;
import aphorea.registry.AphLootTables;
import aphorea.registry.AphMobs;
import aphorea.registry.AphModifiers;
import aphorea.registry.AphObjects;
import aphorea.registry.AphPackets;
import aphorea.registry.AphProjectiles;
import aphorea.registry.AphRecipes;
import aphorea.registry.AphSpawnTables;
import aphorea.registry.AphTech;
import aphorea.registry.AphTiles;
import aphorea.registry.AphWorldPresets;
import aphorea.utils.AphColors;
import java.awt.Color;
import java.lang.reflect.Field;
import java.util.function.Supplier;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.gfx.GameColor;

@ModEntry
public class AphoreaMod {
    public void init() throws Exception {
        System.out.println("AphoreaMod starting...");
        AphData.registerCore();
        AphLevelEvents.registerCore();
        AphPackets.registerCore();
        AphControls.registerCore();
        AphContainers.registerCore();
        AphItemCategories.registerCore();
        AphEnchantments.registerCore();
        AphDamageType.registerCore();
        AphItems.registerCore();
        AphGlobalIngredients.registerCore();
        AphTiles.registerCore();
        AphObjects.registerCore();
        AphTech.registerCore();
        AphMobs.registerCore();
        AphProjectiles.registerCore();
        AphBuffs.registerCore();
        AphBiomes.registerCore();
        AphWorldPresets.registerCore();
        AphLevels.registerCore();
        AphJournalChallenges.registerCore();
        AphJournal.registerCore();
        System.out.println("AphoreaMod started");
    }

    public void initResources() {
        AphResources.initResources();
    }

    public void postInit() {
        AphRecipes.initRecipes();
        AphSpawnTables.modifySpawnTables();
        AphLootTables.modifyLootTables();
        try {
            Supplier<Color> newColor = () -> AphColors.normal_rarity;
            Field description = GameColor.class.getDeclaredField("color");
            description.setAccessible(true);
            description.set(GameColor.ITEM_NORMAL, newColor);
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        new AphModifiers();
    }
}

