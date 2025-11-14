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
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.BiomeExtractionIncursionData;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionBiome;

public class SwampDeepCaveIncursionLevel
extends IncursionLevel {
    public SwampDeepCaveIncursionLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public SwampDeepCaveIncursionLevel(LevelIdentifier identifier, BiomeMissionIncursionData incursion, WorldEntity worldEntity, AltarData altarData) {
        super(identifier, 150, 150, incursion, worldEntity);
        this.baseBiome = BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION;
        this.isCave = true;
        this.generateLevel(incursion, altarData);
    }

    public void generateLevel(BiomeMissionIncursionData incursionData, AltarData altarData) {
        CaveGeneration cg = new CaveGeneration(this, "deepswamprocktile", "deepswamprock");
        cg.random.setSeed(incursionData.getUniqueID());
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), e -> cg.generateLevel(0.38f, 4, 3, 6));
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), e -> {
            GenerationTools.generateRandomSmoothVeinsC(this, cg.random, 0.02f, 4, 15.0f, 25.0f, 3.0f, 5.0f, ca -> ca.forEachTile(this, (level, tileX, tileY) -> {
                level.setTile(tileX, tileY, TileRegistry.spiderNestID);
                if (cg.random.getChance(0.95f)) {
                    level.setObject(tileX, tileY, ObjectRegistry.cobWebID);
                } else {
                    level.setObject(tileX, tileY, 0);
                }
            }));
            GameTile swampRockTile = TileRegistry.getTile(TileRegistry.deepSwampRockID);
            GameObject tallGrass = ObjectRegistry.getObject(ObjectRegistry.getObjectID("deepswamptallgrass"));
            GenerationTools.generateRandomSmoothVeinsC(this, cg.random, 0.03f, 5, 4.0f, 10.0f, 3.0f, 5.0f, cells -> cells.forEachTile(this, (level, tileX, tileY) -> {
                swampRockTile.placeTile(level, tileX, tileY, false);
                this.setObject(tileX, tileY, 0);
                if (cg.random.getChance(0.85f) && tallGrass.canPlace(level, tileX, tileY, 0, false) == null) {
                    tallGrass.placeObject(level, tileX, tileY, 0, false);
                }
            }));
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.04f, 2, 2.0f, 10.0f, 2.0f, 4.0f, TileRegistry.getTileID("lavatile"), 1.0f, true);
            this.liquidManager.calculateShores();
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepswampcaverock"), 0.005f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepswampcaverocksmall"), 0.01f);
            GameObject grassObject = ObjectRegistry.getObject(ObjectRegistry.getObjectID("deepswampgrass"));
            GenerationTools.iterateLevel(this, (x, y) -> this.getTileID((int)x, (int)y) == TileRegistry.deepSwampRockID && this.getObjectID((int)x, (int)y) == 0 && cg.random.getChance(0.6f), (x, y) -> grassObject.placeObject(this, (int)x, (int)y, 0, false));
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
            cg.generateGuaranteedOreVeins(100, 8, 16, ObjectRegistry.getObjectID("myceliumoredeepswamprock"));
        }
        cg.generateGuaranteedOreVeins(75, 6, 12, ObjectRegistry.getObjectID("upgradesharddeepswamprock"));
        cg.generateGuaranteedOreVeins(75, 6, 12, ObjectRegistry.getObjectID("alchemysharddeepswamprock"));
        this.generateUpgradeAndAlchemyVeinsBasedOnPerks(altarData, cg, "upgradesharddeepswamprock", "alchemysharddeepswamprock", cg.random);
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
    }
}

