/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

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
import necesse.level.maps.presets.MoonArenaBottomPreset;
import necesse.level.maps.presets.MoonArenaTopPreset;

public class MoonArenaIncursionLevel
extends IncursionLevel {
    public MoonArenaIncursionLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public MoonArenaIncursionLevel(LevelIdentifier identifier, BiomeMissionIncursionData incursion, WorldEntity worldEntity, AltarData altarData) {
        super(identifier, 92, 92, incursion, worldEntity);
        this.baseBiome = BiomeRegistry.MOON_ARENA;
        this.isCave = true;
        this.generateLevel(incursion, altarData);
    }

    public void generateLevel(BiomeMissionIncursionData incursionData, AltarData altarData) {
        GameRandom random = new GameRandom(incursionData.getUniqueID());
        PresetGeneration preset = new PresetGeneration(this);
        preset.applyPreset(new MoonArenaTopPreset(), 0, 0);
        preset.applyPreset(new MoonArenaBottomPreset(), 0, 46);
        PresetGeneration perkPresets = new PresetGeneration(this);
        int spawnSize = 640;
        float portalLevelX = 1471.5f;
        float portalLevelY = 2720.5f;
        perkPresets.addOccupiedSpace((int)(portalLevelX - (float)(spawnSize / 2)), (int)(portalLevelY - (float)(spawnSize / 2)), spawnSize, spawnSize);
        this.generatePresetsBasedOnPerks(altarData, perkPresets, random, this.baseBiome);
        IncursionBiome.addReturnPortal(this, portalLevelX, portalLevelY);
        GenerationTools.checkValid(this);
    }
}

