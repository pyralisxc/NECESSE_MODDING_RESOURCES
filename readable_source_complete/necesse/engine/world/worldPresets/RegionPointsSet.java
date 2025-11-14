/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.stream.Stream;
import necesse.engine.util.GameMath;
import necesse.engine.util.PointHashSet;

public class RegionPointsSet
implements Iterable<Point> {
    private final PointHashSet regions = new PointHashSet();

    public void addTile(int tileX, int tileY) {
        this.addRegion(GameMath.getRegionCoordByTile(tileX), GameMath.getRegionCoordByTile(tileY));
    }

    public void addTileRectangle(Rectangle tileRectangle) {
        int startRegionX = GameMath.divideByPowerOf2RoundedDown(tileRectangle.x, 4);
        int startRegionY = GameMath.divideByPowerOf2RoundedDown(tileRectangle.y, 4);
        int endRegionX = GameMath.divideByPowerOf2RoundedDown(tileRectangle.x + tileRectangle.width - 1, 4);
        int endRegionY = GameMath.divideByPowerOf2RoundedDown(tileRectangle.y + tileRectangle.height - 1, 4);
        for (int regionX = startRegionX; regionX <= endRegionX; ++regionX) {
            for (int regionY = startRegionY; regionY <= endRegionY; ++regionY) {
                this.addRegion(regionX, regionY);
            }
        }
    }

    public void addTileBounds(int minTileX, int minTileY, int maxTileX, int maxTileY) {
        int startRegionX = GameMath.divideByPowerOf2RoundedDown(minTileX, 4);
        int startRegionY = GameMath.divideByPowerOf2RoundedDown(minTileY, 4);
        int endRegionX = GameMath.divideByPowerOf2RoundedDown(maxTileX, 4);
        int endRegionY = GameMath.divideByPowerOf2RoundedDown(maxTileY, 4);
        for (int regionX = startRegionX; regionX <= endRegionX; ++regionX) {
            for (int regionY = startRegionY; regionY <= endRegionY; ++regionY) {
                this.addRegion(regionX, regionY);
            }
        }
    }

    public void addRegion(int regionX, int regionY) {
        this.regions.add(regionX, regionY);
    }

    public Iterable<Point> getRegions() {
        return this.regions;
    }

    public Stream<Point> streamRegions() {
        return this.regions.stream();
    }

    public Iterable<Long> getKeys() {
        return this.regions.getKeys();
    }

    @Override
    public Iterator<Point> iterator() {
        return this.regions.iterator();
    }
}

