/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import java.util.ArrayList;
import necesse.entity.TileEntity;
import necesse.entity.manager.RegionTrackerList;
import necesse.level.maps.Level;

public class TileEntityRegionList<T extends TileEntity>
extends RegionTrackerList<T> {
    public TileEntityRegionList(Level level) {
        super(level);
    }

    @Override
    public ArrayList<T> getInRegionByTileRange(int tileX, int tileY, int tileRange) {
        ArrayList list = super.getInRegionByTileRange(tileX, tileY, tileRange);
        list.removeIf(entity -> {
            int eTileX = entity.tileX;
            int eTileY = entity.tileY;
            return eTileX < tileX - tileRange || eTileX > tileX + tileRange || eTileY < tileY - tileRange || eTileY > tileY + tileRange;
        });
        return list;
    }
}

