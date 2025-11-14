/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.LevelRegistry
 */
package aphorea.registry;

import aphorea.levels.InfectedTrialRoomLevel;
import necesse.engine.registries.LevelRegistry;

public class AphLevels {
    public static void registerCore() {
        LevelRegistry.registerLevel((String)"infectedtrialroom", InfectedTrialRoomLevel.class);
    }
}

