/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionBoundsExecutorAbstract;
import necesse.level.maps.regionSystem.RegionManager;

public class RegionBoundsExecutor
extends RegionBoundsExecutorAbstract<Region> {
    private final RegionManager manager;
    private final boolean loadIfNotLoaded;

    public RegionBoundsExecutor(RegionManager manager, int startTileX, int startTileY, int endTileX, int endTileY, boolean loadIfNotLoaded) {
        super(startTileX, startTileY, endTileX, endTileY, 4);
        this.manager = manager;
        this.loadIfNotLoaded = loadIfNotLoaded;
    }

    @Override
    protected RegionBoundsExecutorAbstract.RegionBounds<Region> getRegionBounds(int regionX, int regionY) {
        Region region = this.manager.getRegion(regionX, regionY, this.loadIfNotLoaded);
        if (region == null) {
            return null;
        }
        int regionStartTileX = Math.max(0, this.startTileX - region.tileXOffset);
        int regionStartTileY = Math.max(0, this.startTileY - region.tileYOffset);
        int regionEndTileX = Math.min(region.tileWidth - 1, this.endTileX - region.tileXOffset);
        int regionEndTileY = Math.min(region.tileHeight - 1, this.endTileY - region.tileYOffset);
        return new RegionBoundsExecutorAbstract.RegionBounds<Region>(region, regionX, regionY, regionStartTileX, regionStartTileY, regionEndTileX, regionEndTileY);
    }

    @Override
    protected int getRegionTileXOffset(Region region) {
        return region.tileXOffset;
    }

    @Override
    protected int getRegionTileYOffset(Region region) {
        return region.tileYOffset;
    }
}

