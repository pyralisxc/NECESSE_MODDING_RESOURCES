/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GenerateCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GenerateCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GenerateCaveStructuresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveOresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveStructuresEvent;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.BiomeExtractionIncursionData;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionBiome;

public class SlimeCaveIncursionLevel
extends IncursionLevel {
    public SlimeCaveIncursionLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public SlimeCaveIncursionLevel(LevelIdentifier identifier, BiomeMissionIncursionData incursion, WorldEntity worldEntity, AltarData altarData) {
        super(identifier, 150, 150, incursion, worldEntity);
        this.baseBiome = BiomeRegistry.SLIME_CAVE;
        this.isCave = true;
        this.generateLevel(incursion, altarData);
    }

    public void generateLevel(BiomeMissionIncursionData incursionData, AltarData altarData) {
        CaveGeneration cg = new CaveGeneration(this, "slimerocktile", "slimerock");
        cg.random.setSeed(incursionData.getUniqueID());
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), e -> cg.generateLevel(0.38f, 4, 3, 6));
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), e -> {
            GenerationTools.generateRandomSmoothObjectVeins(this, cg.random, 0.5f, 2, 4.0f, 8.0f, 2.0f, 3.0f, ObjectRegistry.getObjectID("groundslime"), 1.0f, true);
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.12f, 2, 7.0f, 20.0f, 3.0f, 8.0f, TileRegistry.getTileID("liquidslimetile"), 1.0f, true);
            this.liquidManager.calculateShores();
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("slimecaverock"), 0.005f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("slimecaverocksmall"), 0.01f);
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), e -> cg.generateRandomCrates(0.03f, ObjectRegistry.getObjectID("swampcrate")));
        GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, cg, presets));
        PresetGeneration entranceAndPerkPresets = new PresetGeneration(this);
        IncursionBiome.generateEntrance(this, entranceAndPerkPresets, cg.random, 30, cg.rockTile, "deepswampstonebrickfloor", "deepswampstonefloor", "deepswampstonecolumn");
        this.generatePresetsBasedOnPerks(altarData, entranceAndPerkPresets, cg.random, this.baseBiome);
        GenerationTools.checkValid(this);
        if (incursionData instanceof BiomeExtractionIncursionData) {
            cg.generateGuaranteedOreVeins(100, 8, 16, ObjectRegistry.getObjectID("slimeumslimerock"));
        }
        cg.generateGuaranteedOreVeins(75, 6, 12, ObjectRegistry.getObjectID("upgradeshardslimerock"));
        cg.generateGuaranteedOreVeins(75, 6, 12, ObjectRegistry.getObjectID("alchemyshardslimerock"));
        this.generateUpgradeAndAlchemyVeinsBasedOnPerks(altarData, cg, "upgradeshardslimerock", "alchemyshardslimerock", cg.random);
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
    }
}

