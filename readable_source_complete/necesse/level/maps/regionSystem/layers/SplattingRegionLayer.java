/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.layers.RegionLayer;
import necesse.level.maps.splattingManager.SplattingOptions;

public class SplattingRegionLayer
extends RegionLayer {
    protected SplattingOptions[][] splatTiles;

    public SplattingRegionLayer(Region region) {
        super(region);
    }

    @Override
    public void init() {
        if (this.level.isServer()) {
            return;
        }
        this.splatTiles = new SplattingOptions[this.region.tileWidth][this.region.tileHeight];
    }

    @Override
    public void onLayerLoaded() {
        this.updateAllSplatting();
    }

    @Override
    public void onLoadingComplete() {
        this.updateAllSplatting();
    }

    public SplattingOptions getSplatTilesByRegion(int regionTileX, int regionTileY) {
        return this.splatTiles == null ? null : this.splatTiles[regionTileX][regionTileY];
    }

    public void updateAllSplatting() {
        if (this.level.isServer()) {
            return;
        }
        this.region.updateSplattingManager();
    }

    public void updateSplattingByRegion(int regionTileX, int regionTileY) {
        if (this.level.isServer()) {
            return;
        }
        GameTile tile = this.level.getTile(regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
        if (tile.terrainSplatting || tile.isLiquid) {
            GameObject obj = this.level.getObject(regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
            if (obj.stopsTerrainSplatting()) {
                this.splatTiles[regionTileX][regionTileY] = null;
            } else {
                SplattingOptions options = new SplattingOptions(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
                this.splatTiles[regionTileX][regionTileY] = options.isNull() ? null : options;
            }
        } else {
            this.splatTiles[regionTileX][regionTileY] = null;
        }
    }

    public void addDebugTooltips(int regionTileX, int regionTileY, StringTooltips tooltips) {
        SplattingOptions options;
        GameTile tile = this.region.tileLayer.getTileByRegion(regionTileX, regionTileY);
        if (tile.terrainSplatting) {
            tooltips.add("Splat priority: " + ((TerrainSplatterTile)tile).getTerrainPriority());
        }
        if ((options = this.getSplatTilesByRegion(regionTileX, regionTileY)) != null) {
            options.addDebugTooltips(tooltips);
        }
    }

    @Override
    public void onLayerUnloaded() {
    }
}

