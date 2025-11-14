/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.trial;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class TrialRoomLevel
extends Level {
    public TrialRoomLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public TrialRoomLevel(LevelIdentifier levelIdentifier, WorldEntity worldEntity) {
        this(levelIdentifier, 50, 50, worldEntity);
        this.isCave = true;
        this.isProtected = true;
        this.baseBiome = BiomeRegistry.TRIAL_ROOM;
    }

    @Override
    public void generateRegion(Region region) {
        int floorTile = TileRegistry.getTileID("rocktile");
        for (int regionTileX = 0; regionTileX < region.tileWidth; ++regionTileX) {
            for (int regionTileY = 0; regionTileY < region.tileHeight; ++regionTileY) {
                region.tileLayer.setTileByRegion(regionTileX, regionTileY, floorTile);
            }
        }
    }

    @Override
    public boolean shouldLimitCameraWithinBounds(PlayerMob perspective) {
        return false;
    }
}

