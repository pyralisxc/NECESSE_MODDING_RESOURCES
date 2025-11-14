/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import necesse.engine.registries.TileRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class DebugLevel
extends Level {
    public GameTile tile;

    public DebugLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public DebugLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity, GameTile tile) {
        super(identifier, width, height, worldEntity);
        this.tile = tile;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        if (this.tile != null) {
            save.addSafeString("debugTile", this.tile.getStringID());
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        String tileStringID = save.getSafeString("debugTile", null, false);
        if (tileStringID != null) {
            this.tile = TileRegistry.getTile(tileStringID);
            if (this.tile == null) {
                this.tile = TileRegistry.getTile(TileRegistry.grassID);
            }
        }
    }

    @Override
    public boolean isOneWorldLevel() {
        return true;
    }

    @Override
    public void generateRegion(Region region) {
        super.generateRegion(region);
        if (this.tile == null) {
            this.tile = TileRegistry.getTile(TileRegistry.grassID);
        }
        for (int regionTileX = 0; regionTileX < region.tileLayer.region.tileWidth; ++regionTileX) {
            for (int regionTileY = 0; regionTileY < region.tileLayer.region.tileHeight; ++regionTileY) {
                region.tileLayer.setTileByRegion(regionTileX, regionTileY, this.tile.getID());
            }
        }
    }
}

