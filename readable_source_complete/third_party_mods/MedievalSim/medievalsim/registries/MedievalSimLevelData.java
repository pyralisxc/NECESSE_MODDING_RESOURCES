/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.LevelDataRegistry
 */
package medievalsim.registries;

import medievalsim.util.ModLogger;
import medievalsim.zones.AdminZonesLevelData;
import necesse.engine.registries.LevelDataRegistry;

public class MedievalSimLevelData {
    public static void registerCore() {
        LevelDataRegistry.registerLevelData((String)"adminzonesdata", AdminZonesLevelData.class);
        ModLogger.info("Registered LevelData classes");
    }
}

