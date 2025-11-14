/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.managers;

import necesse.engine.registries.TileRegistry;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class TileLayerManager {
    protected final Level level;

    public TileLayerManager(Level level) {
        this.level = level;
    }

    public int getTileID(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return TileRegistry.emptyID;
        }
        return region.tileLayer.getTileIDByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset);
    }

    public void setTile(int tileX, int tileY, int tileID) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, true);
        if (region == null) {
            return;
        }
        region.tileLayer.setTileByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset, tileID);
    }

    public GameTile getTile(int tileX, int tileY) {
        return TileRegistry.getTile(this.getTileID(tileX, tileY));
    }

    public boolean isPlayerPlaced(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return false;
        }
        return region.tileLayer.isPlayerPlacedByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset);
    }

    public void setIsPlayerPlaced(int tileX, int tileY, boolean isPlayerPlaced) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, true);
        if (region == null) {
            return;
        }
        region.tileLayer.setIsPlayerPlacedByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset, isPlayerPlaced);
    }

    public boolean isTileLiquid(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return false;
        }
        return region.tileLayer.isTileLiquidByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset);
    }
}

