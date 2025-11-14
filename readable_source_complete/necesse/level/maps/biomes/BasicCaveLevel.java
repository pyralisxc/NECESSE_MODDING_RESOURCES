/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes;

import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GenerateCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GenerateCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GenerateCaveOresEvent;
import necesse.engine.events.worldGeneration.GenerateCaveStructuresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveOresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveStructuresEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;

public class BasicCaveLevel
extends Level {
    public BasicCaveLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public BasicCaveLevel(int islandX, int islandY, int dimension, WorldEntity worldEntity) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.isCave = true;
        this.generateLevel();
    }

    public void generateLevel() {
        CaveGeneration cg = new CaveGeneration(this, "rocktile", "rock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), e -> cg.generateLevel());
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        int crate = ObjectRegistry.getObjectID("crate");
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), e -> {
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("caverock"), 0.005f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("caverocksmall"), 0.01f);
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), e -> {});
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), e -> cg.generateRandomCrates(0.03f, crate));
        GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, cg, presets));
        GenerationTools.checkValid(this);
    }

    @Override
    public GameMessage getLocationMessage(int tileX, int tileY) {
        return new LocalMessage("biome", "cave", "biome", this.getBiome(tileX, tileY).getLocalization());
    }

    @Override
    public float getLiquidSaltWaterSinkRate() {
        return 4.0f;
    }

    @Override
    public float getLiquidFreshWaterSinkRate() {
        return 10.0f;
    }

    @Override
    public float getLiquidMobSinkRate() {
        return 10.0f;
    }
}

