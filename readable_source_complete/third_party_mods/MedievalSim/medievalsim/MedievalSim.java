/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.LoadedMod
 *  necesse.engine.modLoader.ModLoader
 *  necesse.engine.modLoader.ModSettings
 *  necesse.engine.modLoader.annotations.ModEntry
 */
package medievalsim;

import medievalsim.MedievalSimSettings;
import medievalsim.commandcenter.wrapper.NecesseCommandRegistry;
import medievalsim.registries.MedievalSimBuffs;
import medievalsim.registries.MedievalSimControls;
import medievalsim.registries.MedievalSimLevelData;
import medievalsim.registries.MedievalSimObjects;
import medievalsim.registries.MedievalSimPackets;
import medievalsim.util.ModLogger;
import medievalsim.util.RuntimeConstants;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModSettings;
import necesse.engine.modLoader.annotations.ModEntry;

@ModEntry
public class MedievalSim {
    public void init() {
        ModLogger.info("Initializing mod...");
        MedievalSimObjects.registerCore();
        MedievalSimLevelData.registerCore();
        MedievalSimPackets.registerCore();
        MedievalSimControls.registerCore();
        MedievalSimBuffs.registerCore();
        ModLogger.info("Mod initialized successfully!");
        this.applySettingsToRuntime();
    }

    public void initResources() {
    }

    public void postInit() {
        ModLogger.info("Post-initialization: Scanning Necesse commands...");
        NecesseCommandRegistry.initialize();
    }

    public ModSettings initSettings() {
        ModLogger.info("Loading mod settings...");
        return new MedievalSimSettings();
    }

    public void applySettingsToRuntime() {
        try {
            LoadedMod mod = ModLoader.getEnabledMods().stream().filter(m -> "medieval.sim".equals(m.id)).findFirst().orElse(null);
            if (mod != null && mod.getSettings() instanceof MedievalSimSettings) {
                MedievalSimSettings settings = (MedievalSimSettings)mod.getSettings();
                RuntimeConstants.Zones.setPvpReentryCooldownMs(settings.pvpReentryCooldownMs);
                RuntimeConstants.Zones.setPvpSpawnImmunitySeconds(settings.pvpSpawnImmunitySeconds);
                RuntimeConstants.Zones.setDefaultDamageMultiplier(settings.pvpDefaultDamageMultiplier);
                RuntimeConstants.Zones.setMaxBarrierTiles(settings.pvpMaxBarrierTiles);
                RuntimeConstants.Zones.setBarrierAddBatchSize(settings.pvpBarrierBatchSize);
                RuntimeConstants.Zones.setBarrierMaxTilesPerTick(settings.pvpBarrierMaxTilesPerTick);
                RuntimeConstants.Zones.setDefaultCombatLockSeconds(10);
                ModLogger.info("Applied PVP settings to runtime from config");
            }
        }
        catch (Exception e) {
            ModLogger.warn("Failed to apply PVP settings to runtime", e);
        }
    }
}

