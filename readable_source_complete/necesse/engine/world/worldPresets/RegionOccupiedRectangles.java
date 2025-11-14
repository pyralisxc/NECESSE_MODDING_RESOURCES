/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.IntStream;
import necesse.engine.util.GameMath;
import necesse.engine.util.PointHashMap;

public class RegionOccupiedRectangles {
    private final PointHashMap<LinkedList<Rectangle>> regionOccupiedRectangles = new PointHashMap();

    public void add(Rectangle tileRectangle) {
        for (Point occupiedRegion : this.getOccupiedRegions(tileRectangle)) {
            LinkedList list = this.regionOccupiedRectangles.compute(occupiedRegion.x, occupiedRegion.y, (x, y, lastValue) -> {
                if (lastValue == null) {
                    lastValue = new LinkedList();
                }
                return lastValue;
            });
            list.add(tileRectangle);
        }
    }

    public boolean isOccupied(Rectangle tileRectangle) {
        for (Point occupiedRegion : this.getOccupiedRegions(tileRectangle)) {
            LinkedList<Rectangle> list = this.regionOccupiedRectangles.get(occupiedRegion.x, occupiedRegion.y);
            if (list == null) continue;
            for (Rectangle occupiedRectangle : list) {
                if (!occupiedRectangle.intersects(tileRectangle)) continue;
                return true;
            }
        }
        return false;
    }

    protected Iterable<Point> getOccupiedRegions(Rectangle tileRectangle) {
        int startRegionX = GameMath.getRegionCoordByTile(tileRectangle.x);
        int startRegionY = GameMath.getRegionCoordByTile(tileRectangle.y);
        int endRegionX = GameMath.getRegionCoordByTile(tileRectangle.x + tileRectangle.width - 1);
        int endRegionY = GameMath.getRegionCoordByTile(tileRectangle.y + tileRectangle.height - 1);
        Iterator iterator = IntStream.range(startRegionX, endRegionX + 1).boxed().flatMap(regionX -> IntStream.range(startRegionY, endRegionY + 1).mapToObj(regionY -> new Point((int)regionX, regionY))).iterator();
        return () -> iterator;
    }
}

