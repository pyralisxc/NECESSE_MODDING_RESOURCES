/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.mapData;

import necesse.level.maps.mapData.ClientDiscoveredMap;
import necesse.level.maps.mapData.ClientDiscoveredMapRegion;
import necesse.level.maps.regionSystem.RegionBoundsExecutorAbstract;

public class DiscoveredMapBoundsExecutor
extends RegionBoundsExecutorAbstract<ClientDiscoveredMapRegion> {
    private final ClientDiscoveredMap map;
    private final boolean createIfDoesntExist;
    private final boolean onlyIfFileExists;

    public DiscoveredMapBoundsExecutor(ClientDiscoveredMap map, int startTileX, int startTileY, int endTileX, int endTileY, boolean createIfDoesntExist, boolean onlyIfFileExists) {
        super(startTileX, startTileY, endTileX, endTileY, 8);
        this.map = map;
        this.createIfDoesntExist = createIfDoesntExist;
        this.onlyIfFileExists = onlyIfFileExists;
    }

    @Override
    protected RegionBoundsExecutorAbstract.RegionBounds<ClientDiscoveredMapRegion> getRegionBounds(int regionX, int regionY) {
        ClientDiscoveredMapRegion region = this.map.getRegion(regionX, regionY, this.createIfDoesntExist, this.onlyIfFileExists);
        if (region == null) {
            return null;
        }
        int regionStartTileX = Math.max(0, this.startTileX - region.tileXOffset);
        int regionStartTileY = Math.max(0, this.startTileY - region.tileYOffset);
        int regionEndTileX = Math.min(region.getTileWidth() - 1, this.endTileX - region.tileXOffset);
        int regionEndTileY = Math.min(region.getTileHeight() - 1, this.endTileY - region.tileYOffset);
        return new RegionBoundsExecutorAbstract.RegionBounds<ClientDiscoveredMapRegion>(region, regionX, regionY, regionStartTileX, regionStartTileY, regionEndTileX, regionEndTileY);
    }

    @Override
    protected int getRegionTileXOffset(ClientDiscoveredMapRegion region) {
        return region.tileXOffset;
    }

    @Override
    protected int getRegionTileYOffset(ClientDiscoveredMapRegion region) {
        return region.tileYOffset;
    }
}

