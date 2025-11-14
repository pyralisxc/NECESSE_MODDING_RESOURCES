/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import necesse.engine.Settings;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.light.LightManager;
import necesse.level.maps.presets.SunArenaBottomPreset;
import necesse.level.maps.presets.SunArenaTopPreset;

public class SunArenaIncursionLevel
extends IncursionLevel {
    public static int SUN_ARENA_AMBIENT_LIGHT = 150;

    public SunArenaIncursionLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public SunArenaIncursionLevel(LevelIdentifier identifier, BiomeMissionIncursionData incursion, WorldEntity worldEntity, AltarData altarData) {
        super(identifier, 92, 92, incursion, worldEntity);
        this.baseBiome = BiomeRegistry.SUN_ARENA;
        this.isCave = true;
        this.generateLevel(incursion, altarData);
    }

    public void generateLevel(BiomeMissionIncursionData incursionData, AltarData altarData) {
        GameRandom random = new GameRandom(incursionData.getUniqueID());
        PresetGeneration preset = new PresetGeneration(this);
        preset.applyPreset(new SunArenaTopPreset(), 0, 0);
        preset.applyPreset(new SunArenaBottomPreset(), 0, 46);
        PresetGeneration perkPresets = new PresetGeneration(this);
        int spawnSize = 640;
        float portalLevelX = 1471.5f;
        float portalLevelY = 2720.5f;
        perkPresets.addOccupiedSpace((int)(portalLevelX - (float)(spawnSize / 2)), (int)(portalLevelY - (float)(spawnSize / 2)), spawnSize, spawnSize);
        this.generatePresetsBasedOnPerks(altarData, perkPresets, random, this.baseBiome);
        IncursionBiome.addReturnPortal(this, portalLevelX, portalLevelY);
        GenerationTools.checkValid(this);
    }

    @Override
    public LightManager constructLightManager() {
        return new LightManager(this){

            @Override
            public void updateAmbientLight() {
                if (this.ambientLightOverride != null) {
                    this.ambientLight = this.ambientLightOverride;
                    return;
                }
                if (Settings.alwaysLight) {
                    this.ambientLight = this.newLight(150.0f);
                    return;
                }
                this.ambientLight = SunArenaIncursionLevel.this.lightManager.newLight(SUN_ARENA_AMBIENT_LIGHT);
            }
        };
    }
}

