/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import necesse.engine.util.GameMath;

public abstract class RegionBoundsExecutorAbstract<T> {
    public final int startTileX;
    public final int startTileY;
    public final int endTileX;
    public final int endTileY;
    protected final int regionSizeBits;
    public final int regionStartX;
    public final int regionStartY;
    public final int regionEndX;
    public final int regionEndY;
    private LinkedList<RegionBounds<T>> regionsList;
    private RegionBounds<T>[][] regionsArray;

    public RegionBoundsExecutorAbstract(int startTileX, int startTileY, int endTileX, int endTileY, int regionSizeBits) {
        this.startTileX = startTileX;
        this.startTileY = startTileY;
        this.endTileX = endTileX;
        this.endTileY = endTileY;
        this.regionStartX = GameMath.divideByPowerOf2RoundedDown(startTileX, regionSizeBits);
        this.regionStartY = GameMath.divideByPowerOf2RoundedDown(startTileY, regionSizeBits);
        this.regionEndX = GameMath.divideByPowerOf2RoundedDown(endTileX, regionSizeBits);
        this.regionEndY = GameMath.divideByPowerOf2RoundedDown(endTileY, regionSizeBits);
        this.regionSizeBits = regionSizeBits;
    }

    protected abstract RegionBounds<T> getRegionBounds(int var1, int var2);

    protected abstract int getRegionTileXOffset(T var1);

    protected abstract int getRegionTileYOffset(T var1);

    private void calculateListIfNecessary() {
        block6: {
            if (this.regionsList != null) break block6;
            this.regionsList = new LinkedList();
            if (this.regionsArray != null) {
                RegionBounds<T>[][] regionBoundsArray = this.regionsArray;
                int n = regionBoundsArray.length;
                for (int i = 0; i < n; ++i) {
                    RegionBounds<T>[] innerArray;
                    for (RegionBounds<T> bounds : innerArray = regionBoundsArray[i]) {
                        if (bounds == null) continue;
                        this.regionsList.add(bounds);
                    }
                }
            } else {
                for (int x = this.regionStartX; x <= this.regionEndX; ++x) {
                    for (int y = this.regionStartY; y <= this.regionEndY; ++y) {
                        RegionBounds<T> bounds = this.getRegionBounds(x, y);
                        if (bounds == null) continue;
                        this.regionsList.add(bounds);
                    }
                }
            }
        }
    }

    private void calculateArrayIfNecessary() {
        block5: {
            if (this.regionsArray != null) break block5;
            this.regionsArray = new RegionBounds[this.regionEndX - this.regionStartX + 1][this.regionEndY - this.regionStartY + 1];
            if (this.regionsList != null) {
                Iterator iterator = this.regionsList.iterator();
                while (iterator.hasNext()) {
                    RegionBounds bounds;
                    this.regionsArray[bounds.regionX - this.regionStartX][bounds.regionY - this.regionStartY] = bounds = (RegionBounds)iterator.next();
                }
            } else {
                for (int x = this.regionStartX; x <= this.regionEndX; ++x) {
                    for (int y = this.regionStartY; y <= this.regionEndY; ++y) {
                        RegionBounds<T> bounds = this.getRegionBounds(x, y);
                        if (bounds == null) continue;
                        this.regionsArray[x - this.regionStartX][y - this.regionStartY] = bounds;
                    }
                }
            }
        }
    }

    public void runBounds(BoundsExecutor<T> executor) {
        this.calculateListIfNecessary();
        for (RegionBounds regionBounds : this.regionsList) {
            executor.run(regionBounds.region, regionBounds.regionStartTileX, regionBounds.regionStartTileY, regionBounds.regionEndTileX, regionBounds.regionEndTileY);
        }
    }

    public void runDimensions(DimensionsExecutor<T> executor) {
        this.calculateListIfNecessary();
        for (RegionBounds regionBounds : this.regionsList) {
            executor.run(regionBounds.region, regionBounds.regionStartTileX, regionBounds.regionStartTileY, regionBounds.regionEndTileX - regionBounds.regionStartTileX + 1, regionBounds.regionEndTileY - regionBounds.regionStartTileY + 1);
        }
    }

