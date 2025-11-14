/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.layers.BiomeBlendingOptions;
import necesse.level.maps.regionSystem.layers.RegionLayer;

public class BiomeBlendingRegionLayer
extends RegionLayer {
    protected BiomeBlendingOptions[][] blendOptions;

    public BiomeBlendingRegionLayer(Region region) {
        super(region);
    }

    @Override
    public void init() {
        if (this.level.isServer()) {
            return;
        }
        this.blendOptions = new BiomeBlendingOptions[this.region.tileWidth][this.region.tileHeight];
        for (int x = 0; x < this.region.tileWidth; ++x) {
            for (int y = 0; y < this.region.tileHeight; ++y) {
                this.blendOptions[x][y] = new BiomeBlendingOptions();
            }
        }
    }

    @Override
    public void onLayerLoaded() {
        if (this.level.isServer()) {
            return;
        }
        this.region.updateBiomeBlending();
    }

    @Override
    public void onLoadingComplete() {
        if (this.level.isServer()) {
            return;
        }
        this.region.updateBiomeBlending();
    }

    public BiomeBlendingOptions getBlendingOptionsByRegion(int regionTileX, int regionTileY) {
        if (this.blendOptions == null) {
            return null;
        }
        return this.blendOptions[regionTileX][regionTileY];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addDebugTooltips(int regionTileX, int regionTileY, StringTooltips tooltips) {
        BiomeBlendingOptions options = this.getBlendingOptionsByRegion(regionTileX, regionTileY);
        if (options == null) {
            return;
        }
        Object object = this.level.entityManager.lock;
        synchronized (object) {
            options.addDebugTooltips(tooltips);
        }
    }

    @Override
    public void onLayerUnloaded() {
    }
}

