/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import java.awt.Point;
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
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastlePreset;

public class SpiderCastleIncursionLevel
extends IncursionLevel {
    public SpiderCastleIncursionLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public SpiderCastleIncursionLevel(LevelIdentifier identifier, BiomeMissionIncursionData incursion, WorldEntity worldEntity, AltarData altarData) {
        super(identifier, 150, 150, incursion, worldEntity);
        this.baseBiome = BiomeRegistry.SPIDER_CASTLE;
        this.isCave = true;
        this.generateLevel(incursion, altarData);
    }

    public void generateLevel(BiomeMissionIncursionData incursionData, AltarData altarData) {
        CaveGeneration cg = new CaveGeneration(this, "spidercobbletile", "spiderrock");
        cg.random.setSeed(incursionData.getUniqueID());
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), e -> cg.generateLevel(0.38f, 4, 3, 6));
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        PresetGeneration presets = new PresetGeneration(this);
        PresetGeneration entranceAndPerkPresets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), e -> {
            cg.generateRandomCrates(0.03f, ObjectRegistry.getObjectID("spideregg"));
            cg.generateTileVeins(0.3f, 6, 12, TileRegistry.getTileID("spidernesttile"), ObjectRegistry.cobWebID);
            Point portalSpawnPoint = SpiderCastlePreset.generateSpiderCasteOnLevel(this, cg.random);
            int spawnSize = 40;
            entranceAndPerkPresets.addOccupiedSpace(portalSpawnPoint.x - spawnSize / 2, portalSpawnPoint.y - spawnSize / 2, spawnSize, spawnSize);
            cg.generateRandomCrates(0.03f, ObjectRegistry.getObjectID("crate"));
        });
        GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, cg, presets));
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), e -> {
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("spidercaverock"), 0.01f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("spidercaverocksmall"), 0.025f);
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        this.generatePresetsBasedOnPerks(altarData, entranceAndPerkPresets, cg.random, this.baseBiome);
        GenerationTools.checkValid(this);
        if (incursionData instanceof BiomeExtractionIncursionData) {
            cg.generateGuaranteedOreVeins(100, 8, 16, ObjectRegistry.getObjectID("spideritespiderrock"));
        }
        cg.generateGuaranteedOreVeins(75, 6, 12, ObjectRegistry.getObjectID("upgradeshardspiderrock"));
        cg.generateGuaranteedOreVeins(75, 6, 12, ObjectRegistry.getObjectID("alchemyshardspiderrock"));
        this.generateUpgradeAndAlchemyVeinsBasedOnPerks(altarData, cg, "upgradeshardspiderrock", "alchemyshardspiderrock", cg.random);
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
    }
}