    public void runCoordinates(CoordinateExecutor<T> executor) {
        this.calculateListIfNecessary();
        for (RegionBounds regionBounds : this.regionsList) {
            for (int x = regionBounds.regionStartTileX; x <= regionBounds.regionEndTileX; ++x) {
                for (int y = regionBounds.regionStartTileY; y <= regionBounds.regionEndTileY; ++y) {
                    executor.run(regionBounds.region, x, y);
                }
            }
        }
    }

    public Stream<RegionTilePosition<T>> streamCoordinates() {
        this.calculateListIfNecessary();
        return this.regionsList.stream().flatMap(bounds -> IntStream.range(bounds.regionStartTileX, bounds.regionEndTileX + 1).boxed().flatMap(regionTileX -> IntStream.range(bounds.regionStartTileY, bounds.regionEndTileY + 1).mapToObj(regionTileY -> new RegionTilePosition(bounds.region, (int)regionTileX, regionTileY))));
    }

    public void runOnBounds(int startTileX, int startTileY, int endTileX, int endTileY, CoordinateExecutor<T> executor) {
        this.calculateArrayIfNecessary();
        for (int tileX = startTileX; tileX <= endTileX; ++tileX) {
            for (int tileY = startTileY; tileY <= endTileY; ++tileY) {
                int boundsRegionY;
                int boundsRegionX = GameMath.divideByPowerOf2RoundedDown(tileX, this.regionSizeBits) - this.regionStartX;
                RegionBounds<T> bounds = this.regionsArray[boundsRegionX][boundsRegionY = GameMath.divideByPowerOf2RoundedDown(tileY, this.regionSizeBits) - this.regionStartY];
                if (bounds == null) continue;
                executor.run(bounds.region, tileX - this.getRegionTileXOffset(bounds.region), tileY - this.getRegionTileYOffset(bounds.region));
            }
        }
    }

    public void runOnTiles(Iterable<Point> tiles, boolean acceptOutOfTileBounds, CoordinateExecutor<T> executor) {
        this.calculateArrayIfNecessary();
        for (Point tile : tiles) {
            int boundsRegionX = GameMath.divideByPowerOf2RoundedDown(tile.x, this.regionSizeBits) - this.regionStartX;
            int boundsRegionY = GameMath.divideByPowerOf2RoundedDown(tile.y, this.regionSizeBits) - this.regionStartY;
            if (boundsRegionX < 0 || boundsRegionX >= this.regionsArray.length || boundsRegionY < 0 || boundsRegionY >= this.regionsArray[boundsRegionX].length) {
                if (acceptOutOfTileBounds) continue;
                throw new IllegalArgumentException("Tile coordinates out of bounds " + boundsRegionX + "x" + boundsRegionY);
            }
            RegionBounds<T> bounds = this.regionsArray[boundsRegionX][boundsRegionY];
            if (bounds == null) continue;
            executor.run(bounds.region, tile.x - this.getRegionTileXOffset(bounds.region), tile.y - this.getRegionTileYOffset(bounds.region));
        }
    }

    public void runOnTiles(Iterable<Point> tiles, CoordinateExecutor<T> executor) {
        this.runOnTiles(tiles, false, executor);
    }

    public boolean runOnTile(int tileX, int tileY, CoordinateExecutor<T> executor) {
        this.calculateArrayIfNecessary();
        int boundsRegionX = GameMath.divideByPowerOf2RoundedDown(tileX, this.regionSizeBits) - this.regionStartX;
        int boundsRegionY = GameMath.divideByPowerOf2RoundedDown(tileY, this.regionSizeBits) - this.regionStartY;
        if (boundsRegionX < 0 || boundsRegionX >= this.regionsArray.length || boundsRegionY < 0 || boundsRegionY >= this.regionsArray[boundsRegionX].length) {
            throw new IllegalArgumentException("Tile coordinates out of bounds " + boundsRegionX + "x" + boundsRegionY);
        }
        RegionBounds<T> bounds = this.regionsArray[boundsRegionX][boundsRegionY];
        if (bounds == null) {
            return false;
        }
        executor.run(bounds.region, tileX - this.getRegionTileXOffset(bounds.region), tileY - this.getRegionTileYOffset(bounds.region));
        return true;
    }

