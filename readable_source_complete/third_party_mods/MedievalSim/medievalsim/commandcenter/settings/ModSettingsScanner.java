/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.LoadedMod
 *  necesse.engine.modLoader.ModLoader
 *  necesse.engine.modLoader.ModSettings
 */
package medievalsim.commandcenter.settings;

import java.util.LinkedHashMap;
import java.util.Map;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModSettings;

public class ModSettingsScanner {
    public static Map<String, ModSettingsInfo> scanAllModSettings() {
        LinkedHashMap<String, ModSettingsInfo> modSettingsMap = new LinkedHashMap<String, ModSettingsInfo>();
        for (LoadedMod mod : ModLoader.getEnabledMods()) {
            ModSettings settings;
            if (mod == null || (settings = mod.getSettings()) == null) continue;
            ModSettingsInfo info = new ModSettingsInfo(mod.name, mod.id, mod.version, settings);
            modSettingsMap.put(mod.id, info);
        }
        return modSettingsMap;
    }

    public static ModSettings getModSettings(String modId) {
        for (LoadedMod mod : ModLoader.getEnabledMods()) {
            if (mod == null || !mod.id.equals(modId)) continue;
            return mod.getSettings();
        }
        return null;
    }

    public static class ModSettingsInfo {
        private final String modName;
        private final String modId;
        private final String modVersion;
        private final ModSettings settings;

        public ModSettingsInfo(String modName, String modId, String modVersion, ModSettings settings) {
            this.modName = modName;
            this.modId = modId;
            this.modVersion = modVersion;
            this.settings = settings;
        }

        public String getModName() {
            return this.modName;
        }

        public String getModId() {
            return this.modId;
        }

        public String getModVersion() {
            return this.modVersion;
        }

        public ModSettings getSettings() {
            return this.settings;
        }

        public String getDisplayName() {
            return this.modName + " v" + this.modVersion;
        }
    }
}

