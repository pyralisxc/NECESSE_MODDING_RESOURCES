/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import java.util.Arrays;
import java.util.List;
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
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.BiomeExtractionIncursionData;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.presets.AncientAmethystRuinPreset;
import necesse.level.maps.presets.AncientEmeraldRuinPreset;
import necesse.level.maps.presets.AncientRubyRuinPreset;
import necesse.level.maps.presets.AncientSapphireRuinPreset;
import necesse.level.maps.presets.Preset;

public class CrystalHollowIncursionLevel
extends IncursionLevel {
    public CrystalHollowIncursionLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public CrystalHollowIncursionLevel(LevelIdentifier identifier, BiomeMissionIncursionData incursion, WorldEntity worldEntity, AltarData altarData) {
        super(identifier, 150, 150, incursion, worldEntity);
        this.baseBiome = BiomeRegistry.CRYSTAL_HOLLOW;
        this.isCave = true;
        this.generateLevel(incursion, altarData);
    }

    public void generateLevel(BiomeMissionIncursionData incursionData, AltarData altarData) {
        CaveGeneration cg = new CaveGeneration(this, "crystaltile", "crystal");
        cg.random.setSeed(incursionData.getUniqueID());
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), e -> cg.generateLevel(0.38f, 4, 3, 6));
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), e -> {
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.07f, 2, 7.0f, 20.0f, 3.0f, 8.0f, TileRegistry.getTileID("watertile"), 1.0f, true);
            this.liquidManager.calculateShores();
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("amethystclusterpure"), 0.001f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("sapphireclusterpure"), 0.001f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("emeraldclusterpure"), 0.001f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("rubyclusterpure"), 0.001f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("amethystclustersmall"), 0.005f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("sapphireclustersmall"), 0.005f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("emeraldclustersmall"), 0.005f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("rubyclustersmall"), 0.005f);
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), e -> {
            cg.generateRandomCrates(0.03f, ObjectRegistry.getObjectID("crate"));
            List<Preset> ruins = Arrays.asList(new AncientAmethystRuinPreset(), new AncientSapphireRuinPreset(), new AncientEmeraldRuinPreset(), new AncientRubyRuinPreset());
            for (int i = 0; i < 10; ++i) {
                Preset ruin = cg.random.getOneOf(ruins);
                presets.findRandomValidPositionAndApply(cg.random, 5, ruin, 4, true, false);
            }
        });
        GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, cg, presets));
        PresetGeneration entranceAndPerkPresets = new PresetGeneration(this);
        IncursionBiome.generateEntrance(this, entranceAndPerkPresets, cg.random, 30, cg.rockTile, "ancientruinfloor", "deadwoodfloor", "ancientruinwall");
        this.generatePresetsBasedOnPerks(altarData, entranceAndPerkPresets, cg.random, this.baseBiome);
        GenerationTools.checkValid(this);
        GameObject extractionObject = ObjectRegistry.getObject("pearlescentshard");
        if (incursionData instanceof BiomeExtractionIncursionData) {
            GenerationTools.generateGuaranteedRandomVeins(this, cg.random, 25, 1, 1, (level, tileX, tileY) -> level.getTileID(tileX, tileY) == cg.rockTile && extractionObject.canPlace(level, tileX, tileY, 0, false) == null, (level, tileX, tileY) -> extractionObject.placeObject(level, tileX, tileY, 0, false));
        }
        cg.generateGuaranteedOreVeins(75, 6, 12, ObjectRegistry.getObjectID("upgradeshardcrystal"));
        cg.generateGuaranteedOreVeins(75, 6, 12, ObjectRegistry.getObjectID("alchemyshardcrystal"));
        this.generateUpgradeAndAlchemyVeinsBasedOnPerks(altarData, cg, "upgradeshardcrystal", "alchemyshardcrystal", cg.random);
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
    }
}

