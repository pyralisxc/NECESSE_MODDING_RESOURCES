/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

import java.util.Arrays;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.layers.BooleanRegionLayer;

public class TilesProtectedRegionLayer
extends BooleanRegionLayer {
    public TilesProtectedRegionLayer(Region region) {
        super(region);
    }

    @Override
    public void init() {
    }

    @Override
    public void onLayerLoaded() {
    }

    @Override
    public void onLoadingComplete() {
    }

    @Override
    public void onLayerUnloaded() {
    }

    public void setTileProtectedByRegion(int regionTileX, int regionTileY, boolean value) {
        this.set(regionTileX, regionTileY, value);
        if (this.level.isLoadingComplete() && this.region.isLoadingComplete()) {
            this.level.addDirtyRegion(this.region);
        }
    }

    public boolean isTileProtectedByRegion(int regionTileX, int regionTileY) {
        return this.get(regionTileX, regionTileY);
    }

    @Override
    protected void handleSaveNotFound() {
    }

    @Override
    protected void handleLoadException(Exception e) {
        this.data = new boolean[this.region.tileWidth * this.region.tileHeight];
        boolean defaultValue = this.getDefault();
        if (defaultValue) {
            Arrays.fill(this.data, true);
        }
    }
}

