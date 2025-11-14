/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.ModLoader
 *  necesse.engine.modLoader.ModNextListData
 */
package aphorea;

import java.util.Objects;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModNextListData;

public class AphDependencies {
    public static String APHOREA_MOD_ID = "aphoreateam.aphoreamod";
    public static String RPG_MOD_ID = "aizsave.rpgmod";
    public static String MIGHTY_BANNER_MOD_ID = "daria40k.mightybannermod";
    public static String SUMMONER_EXPANSION_MOD_ID = "gagadoliano.summonerexpansion";

    public static boolean checkHasMod(String modId) {
        return ModLoader.getEnabledMods().stream().anyMatch(mod -> Objects.equals(mod.id, modId));
    }

    public static boolean checkOptionalDependency(String modId) {
        for (ModNextListData mod : ModLoader.getAllModsSortedByCurrentList()) {
            if (mod.mod.id.equals(APHOREA_MOD_ID)) {
                return false;
            }
            if (!mod.mod.id.equals(modId)) continue;
            return mod.enabled;
        }
        return false;
    }

    public static boolean checkRPGMod() {
        return AphDependencies.checkHasMod(RPG_MOD_ID);
    }

    public static boolean checkDependencyMightyBanner() {
        return AphDependencies.checkOptionalDependency(MIGHTY_BANNER_MOD_ID);
    }

    public static boolean checkDependencySummonerExpansion() {
        return AphDependencies.checkOptionalDependency(SUMMONER_EXPANSION_MOD_ID);
    }
}

