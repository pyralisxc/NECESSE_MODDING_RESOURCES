/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.BuffRegistry
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 */
package medievalsim.registries;

import medievalsim.buffs.PvPImmunityBuff;
import medievalsim.util.ModLogger;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class MedievalSimBuffs {
    public static void registerCore() {
        BuffRegistry.registerBuff((String)"pvpimmunity", (Buff)new PvPImmunityBuff());
        ModLogger.info("Registered buffs");
    }
}

