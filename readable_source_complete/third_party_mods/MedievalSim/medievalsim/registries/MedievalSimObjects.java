/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.ObjectRegistry
 *  necesse.level.gameObject.GameObject
 */
package medievalsim.registries;

import medievalsim.objects.PvPZoneBarrierObject;
import medievalsim.util.ModLogger;
import necesse.engine.registries.ObjectRegistry;
import necesse.level.gameObject.GameObject;

public class MedievalSimObjects {
    public static void registerCore() {
        ObjectRegistry.registerObject((String)"pvpzonebarrier", (GameObject)new PvPZoneBarrierObject(), (float)0.0f, (boolean)false);
        ModLogger.info("Registered game objects");
    }
}

