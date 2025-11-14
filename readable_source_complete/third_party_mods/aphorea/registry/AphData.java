/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.WorldDataRegistry
 *  necesse.engine.world.WorldEntity
 */
package aphorea.registry;

import aphorea.data.AphWorldData;
import necesse.engine.registries.WorldDataRegistry;
import necesse.engine.world.WorldEntity;

public class AphData {
    public static AphWorldData worldData;

    public static void registerCore() {
        WorldDataRegistry.registerWorldData((String)"aphoreaworlddata", AphWorldData.class);
        worldData = new AphWorldData();
    }

    public static AphWorldData getWorldData(WorldEntity world) {
        return worldData.getData(world);
    }

    public static boolean gelSlimesNulled(WorldEntity world) {
        return AphData.getWorldData((WorldEntity)world).gelSlimesNulled;
    }
}

