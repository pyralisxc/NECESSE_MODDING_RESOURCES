/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.IslandGeneration;

public class BasicSurfaceLevel
extends Level {
    public BasicSurfaceLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public BasicSurfaceLevel(int islandX, int islandY, float islandSize, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, 0), 300, 300, worldEntity);
        this.baseBiome = biome;
        this.generateLevel(islandSize);
    }

    public void generateLevel(float islandSize) {
        int size = (int)(islandSize * 90.0f) + 40;
        IslandGeneration ig = new IslandGeneration(this, size);
        int waterTile = TileRegistry.getTileID("watertile");
        int sandTile = TileRegistry.getTileID("sandtile");
        int grassTile = TileRegistry.grassID;
        if (ig.random.getChance(0.05f)) {
            ig.generateSimpleIsland(this.tileWidth / 2, this.tileHeight / 2, waterTile, grassTile, sandTile);
        } else {
            ig.generateShapedIsland(waterTile, grassTile, sandTile);
        }
    }

    @Override
    public GameMessage getLocationMessage(int tileX, int tileY) {
        return new LocalMessage("biome", "surface", "biome", this.getBiome(tileX, tileY).getLocalization());
    }
}