    public <R> R getOnTile(int tileX, int tileY, CoordinateGetter<T, R> getter, R defaultValue) {
        this.calculateArrayIfNecessary();
        int boundsRegionX = GameMath.divideByPowerOf2RoundedDown(tileX, this.regionSizeBits) - this.regionStartX;
        int boundsRegionY = GameMath.divideByPowerOf2RoundedDown(tileY, this.regionSizeBits) - this.regionStartY;
        if (boundsRegionX < 0 || boundsRegionX >= this.regionsArray.length || boundsRegionY < 0 || boundsRegionY >= this.regionsArray[boundsRegionX].length) {
            throw new IllegalArgumentException("Tile coordinates out of bounds: " + boundsRegionX + "x" + boundsRegionY);
        }
        RegionBounds<T> bounds = this.regionsArray[boundsRegionX][boundsRegionY];
        if (bounds == null) {
            return defaultValue;
        }
        return getter.get(bounds.region, tileX - this.getRegionTileXOffset(bounds.region), tileY - this.getRegionTileYOffset(bounds.region));
    }

    public boolean isTileLoaded(int tileX, int tileY) {
        this.calculateArrayIfNecessary();
        int boundsRegionX = GameMath.divideByPowerOf2RoundedDown(tileX, this.regionSizeBits) - this.regionStartX;
        int boundsRegionY = GameMath.divideByPowerOf2RoundedDown(tileY, this.regionSizeBits) - this.regionStartY;
        if (boundsRegionX < 0 || boundsRegionX >= this.regionsArray.length || boundsRegionY < 0 || boundsRegionY >= this.regionsArray[boundsRegionX].length) {
            throw new IllegalArgumentException("Tile coordinates out of bounds " + boundsRegionX + "x" + boundsRegionY);
        }
        RegionBounds<T> bounds = this.regionsArray[boundsRegionX][boundsRegionY];
        return bounds != null;
    }

    public boolean isInsideBounds(int tileX, int tileY) {
        return tileX >= this.startTileX && tileX <= this.endTileX && tileY >= this.startTileY && tileY <= this.endTileY;
    }

    public T getRegionByTile(int tileX, int tileY) {
        return this.getRegion(GameMath.divideByPowerOf2RoundedDown(tileX, this.regionSizeBits), GameMath.divideByPowerOf2RoundedDown(tileY, this.regionSizeBits));
    }

    public T getRegion(int regionTileX, int regionTileY) {
        this.calculateArrayIfNecessary();
        int boundsRegionX = regionTileX - this.regionStartX;
        int boundsRegionY = regionTileY - this.regionStartY;
        if (boundsRegionX < 0 || boundsRegionX >= this.regionsArray.length || boundsRegionY < 0 || boundsRegionY >= this.regionsArray[boundsRegionX].length) {
            throw new IllegalArgumentException("Tile coordinates out of bounds " + boundsRegionX + "x" + boundsRegionY);
        }
        RegionBounds<T> bounds = this.regionsArray[boundsRegionX][boundsRegionY];
        return bounds == null ? null : (T)bounds.region;
    }

    public void calculateAllRegions() {
        this.calculateArrayIfNecessary();
    }

    protected static class RegionBounds<T> {
        public final T region;
        public final int regionX;
        public final int regionY;
        public final int regionStartTileX;
        public final int regionStartTileY;
        public final int regionEndTileX;
        public final int regionEndTileY;

        public RegionBounds(T region, int regionX, int regionY, int regionStartTileX, int regionStartTileY, int regionEndTileX, int regionEndTileY) {
            this.region = region;
            this.regionX = regionX;
            this.regionY = regionY;
            this.regionStartTileX = regionStartTileX;
            this.regionStartTileY = regionStartTileY;
            this.regionEndTileX = regionEndTileX;
            this.regionEndTileY = regionEndTileY;
        }
    }

    @FunctionalInterface
    public static interface BoundsExecutor<T> {
        public void run(T var1, int var2, int var3, int var4, int var5);
    }

    @FunctionalInterface
    public static interface DimensionsExecutor<T> {
        public void run(T var1, int var2, int var3, int var4, int var5);
    }

    @FunctionalInterface
    public static interface CoordinateExecutor<T> {
        public void run(T var1, int var2, int var3);
    }

    @FunctionalInterface
    public static interface CoordinateGetter<T, R> {
        public R get(T var1, int var2, int var3);
    }

    public static class RegionTilePosition<T> {
        public final T region;
        public final int regionTileX;
        public final int regionTileY;

        public RegionTilePosition(T region, int regionTileX, int regionTileY) {
            this.region = region;
            this.regionTileX = regionTileX;
            this.regionTileY = regionTileY;
        }
    }
}

