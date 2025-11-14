/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.dungeon;

import java.awt.Rectangle;
import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GenerateIslandFeatureEvent;
import necesse.engine.events.worldGeneration.GeneratedIslandFeatureEvent;
import necesse.engine.network.server.Server;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.forest.ForestSurfaceLevel;
import necesse.level.maps.generationModules.IslandGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.DungeonEntrancePreset;

public class DungeonSurfaceLevel
extends ForestSurfaceLevel {
    public DungeonSurfaceLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public DungeonSurfaceLevel(int islandX, int islandY, float islandSize, Server server, WorldEntity worldEntity, Biome biome) {
        super(islandX, islandY, islandSize, server, worldEntity, biome);
    }

    @Override
    protected void preGeneratedStructures(float islandSize, IslandGeneration ig, PresetGeneration presets) {
        super.preGeneratedStructures(islandSize, ig, presets);
        GameEvents.triggerEvent(new GenerateIslandFeatureEvent(this, islandSize), e -> {
            DungeonEntrancePreset preset = new DungeonEntrancePreset(ig.random);
            int centerX = this.tileWidth / 2;
            int centerY = this.tileHeight / 2;
            int presetWidth = preset.width + 40;
            int presetHeight = preset.height + 40;
            presets.addOccupiedSpace(new Rectangle(centerX - presetWidth / 2, centerY - presetHeight / 2, presetWidth, presetHeight));
            preset.applyToLevelCentered(this, this.tileWidth / 2, this.tileHeight / 2);
        });
        GameEvents.triggerEvent(new GeneratedIslandFeatureEvent(this, islandSize));
    }
}

