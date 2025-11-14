/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import java.util.ArrayList;
import necesse.entity.Entity;
import necesse.entity.manager.RegionTrackerList;
import necesse.level.maps.Level;

public class EntityRegionList<T extends Entity>
extends RegionTrackerList<T> {
    public EntityRegionList(Level level) {
        super(level);
    }

    @Override
    public ArrayList<T> getInRegionByTileRange(int tileX, int tileY, int tileRange) {
        ArrayList list = super.getInRegionByTileRange(tileX, tileY, tileRange);
        list.removeIf(entity -> {
            int eTileX = entity.getTileX();
            int eTileY = entity.getTileY();
            return eTileX < tileX - tileRange || eTileX > tileX + tileRange || eTileY < tileY - tileRange || eTileY > tileY + tileRange;
        });
        return list;
    }
}

