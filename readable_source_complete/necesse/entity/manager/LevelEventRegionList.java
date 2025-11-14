/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Stream;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.RegionTrackerList;
import necesse.level.maps.Level;

public class LevelEventRegionList<T extends LevelEvent>
extends RegionTrackerList<T> {
    public LevelEventRegionList(Level level) {
        super(level);
    }

    @Override
    public Stream<T> streamInRegionsShape(Shape shape, int extraRegionRange) {
        return super.streamInRegionsShape(shape, extraRegionRange).distinct();
    }

    @Override
    public GameAreaStream<T> streamAreaTileRange(int x, int y, int tileRange) {
        return super.streamAreaTileRange(x, y, tileRange).distinct();
    }

    @Override
    public ArrayList<T> getInRegionRange(int regionX, int regionY, int regionRange) {
        HashSet set = new HashSet();
        for (int x = regionX - regionRange; x <= regionX + regionRange; ++x) {
            if (!this.level.regionManager.isRegionXWithinBounds(x)) continue;
            for (int y = regionY - regionRange; y <= regionY + regionRange; ++y) {
                if (!this.level.regionManager.isRegionYWithinBounds(y)) continue;
                set.addAll(this.getInRegion(x, y));
            }
        }
        return new ArrayList(set);
    }
}

