/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.layers.ByteRegionLayer;

public class WireDataRegionLayer
extends ByteRegionLayer {
    public WireDataRegionLayer(Region region) {
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

    public byte getWireDataByRegion(int regionTileX, int regionTileY) {
        return this.get(regionTileX, regionTileY);
    }

    public void setWireDataByRegion(int regionTileX, int regionTileY, byte wireData) {
        this.set(regionTileX, regionTileY, wireData);
        if (this.level.isLoadingComplete() && this.region.isLoadingComplete()) {
            this.level.addDirtyRegion(this.region);
        }
    }

    @Override
    protected boolean isValidValue(int regionTileX, int regionTileY, byte value) {
        return true;
    }
}

