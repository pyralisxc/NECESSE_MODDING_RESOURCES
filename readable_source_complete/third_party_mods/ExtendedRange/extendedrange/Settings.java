/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.ModSettings
 *  necesse.engine.save.LoadData
 *  necesse.engine.save.SaveData
 */
package extendedrange;

import necesse.engine.modLoader.ModSettings;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class Settings
extends ModSettings {
    public static int CraftingStationsRange = 30;

    public void addSaveData(SaveData save) {
        try {
            CraftingStationsRange = Math.max(CraftingStationsRange, 0);
            save.addInt("Crafting Stations Range", CraftingStationsRange, "Change the range of the 'Use nearby inventory' to the one specified here, 15 it's the vanilla default");
        }
        catch (Exception e) {
            System.err.println("[Extended range mod] An error has occurred while saving the config file.\nError:\n" + e);
        }
    }

    public void applyLoadData(LoadData save) {
        try {
            if (save == null) {
                return;
            }
            if (save.hasLoadDataByName("Crafting Stations Range")) {
                CraftingStationsRange = Math.max(save.getInt("Crafting Stations Range"), 0);
            }
        }
        catch (Exception e) {
            System.err.println("[Extended range mod] An error has occurred while loading the config file.\nError:\n" + e);
        }
    }
}

